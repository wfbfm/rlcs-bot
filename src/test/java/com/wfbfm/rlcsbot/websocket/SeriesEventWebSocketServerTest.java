package com.wfbfm.rlcsbot.websocket;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SeriesEventWebSocketServerTest
{
    @Test
    public void runWebSocketServer()
    {
        final int port = 8887;
        final SeriesEventWebSocketServer server = new SeriesEventWebSocketServer(port);
        server.start();

        while (true)
        {

        }
    }
}