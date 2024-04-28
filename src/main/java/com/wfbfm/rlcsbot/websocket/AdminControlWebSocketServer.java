package com.wfbfm.rlcsbot.websocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wfbfm.rlcsbot.app.RlcsBotApplication;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminControlWebSocketServer extends WebSocketServer
{
    private static final String COMMAND = "command";
    private final Logger logger = Logger.getLogger(AdminControlWebSocketServer.class.getName());
    private final RlcsBotApplication application;

    public AdminControlWebSocketServer(final int port, final RlcsBotApplication application)
    {
        super(new InetSocketAddress(port));
        this.application = application;
    }

    @Override
    public void onOpen(final WebSocket webSocket, final ClientHandshake clientHandshake)
    {
        logger.log(Level.INFO, "Connection opened: " + webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onClose(final WebSocket webSocket, final int code, final String reason, final boolean remote)
    {
        logger.log(Level.INFO, "Connection closed: " + webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(final WebSocket webSocket, final String message)
    {
        logger.log(Level.INFO, "Parsing command from client: " + message);
        final JsonObject commandJson = parseJson(message);
        if (commandJson == null)
        {
            return;
        }
        final AdminCommand adminCommand = parseAdminCommand(commandJson);

        switch (adminCommand)
        {
            case INVALID:
                broadcast(String.format("Not a valid command: %s", message));
                logger.log(Level.INFO, "Received invalid command from client: " + message);
                break;
            case DISPLAY_NAME:
                if (!handleDisplayName(commandJson))
                {
                    logger.log(Level.WARNING, "Received command with missing params");
                }
                break;
            case LIQUIPEDIA_URL:
                if (!handleLiquipediaUrl(commandJson))
                {
                    logger.log(Level.WARNING, "Received command with missing params");
                }
                break;
            case BROADCAST_URL:
                if (!handleBroadcastUrl(commandJson))
                {
                    logger.log(Level.WARNING, "Received command with missing params");
                }
                break;
            case START_BROADCAST:
                handleStartBroadcast();
                break;
            case STOP_BROADCAST:
                handleStopBroadcast();
                break;
            case DELETE_SERIES:
            case DELETE_SERIES_EVENT:
            case UPDATE_SERIES:
            case UPDATE_SERIES_EVENT:
                // FIXME: handle these commands in future
                broadcast(String.format("Unhandled command: %s", message));
                logger.log(Level.INFO, "Received unhandled command from client: " + message);
                break;
            default:
                broadcast(String.format("Unhandled command: %s", message));
                logger.log(Level.INFO, "Received unhandled command from client: " + message);
                break;
        }
    }

    private JsonObject parseJson(final String message)
    {
        final JsonElement jsonElement = JsonParser.parseString(message);
        if (jsonElement == null || !jsonElement.isJsonObject())
        {
            broadcast(String.format("Not a valid command: %s", message));
            logger.log(Level.INFO, "Received invalid command from client: " + message);
            return null;
        }

        return jsonElement.getAsJsonObject();
    }

    private AdminCommand parseAdminCommand(final JsonObject commandJson)
    {
        if (!commandJson.has(COMMAND))
        {
            return AdminCommand.INVALID;
        }
        final String command = commandJson.get(COMMAND).getAsString();
        try
        {
            final AdminCommand adminCommand = AdminCommand.valueOf(command);
            return adminCommand;
        }
        catch (Exception e)
        {
            return AdminCommand.INVALID;
        }
    }

    private boolean handleDisplayName(final JsonObject commandJson)
    {
        if (commandJson.has("displayName") && commandJson.has("liquipediaName"))
        {
            final String displayName = commandJson.get("displayName").getAsString();
            final String liquipediaName = commandJson.get("liquipediaName").getAsString();
            broadcast(String.format("Added display name mapping to cache: %s - %s", displayName, liquipediaName));
            this.application.addDisplayNameMapping(displayName, liquipediaName);
            return true;
        }
        else
        {
            broadcast("Required parameters: displayName, liquipediaName");
            return false;
        }
    }

    private boolean handleLiquipediaUrl(final JsonObject commandJson)
    {
        if (commandJson.has("liquipediaUrl"))
        {
            final String liquipediaUrl = commandJson.get("liquipediaUrl").getAsString();
            this.application.updateLiquipediaUrl(liquipediaUrl);
            broadcast(String.format("Updated Liquipedia URL: " + liquipediaUrl));
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean handleBroadcastUrl(final JsonObject commandJson)
    {
        if (commandJson.has("broadcastUrl"))
        {
            final String broadcastUrl = commandJson.get("broadcastUrl").getAsString();
            this.application.updateBroadcastUrl(broadcastUrl);
            broadcast(String.format("Updated Broadcast URL: " + broadcastUrl));
            return true;
        }
        else
        {
            return false;
        }
    }

    private void handleStartBroadcast()
    {
        broadcast("Broadcast starting...");
        this.application.startBroadcast();
        broadcast("Broadcast started!");
    }

    private void handleStopBroadcast()
    {
        this.application.stopBroadcast();
        broadcast("Broadcast stopped!");
    }

    @Override
    public void onError(final WebSocket webSocket, final Exception e)
    {
        e.printStackTrace();
    }

    @Override
    public void onStart()
    {
        logger.log(Level.INFO, "Admin server is running on secret port");
    }
}
