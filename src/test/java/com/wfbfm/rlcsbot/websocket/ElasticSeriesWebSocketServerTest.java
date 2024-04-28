package com.wfbfm.rlcsbot.websocket;

import com.wfbfm.rlcsbot.app.ApplicationContext;
import org.junit.jupiter.api.Test;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.LIQUIPEDIA_PAGE;

public class ElasticSeriesWebSocketServerTest
{
    @Test
    public void runWebSocketServer()
    {
        final int port = 8887;
        final ApplicationContext applicationContext = new ApplicationContext("test", LIQUIPEDIA_PAGE, false);
        final ElasticSeriesWebSocketServer server = new ElasticSeriesWebSocketServer(port, applicationContext);
        server.start();

        while (true)
        {

        }
    }
}