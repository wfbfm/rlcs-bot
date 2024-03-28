package com.wfbfm.rlcsbot.app;

import com.wfbfm.rlcsbot.audiotranscriber.CommentaryRecorder;
import com.wfbfm.rlcsbot.audiotranscriber.TranscriptionPoller;
import com.wfbfm.rlcsbot.screenshotparser.GameScreenshotProcessor;
import com.wfbfm.rlcsbot.twitch.HeadlessTwitchWatcher;
import com.wfbfm.rlcsbot.websocket.ElasticSeriesWebSocketServer;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.*;

public class RlcsBotApplication
{
    public static void main(String[] args)
    {
        initaliseTempDirectories();

        if (WEBSOCKET_ENABLED)
        {
            final ElasticSeriesWebSocketServer webSocketServer = new ElasticSeriesWebSocketServer(WEBSOCKET_PORT);
            webSocketServer.start();
        }

        final ExecutorService executorService = Executors.newFixedThreadPool(getTotalNumberOfThreads());

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
            final Thread commentaryRecorder = initialiseCommentaryRecorderThread();
            executorService.submit(commentaryRecorder);
        }

        final GameScreenshotProcessor snapshotParser = initialiseSnapshotParser();
        executorService.submit(snapshotParser::run);

        Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));
    }

    private static int getTotalNumberOfThreads()
    {
        int numberOfThreads = 1; // game screenshot processor
        if (BROADCAST_ENABLED)
        {
            numberOfThreads++;
        }
        if (TRANSCRIPTION_ENABLED)
        {
            numberOfThreads++;
        }
        if (LIVE_COMMENTARY_RECORDING_ENABLED)
        {
            numberOfThreads++;
        }
        return numberOfThreads;
    }
    private static GameScreenshotProcessor initialiseSnapshotParser()
    {
        final GameScreenshotProcessor snapshotParser = new GameScreenshotProcessor();
        final Thread snapshotParserThread = new Thread(snapshotParser::run);
        snapshotParserThread.setUncaughtExceptionHandler((thread, throwable) ->
        {
            throwable.printStackTrace();
        });
        return snapshotParser;
    }

    private static Thread initialiseTwitchWatcher()
    {
        final HeadlessTwitchWatcher twitchWatcher = new HeadlessTwitchWatcher();
        final Thread twitchWatcherThread = new Thread(twitchWatcher::run);
        twitchWatcherThread.setUncaughtExceptionHandler((thread, throwable) ->
        {
            throwable.printStackTrace();
        });
        return twitchWatcherThread;
    }

    private static Thread initialiseTranscriptionPollerThread()
    {
        final TranscriptionPoller transcriptionPoller = new TranscriptionPoller();
        final Thread transcriptionThread = new Thread(transcriptionPoller::run);
        transcriptionThread.setUncaughtExceptionHandler((thread, throwable) ->
        {
            throwable.printStackTrace();
        });
        return transcriptionThread;
    }

    private static Thread initialiseCommentaryRecorderThread()
    {
        final CommentaryRecorder commentaryRecorder = new CommentaryRecorder();
        final Thread commentaryRecorderThread = new Thread(commentaryRecorder::run);
        commentaryRecorderThread.setUncaughtExceptionHandler((thread, throwable) ->
        {
            throwable.printStackTrace();
        });
        return commentaryRecorderThread;
    }

    private static void initaliseTempDirectories()
    {
        for (final File directory : Arrays.asList(TEMP_DIRECTORY, INCOMING_DIRECTORY, PROCESSING_DIRECTORY, COMPLETE_DIRECTORY, AUDIO_DIRECTORY))
        {
            if (!directory.exists())
            {
                boolean success = directory.mkdirs();
                if (success)
                {
                    System.out.println("Directory created: " + directory.getAbsolutePath());
                }
                else
                {
                    System.err.println("Failed to create directory: " + directory.getAbsolutePath());
                }
            }
        }
    }
}
