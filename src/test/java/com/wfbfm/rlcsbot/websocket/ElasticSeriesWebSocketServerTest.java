package com.wfbfm.rlcsbot.websocket;

import com.wfbfm.rlcsbot.app.ApplicationContext;
import org.junit.jupiter.api.Test;

public class ElasticSeriesWebSocketServerTest
{
    @Test
    public void runWebSocketServer()
    {
        final int port = 8887;
        final ApplicationContext applicationContext = new ApplicationContext("test", "https://liquipedia.net/rocketleague/Rocket_League_Championship_Series/2024/Major_2/Europe/Open_Qualifier_4", false);
        final ElasticSeriesWebSocketServer server = new ElasticSeriesWebSocketServer(port, applicationContext);
        server.start();

        while (true)
        {

        }
    }
}