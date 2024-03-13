package com.wfbfm.rlcsbot.audiotranscriber;

import com.google.common.annotations.VisibleForTesting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.AUDIO_DIRECTORY;
import static com.wfbfm.rlcsbot.app.RuntimeConstants.INCOMING_POLLING_SLEEP_TIME_MS;

public class TranscriptionPoller
{
    private final Logger logger = Logger.getLogger(TranscriptionPoller.class.getName());

    public void run()
    {
        try
        {
            final File[] transcriptionFiles = AUDIO_DIRECTORY.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
            if (transcriptionFiles != null && transcriptionFiles.length > 0)
            {
                Arrays.sort(transcriptionFiles, Comparator.comparingLong(File::lastModified));
                for (final File transcriptionFile : transcriptionFiles)
                {
                    handleTranscription(transcriptionFile);
                }
            }
            Thread.sleep(INCOMING_POLLING_SLEEP_TIME_MS);
        } catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    @VisibleForTesting
    public void handleTranscription(final File transcriptionFile)
    {
        try
        {
            final String transcription = new String(Files.readAllBytes(Paths.get(transcriptionFile.getAbsolutePath())));
            logger.log(Level.INFO, transcriptionFile.getName() + ": " + transcription);
            uploadTranscription(transcription);
        }
        catch (IOException e)
        {
            logger.log(Level.WARNING, "Unable to access transcription file " + transcriptionFile.getName(), e);
        }
    }

    private boolean uploadTranscription(final String transcription)
    {
        // TODO: publish to elastic.
        return true;
    }
}
