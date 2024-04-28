package com.wfbfm.rlcsbot.audiotranscriber;

import com.wfbfm.rlcsbot.app.ApplicationContext;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TranscriptionPollerTest
{

    @Test
    public void testPollTranscriptionFile()
    {
        final ApplicationContext applicationContext = new ApplicationContext("test", "test", true);
        final ExecutorService executorService = Executors.newCachedThreadPool();

        final TranscriptionPoller transcriptionPoller = new TranscriptionPoller(applicationContext);
        final Thread transcriptionThread = new Thread(transcriptionPoller::run);
        transcriptionThread.setUncaughtExceptionHandler((thread, throwable) ->
        {
            throwable.printStackTrace();
        });
        executorService.submit(transcriptionThread);

        applicationContext.setBroadcastLive(false);
    }
}