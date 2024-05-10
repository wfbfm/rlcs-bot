package com.wfbfm.rlcsbot.app;

import com.wfbfm.rlcsbot.audiotranscriber.CommentaryRecorder;
import com.wfbfm.rlcsbot.audiotranscriber.TranscriptionPoller;
import com.wfbfm.rlcsbot.screenshotparser.GameScreenshotProcessor;
import com.wfbfm.rlcsbot.twitch.HeadlessTwitchWatcher;
import com.wfbfm.rlcsbot.websocket.AdminControlWebSocketServer;
import com.wfbfm.rlcsbot.websocket.ElasticSeriesWebSocketServer;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.*;

public class RlcsBotApplication
{
    private final ApplicationContext applicationContext = new ApplicationContext(BROADCAST_URL, LIQUIPEDIA_PAGE, true);
    private ExecutorService executorService;
    private HeadlessTwitchWatcher twitchWatcher;
    private TranscriptionPoller transcriptionPoller;
    private CommentaryRecorder commentaryRecorder;
    private GameScreenshotProcessor gameScreenshotProcessor;
    private ElasticSeriesWebSocketServer webSocketServer;
    private AdminControlWebSocketServer adminControlWebSocketServer;

    public void start()
    {
        executorService = Executors.newCachedThreadPool();
        Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));

        if (WEBSOCKET_ENABLED)
        {
            webSocketServer = new ElasticSeriesWebSocketServer(WEBSOCKET_PORT, applicationContext);
            webSocketServer.start();
        }

        if (ADMIN_WEBSOCKET_ENABLED)
        {
            adminControlWebSocketServer = new AdminControlWebSocketServer(SECRET_ADMIN_PORT, this);
            adminControlWebSocketServer.start();
        }
    }

    private GameScreenshotProcessor initialiseScreenshotProcessor()
    {
        gameScreenshotProcessor = new GameScreenshotProcessor(applicationContext);
        final Thread snapshotParserThread = new Thread(gameScreenshotProcessor::run);
        snapshotParserThread.setUncaughtExceptionHandler((thread, throwable) ->
        {
            throwable.printStackTrace();
        });
        return gameScreenshotProcessor;
    }

    private Thread initialiseTwitchWatcher()
    {
        twitchWatcher = new HeadlessTwitchWatcher(applicationContext);
        final Thread twitchWatcherThread = new Thread(twitchWatcher::run);
        twitchWatcherThread.setUncaughtExceptionHandler((thread, throwable) ->
        {
            throwable.printStackTrace();
        });
        return twitchWatcherThread;
    }

    private Thread initialiseTranscriptionPollerThread()
    {
        transcriptionPoller = new TranscriptionPoller(applicationContext);
        final Thread transcriptionThread = new Thread(transcriptionPoller::run);
        transcriptionThread.setUncaughtExceptionHandler((thread, throwable) ->
        {
            throwable.printStackTrace();
        });
        return transcriptionThread;
    }

    private Thread initialiseCommentaryRecorderThread()
    {
        commentaryRecorder = new CommentaryRecorder(applicationContext);
        final Thread commentaryRecorderThread = new Thread(commentaryRecorder::run);
        commentaryRecorderThread.setUncaughtExceptionHandler((thread, throwable) ->
        {
            throwable.printStackTrace();
        });
        return commentaryRecorderThread;
    }

    private void initialiseTempDirectories()
    {
        for (final File directory : Arrays.asList(TEMP_DIRECTORY, INCOMING_DIRECTORY, PROCESSING_DIRECTORY, COMPLETE_DIRECTORY, AUDIO_DIRECTORY, LOGO_DIRECTORY))
        {
            directory.delete();
            directory.mkdirs();
            System.out.println("Directory cleared: " + directory.getAbsolutePath());
        }
    }

    public void stopBroadcast()
    {
        applicationContext.setBroadcastLive(false);

        if (twitchWatcher != null)
        {
            twitchWatcher = null;
        }

        if (gameScreenshotProcessor != null)
        {
            gameScreenshotProcessor = null;
        }

        if (commentaryRecorder != null)
        {
            commentaryRecorder = null;
        }

        if (transcriptionPoller != null)
        {
            transcriptionPoller = null;
        }
    }

    public void startBroadcast()
    {
        initialiseTempDirectories();

        applicationContext.setBroadcastLive(true);

        if (BROADCAST_ENABLED)
        {
            final Thread twitchWatcherThread = initialiseTwitchWatcher();
            executorService.submit(twitchWatcherThread);
        }

        if (TRANSCRIPTION_ENABLED)
        {
            final Thread transcriptionThread = initialiseTranscriptionPollerThread();
            executorService.submit(transcriptionThread);
        }

        if (LIVE_COMMENTARY_RECORDING_ENABLED)
        {
            final Thread commentaryThread = initialiseCommentaryRecorderThread();
            executorService.submit(commentaryThread);
        }

        if (SCREENSHOT_PROCESSING_ENABLED)
        {
            gameScreenshotProcessor = initialiseScreenshotProcessor();
            executorService.submit(gameScreenshotProcessor::run);
        }
    }

    public void updateBroadcastUrl(final String broadcastUrl)
    {
        this.applicationContext.setBroadcastUrl(broadcastUrl);
    }

    public void updateLiquipediaUrl(final String liquipediaUrl)
    {
        this.applicationContext.setLiquipediaUrl(liquipediaUrl);
    }

    public void addDisplayNameMapping(final String displayName, final String liquipediaName)
    {
        this.applicationContext.getUppercaseDisplayToLiquipediaName().put(displayName.toUpperCase(), liquipediaName);
    }

    public void updateMidSeries(final boolean midSeries)
    {
        this.applicationContext.setMidSeriesAllowed(midSeries);
    }

    public ApplicationContext getApplicationContext()
    {
        return this.applicationContext;
    }
}
