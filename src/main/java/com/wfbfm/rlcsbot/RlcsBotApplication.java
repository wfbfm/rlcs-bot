package com.wfbfm.rlcsbot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RlcsBotApplication
{
    private final static boolean BROADCAST_ENABLED = false;

    public static void main(String[] args)
    {
        if (BROADCAST_ENABLED)
        {
            final ExecutorService executorService = Executors.newFixedThreadPool(2);

            final HeadlessTwitchWatcher twitchWatcher = new HeadlessTwitchWatcher();
            executorService.submit(twitchWatcher::run);

            final GameScreenshotProcessor snapshotParser = new GameScreenshotProcessor();
            executorService.submit(snapshotParser::run);

            Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));
        }
        else
        {
            final ExecutorService executorService = Executors.newFixedThreadPool(1);

            final GameScreenshotProcessor snapshotParser = new GameScreenshotProcessor();
            executorService.submit(snapshotParser::run);

            Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));
        }
    }
}
