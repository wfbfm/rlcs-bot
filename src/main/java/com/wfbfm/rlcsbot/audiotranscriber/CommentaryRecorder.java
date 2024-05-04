package com.wfbfm.rlcsbot.audiotranscriber;

import com.wfbfm.rlcsbot.app.ApplicationContext;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.FULL_AUDIO_FILE;
import static com.wfbfm.rlcsbot.app.RuntimeConstants.INCOMING_POLLING_SLEEP_TIME_MS;

public class CommentaryRecorder
{
    private static final String STREAMLINK_COMMAND = "streamlink";
    private final ApplicationContext applicationContext;
    private final Logger logger = Logger.getLogger(CommentaryRecorder.class.getName());
    private Process process;

    public CommentaryRecorder(final ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    public void run()
    {
        if (applicationContext.getBroadcastUrl() == null)
        {
            logger.log(Level.SEVERE, "Broadcast URL is not set on context - cannot record commentary");
            return;
        }

        final ProcessBuilder processBuilder = createStreamlinkProcess(applicationContext.getBroadcastUrl());

        try
        {
            logger.log(Level.INFO, "Submitting process: " + processBuilder.command());
            process = processBuilder.start();
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Unsuccessful commentary recording", e);
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

        while (applicationContext.isBroadcastLive() && process.isAlive())
        {
            try
            {
                Thread.sleep(INCOMING_POLLING_SLEEP_TIME_MS);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }

        stop();
    }

    private void stop()
    {
        // TODO - wire this into ApplicationContext?
        if (process != null)
        {
            logger.log(Level.INFO, "Destroying CommentaryRecorder process");
            process.destroy();
        }
        else
        {
            logger.log(Level.INFO, "CommentaryRecorder process is already stopped");
        }
    }

    private ProcessBuilder createStreamlinkProcess(final String streamUrl)
    {
        final String audioQuality = streamUrl.contains("/videos/") ? "audio" : "audio_only";
        return new ProcessBuilder(
                STREAMLINK_COMMAND,
                streamUrl,
                audioQuality,
                "-o",
                FULL_AUDIO_FILE.getAbsolutePath(),
                "--twitch-disable-ads"
        );
    }
}
