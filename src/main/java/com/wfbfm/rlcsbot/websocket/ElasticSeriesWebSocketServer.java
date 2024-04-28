package com.wfbfm.rlcsbot.websocket;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonpUtils;
import com.wfbfm.rlcsbot.elastic.ElasticSearchClientBuilder;
import com.wfbfm.rlcsbot.series.Series;
import com.wfbfm.rlcsbot.series.SeriesEvent;
import org.apache.commons.io.FileUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.*;

public class ElasticSeriesWebSocketServer extends WebSocketServer
{
    private static final String BROADCAST_JSON_TEMPLATE = "{\"payloadType\": \"rlcs_data\", \"payload\": %s}";
    private static final String IMAGE_JSON_TEMPLATE = "{\"payloadType\": \"image\", \"imageName\": \"%s\", \"base64Image\": \"%s\"}";
    private static final String BASE_64_TEMPLATE = "data:image/png;base64,";
    private final Base64.Encoder base64Encoder = Base64.getEncoder();
    private final Logger logger = Logger.getLogger(ElasticSeriesWebSocketServer.class.getName());
    private final ElasticsearchClient client = ElasticSearchClientBuilder.getElasticsearchClient();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Semaphore semaphore = new Semaphore(1);
    private final Map<String, String> allBroadcastSeries = new HashMap<>();
    private final Map<String, String> allBroadcastSeriesEvents = new HashMap<>();
    private final Map<String, String> allTeamLogos = new HashMap<>();

    public ElasticSeriesWebSocketServer(final int port)
    {
        super(new InetSocketAddress(port));

        startPollingForNewDocuments();
    }

    public void startPollingForNewDocuments()
    {
        scheduler.scheduleAtFixedRate(() ->
        {
            try
            {
                semaphore.acquire();
                pollAndBroadcastNewDocuments();
            } catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            } finally
            {
                semaphore.release();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void onOpen(final WebSocket webSocket, final ClientHandshake clientHandshake)
    {
        logger.log(Level.INFO, String.format("New client connection opened: %s - broadcasting all %d series and %d seriesEvents",
                webSocket.getRemoteSocketAddress(), allBroadcastSeries.size(), allBroadcastSeriesEvents.size()));
        allBroadcastSeries.forEach((seriesId, seriesJson) -> webSocket.send(String.format(BROADCAST_JSON_TEMPLATE, seriesJson)));
        allBroadcastSeriesEvents.forEach((seriesId, seriesEventJson) -> webSocket.send(String.format(BROADCAST_JSON_TEMPLATE, seriesEventJson)));
        logger.log(Level.INFO, String.format("Broadcasting %d team logos", allTeamLogos.size()));
        allTeamLogos.forEach((fileName, base64String) -> broadcast(String.format(IMAGE_JSON_TEMPLATE, fileName, base64String)));
    }

    @Override
    public void onClose(final WebSocket webSocket, final int code, final String reason, final boolean remote)
    {
        logger.log(Level.INFO, "Connection closed: " + webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(final WebSocket webSocket, final String message)
    {
        logger.log(Level.INFO, "Ignored message from client: " + message);
    }

    @Override
    public void onError(final WebSocket webSocket, final Exception e)
    {
        e.printStackTrace();
    }

    @Override
    public void onStart()
    {
        logger.log(Level.INFO, "WebSocketServer initialised - broadcasting all documents");
        allBroadcastSeries.forEach((seriesId, seriesJson) -> broadcast(String.format(BROADCAST_JSON_TEMPLATE, seriesJson)));
        allBroadcastSeriesEvents.forEach((seriesId, seriesEventJson) -> broadcast(String.format(BROADCAST_JSON_TEMPLATE, seriesEventJson)));
        allTeamLogos.forEach((fileName, base64String) -> broadcast(String.format(IMAGE_JSON_TEMPLATE, fileName, base64String)));
    }

    private void pollAndBroadcastNewDocuments()
    {
        logger.info("Polling for new Elastic documents.");
        try
        {
            broadcastLatestDocuments(ELASTIC_INDEX_SERIES, allBroadcastSeries, Series.class);
            broadcastLatestDocuments(ELASTIC_INDEX_SERIES_EVENT, allBroadcastSeriesEvents, SeriesEvent.class);
            broadcastLatestLogos();
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Error broadcasting over websocket", e);
            throw new RuntimeException(e);
        }
    }

    private void broadcastLatestDocuments(final String indexName, final Map<String, String> documentMap, final Class<?> objectType) throws IOException
    {
        final SearchResponse<?> response = client.search(s -> s.index(indexName).size(1000), objectType);

        for (final Hit<?> hit : response.hits().hits())
        {
            final String documentId = hit.id();
            final String documentJson = JsonpUtils.toJsonString(hit, client._jsonpMapper());
            final String cachedDocumentJson = documentMap.get(documentId);
            if (cachedDocumentJson == null || !cachedDocumentJson.equals(documentJson))
            {
                documentMap.put(documentId, documentJson);
                logger.info("Found new document - broadcasting to all clients: " + documentId);
                broadcast(String.format(BROADCAST_JSON_TEMPLATE, documentJson));
            }
        }
    }

    private void broadcastLatestLogos() throws IOException
    {
        final File[] logoFiles = LOGO_DIRECTORY.listFiles();
        if (logoFiles != null)
        {
            for (final File logoFile : logoFiles)
            {
                final String fileName = logoFile.getName();
                if (!allTeamLogos.containsKey(fileName))
                {
                    final String encodedImage = encodeImageToBase64(logoFile);
                    logger.info("Found new logo - broadcasting to all clients: " + fileName);
                    allTeamLogos.put(logoFile.getName(), encodedImage);
                    broadcast(String.format(IMAGE_JSON_TEMPLATE, fileName, encodedImage));
                }
            }
        }
    }

    private String encodeImageToBase64(final File imageFile) throws IOException
    {
        final byte[] fileContent = FileUtils.readFileToByteArray(imageFile);
        final String base64String = base64Encoder.encodeToString(fileContent);
        return BASE_64_TEMPLATE + base64String;
    }

}
