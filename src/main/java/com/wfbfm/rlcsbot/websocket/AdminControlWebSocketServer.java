package com.wfbfm.rlcsbot.websocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wfbfm.rlcsbot.app.RlcsBotApplication;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.SSL_ENABLED;

public class AdminControlWebSocketServer extends WebSocketServer
{
    private static final String COMMAND = "command";
    private final Logger logger = Logger.getLogger(AdminControlWebSocketServer.class.getName());
    private final RlcsBotApplication application;

    public AdminControlWebSocketServer(final int port, final RlcsBotApplication application)
    {
        super(new InetSocketAddress(port));
        this.application = application;
        if (SSL_ENABLED)
        {
            final SSLContext sslContext = SslContextProvider.getSslContext();
            if (sslContext == null)
            {
                logger.log(Level.SEVERE, "Unable to set up SSL");
            }
            else
            {
                setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
            }
        }
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
            case ALLOW_MIDSERIES:
                if (!handleAllowMidSeries(commandJson))
                {
                    logger.log(Level.WARNING, "Received command with missing params");
                }
                break;
            case FLUSH_WEBSOCKET:
                handleFlushWebSocket();
                break;
            case SAMPLING_RATE:
                if (!handleSamplingRate(commandJson))
                {
                    logger.log(Level.WARNING, "Received command with missing params");
                }
                break;
            case TRANSCRIPTION_WAIT:
                if (!handleTranscriptionWait(commandJson))
                {
                    logger.log(Level.WARNING, "Received command with missing params");
                }
                break;
            case BEST_OF:
                if (!handleBestOf(commandJson))
                {
                    logger.log(Level.WARNING, "Received command with missing params");
                }
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

    private void handleFlushWebSocket()
    {
        this.application.getApplicationContext().setFlushWebSocket(true);
        broadcast("Flushing websocket...");
    }

    private boolean handleSamplingRate(final JsonObject commandJson)
    {
        if (commandJson.has("samplingRate"))
        {
            final int samplingRate = commandJson.get("samplingRate").getAsInt();
            this.application.getApplicationContext().setSamplingRateMs(samplingRate);
            broadcast(String.format("Updated sampling rate: " + samplingRate));
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean handleTranscriptionWait(final JsonObject commandJson)
    {
        if (commandJson.has("transcriptionWait"))
        {
            final int transcriptionWait = commandJson.get("transcriptionWait").getAsInt();
            this.application.getApplicationContext().setTranscriptionWaitMs(transcriptionWait);
            broadcast(String.format("Updated transcription wait: " + transcriptionWait));
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean handleBestOf(final JsonObject commandJson)
    {
        if (commandJson.has("bestOf"))
        {
            final int bestOf = commandJson.get("bestOf").getAsInt();
            this.application.getApplicationContext().setBestOf(bestOf);
            broadcast(String.format("Updated bestOf: " + bestOf));
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean handleAllowMidSeries(final JsonObject commandJson)
    {
        if (commandJson.has("allowMidSeries"))
        {
            this.application.updateMidSeries(Boolean.parseBoolean(commandJson.get("allowMidSeries").getAsString()));
            broadcast("Midseries allowed: " + this.application.getApplicationContext().isMidSeriesAllowed());
            return true;
        }
        else
        {
            return false;
        }
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
