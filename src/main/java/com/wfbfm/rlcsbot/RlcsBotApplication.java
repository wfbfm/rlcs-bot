package com.wfbfm.rlcsbot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RlcsBotApplication
{
    public static void main(String[] args)
    {
        final ExecutorService executorService = Executors.newFixedThreadPool(2);

        final HeadlessTwitchWatcher twitchWatcher = new HeadlessTwitchWatcher();
        executorService.submit(twitchWatcher::run);

        final GameScreenshotProcessor snapshotParser = new GameScreenshotProcessor();
        executorService.submit(snapshotParser::run);

        Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));
    }
}
