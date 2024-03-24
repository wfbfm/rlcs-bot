package com.wfbfm.rlcsbot.websocket;

import org.junit.jupiter.api.Test;

public class ElasticSeriesWebSocketServerTest
{
    @Test
    public void runWebSocketServer()
    {
        final int port = 8887;
        final ElasticSeriesWebSocketServer server = new ElasticSeriesWebSocketServer(port);
        server.start();

        while (true)
        {

        }
    }
}