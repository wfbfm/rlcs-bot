package com.wfbfm.rlcsbot.audiotranscriber;

import java.io.IOException;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.BROADCAST_URL;
import static com.wfbfm.rlcsbot.app.RuntimeConstants.FULL_AUDIO_FILE;

public class CommentaryRecorder
{
    private static final String STREAMLINK_COMMAND = "streamlink";
    private Process process;

    public void run()
    {
        final ProcessBuilder processBuilder = createStreamlinkProcess();

        try
        {
            process = processBuilder.start();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            if (process != null)
            {
                process.destroy();
            }
        }));
    }

    private ProcessBuilder createStreamlinkProcess()
    {
        return new ProcessBuilder(
                STREAMLINK_COMMAND,
                BROADCAST_URL,
                "audio_only",
                "-o",
                escapeSpaces(FULL_AUDIO_FILE.getAbsolutePath()),
                "--twitch-disable-ads"
        );
    }

    private String escapeSpaces(final String inputString)
    {
        return "\"" + inputString + "\"";
    }
}
