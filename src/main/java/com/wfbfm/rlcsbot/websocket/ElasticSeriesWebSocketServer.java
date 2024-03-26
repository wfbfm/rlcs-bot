package com.wfbfm.rlcsbot.websocket;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonpUtils;
import com.wfbfm.rlcsbot.elastic.ElasticSearchClientBuilder;
import com.wfbfm.rlcsbot.series.Series;
import com.wfbfm.rlcsbot.series.SeriesEvent;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.ELASTIC_INDEX_SERIES;
import static com.wfbfm.rlcsbot.app.RuntimeConstants.ELASTIC_INDEX_SERIES_EVENT;

public class ElasticSeriesWebSocketServer extends WebSocketServer
{
    private final Logger logger = Logger.getLogger(ElasticSeriesWebSocketServer.class.getName());
    private final ElasticsearchClient client = ElasticSearchClientBuilder.getElasticsearchClient();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Semaphore semaphore = new Semaphore(1);
    private final Map<String, String> allBroadcastSeries = new HashMap<>();
    private final Map<String, String> allBroadcastSeriesEvents = new HashMap<>();

    public ElasticSeriesWebSocketServer(final int port)
    {
        super(new InetSocketAddress(port));
        startPollingForNewDocuments();
    }

    private void startPollingForNewDocuments()
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
        allBroadcastSeries.forEach((seriesId, seriesJson) -> {
            webSocket.send(seriesJson);
        });
        allBroadcastSeriesEvents.forEach((seriesId, seriesEventJson) -> {
            webSocket.send(seriesEventJson);
        });
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
        allBroadcastSeries.forEach((seriesId, seriesJson) -> broadcast(seriesJson));
        allBroadcastSeriesEvents.forEach((seriesId, seriesEventJson) -> broadcast(seriesEventJson));
    }

    private void pollAndBroadcastNewDocuments()
    {
        logger.info("Polling for new Elastic documents.");
        try
        {
//            broadcastLatestSeries();
//            broadcastLatestSeriesEvents();
            broadcastLatestDocuments(ELASTIC_INDEX_SERIES, allBroadcastSeries, Series.class);
            broadcastLatestDocuments(ELASTIC_INDEX_SERIES_EVENT, allBroadcastSeriesEvents, SeriesEvent.class);
        }
        catch (IOException e)
        {
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
                broadcast(documentJson);
            }
        }
    }

    private void broadcastLatestSeries() throws IOException
    {
        final SearchResponse<Series> response = client.search(s -> s.index(ELASTIC_INDEX_SERIES).size(1000), Series.class);

        for (final Hit<Series> hit : response.hits().hits())
        {
            final String seriesId = hit.id();
            final String seriesJson = JsonpUtils.toJsonString(hit, client._jsonpMapper());
            final String cachedSeriesJson = allBroadcastSeries.get(seriesId);
            if (cachedSeriesJson == null || !cachedSeriesJson.equals(seriesJson))
            {
                allBroadcastSeries.put(seriesId, seriesJson);
                broadcast(seriesJson);
            }
        }
    }

    private void broadcastLatestSeriesEvents() throws IOException
    {
        final SearchResponse<SeriesEvent> response = client.search(s -> s.index(ELASTIC_INDEX_SERIES_EVENT).size(1000), SeriesEvent.class);

        for (final Hit<SeriesEvent> hit : response.hits().hits())
        {
            final String seriesEventId = hit.id();
            final String seriesEventJson = JsonpUtils.toJsonString(hit, client._jsonpMapper());
            final String cachedSeriesEventJson = allBroadcastSeriesEvents.get(seriesEventId);
            if (cachedSeriesEventJson == null || !cachedSeriesEventJson.equals(seriesEventJson))
            {
                allBroadcastSeriesEvents.put(seriesEventId, seriesEventJson);
                broadcast(seriesEventJson);
                logger.info("Found new document - broadcasting to all clients: " + seriesEventId);
            }
        }
    }
}
