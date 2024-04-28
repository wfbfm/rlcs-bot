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
    private ExecutorService executorService;
    private Thread twitchWatcherThread;
    private Thread transcriptionThread;
    private Thread commentaryThread;
    private HeadlessTwitchWatcher twitchWatcher;
    private TranscriptionPoller transcriptionPoller;
    private CommentaryRecorder commentaryRecorder;
    private GameScreenshotProcessor gameScreenshotProcessor;
    private ElasticSeriesWebSocketServer webSocketServer;
    private AdminControlWebSocketServer adminControlWebSocketServer;

    public void start()
    {
        initialiseTempDirectories();

        executorService = Executors.newCachedThreadPool();

        if (BROADCAST_ENABLED)
        {
            twitchWatcherThread = initialiseTwitchWatcher();
            executorService.submit(twitchWatcherThread);
        }

        if (TRANSCRIPTION_ENABLED)
        {
            transcriptionThread = initialiseTranscriptionPollerThread();
            executorService.submit(transcriptionThread);
        }

        if (LIVE_COMMENTARY_RECORDING_ENABLED)
        {
            commentaryThread = initialiseCommentaryRecorderThread();
            executorService.submit(commentaryThread);
        }

        if (SCREENSHOT_PROCESSING_ENABLED)
        {
            gameScreenshotProcessor = initialiseScreenshotProcessor();
            executorService.submit(gameScreenshotProcessor::run);
        }

        if (WEBSOCKET_ENABLED)
        {
            webSocketServer = new ElasticSeriesWebSocketServer(WEBSOCKET_PORT);
            webSocketServer.start();
        }

        if (ADMIN_WEBSOCKET_ENABLED)
        {
            adminControlWebSocketServer = new AdminControlWebSocketServer(SECRET_ADMIN_PORT);
            adminControlWebSocketServer.start();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));
    }

    private GameScreenshotProcessor initialiseScreenshotProcessor()
    {
        gameScreenshotProcessor = new GameScreenshotProcessor();
        final Thread snapshotParserThread = new Thread(gameScreenshotProcessor::run);
        snapshotParserThread.setUncaughtExceptionHandler((thread, throwable) ->
        {
            throwable.printStackTrace();
        });
        return gameScreenshotProcessor;
    }

    private Thread initialiseTwitchWatcher()
    {
        twitchWatcher = new HeadlessTwitchWatcher();
        final Thread twitchWatcherThread = new Thread(twitchWatcher::run);
        twitchWatcherThread.setUncaughtExceptionHandler((thread, throwable) ->
        {
            throwable.printStackTrace();
        });
        return twitchWatcherThread;
    }

    private Thread initialiseTranscriptionPollerThread()
    {
        transcriptionPoller = new TranscriptionPoller();
        final Thread transcriptionThread = new Thread(transcriptionPoller::run);
        transcriptionThread.setUncaughtExceptionHandler((thread, throwable) ->
        {
            throwable.printStackTrace();
        });
        return transcriptionThread;
    }

    private Thread initialiseCommentaryRecorderThread()
    {
        commentaryRecorder = new CommentaryRecorder();
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
            if (directory.exists())
            {
                if (directory.equals(AUDIO_DIRECTORY) && !LIVE_COMMENTARY_RECORDING_ENABLED)
                {
                    continue;
                }
                directory.delete();
                directory.mkdirs();
                System.out.println("Directory cleared: " + directory.getAbsolutePath());
            }
            else
            {
                directory.mkdirs();
                System.out.println("Directory created: " + directory.getAbsolutePath());
            }
        }
    }

    public void stopBroadcast()
    {
        if (twitchWatcher != null)
        {
            twitchWatcher.stop();
            twitchWatcher = null;
        }

        if (gameScreenshotProcessor != null)
        {
            gameScreenshotProcessor.stop();
            gameScreenshotProcessor = null;
        }

        if (commentaryRecorder != null)
        {
            commentaryRecorder.stop();
            commentaryRecorder = null;
        }

        if (transcriptionPoller != null)
        {
            transcriptionPoller.stop();
            transcriptionPoller = null;
        }
    }

    public void startBroadcast()
    {

    }

    public void updateLiquipediaPage()
    {

    }

    public void addDisplayNameMapping()
    {

    }
}
