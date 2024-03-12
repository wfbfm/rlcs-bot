package com.wfbfm.rlcsbot.app;

import com.wfbfm.rlcsbot.screenshotparser.GameScreenshotProcessor;
import com.wfbfm.rlcsbot.twitch.HeadlessTwitchWatcher;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.*;

public class RlcsBotApplication
{
    private final static boolean BROADCAST_ENABLED = false;

    public static void main(String[] args)
    {
        initaliseTempDirectories();

        if (BROADCAST_ENABLED)
        {
            final ExecutorService executorService = Executors.newFixedThreadPool(2);

            final Thread twitchWatcherThread = initialiseTwitchWatcher();
            executorService.submit(twitchWatcherThread);

            final GameScreenshotProcessor snapshotParser = initialiseSnapshotParser();
            executorService.submit(snapshotParser::run);

            Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));
        }
        else
        {
            final ExecutorService executorService = Executors.newFixedThreadPool(1);

            final GameScreenshotProcessor snapshotParser = initialiseSnapshotParser();
            executorService.submit(snapshotParser::run);

            Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));
        }
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

    private static void initaliseTempDirectories()
    {
        for (final File directory : Arrays.asList(INCOMING_DIRECTORY,
                PROCESSING_DIRECTORY,
                COMPLETE_DIRECTORY,
                AUDIO_DIRECTORY))
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
