package com.wfbfm.rlcsbot.websocket;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonpUtils;
import com.wfbfm.rlcsbot.elastic.ElasticSearchClientBuilder;
import com.wfbfm.rlcsbot.series.SeriesEvent;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.ELASTIC_INDEX_SERIES_EVENT;

public class SeriesEventWebSocketServer extends WebSocketServer
{
    private final Logger logger = Logger.getLogger(SeriesEventWebSocketServer.class.getName());
    private final ElasticsearchClient client = ElasticSearchClientBuilder.getElasticsearchClient();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Semaphore semaphore = new Semaphore(1);
    private final Set<String> allBroadcastDocumentIds = new HashSet<>();
    private final Set<String> allBroadcastDocuments = new HashSet<>();

    public SeriesEventWebSocketServer(final int port)
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
        logger.log(Level.INFO, "Connection opened: " + webSocket.getRemoteSocketAddress() +
                " - broadcasting all " + allBroadcastDocuments.size() + " documents");
        allBroadcastDocuments.forEach(webSocket::send);
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
        logger.log(Level.INFO, "SeriesEventWebSocketServer initialised - broadcasting all documents");
        allBroadcastDocuments.forEach(this::broadcast);
    }

    private void pollAndBroadcastNewDocuments()
    {
        logger.info("Polling for new Elastic documents.");
        try
        {
            final SearchResponse<SeriesEvent> response = client.search(s -> s.index(ELASTIC_INDEX_SERIES_EVENT).size(1000), SeriesEvent.class);

            for (final Hit<SeriesEvent> hit : response.hits().hits())
            {
                final String documentId = hit.id();
                if (!allBroadcastDocumentIds.contains(documentId))
                {
                    logger.info("Found new document - broadcasting to all clients: " + documentId);
                    final String newDocumentAsJson = JsonpUtils.toJsonString(hit, client._jsonpMapper());
                    broadcast(newDocumentAsJson);
                    allBroadcastDocumentIds.add(documentId);
                    allBroadcastDocuments.add(newDocumentAsJson);
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
