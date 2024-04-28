package com.wfbfm.rlcsbot.websocket;

import com.wfbfm.rlcsbot.app.RlcsBotApplication;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class AdminControlWebSocketServerTest
{
    @Test
    public void runWebSocketServer()
    {
        final int port = 8880;
        final RlcsBotApplication rlcsBotApplication = mock(RlcsBotApplication.class);
        final AdminControlWebSocketServer server = new AdminControlWebSocketServer(port, rlcsBotApplication);
        server.start();

        while (true)
        {

        }
    }
}