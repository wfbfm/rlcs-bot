package com.wfbfm.rlcsbot.audiotranscriber;

import com.google.common.annotations.VisibleForTesting;
import com.wfbfm.rlcsbot.elastic.ElasticSearchPublisher;
import com.wfbfm.rlcsbot.series.SeriesEvent;

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
    private final ElasticSearchPublisher elasticSearchPublisher = new ElasticSearchPublisher();
    private final Logger logger = Logger.getLogger(TranscriptionPoller.class.getName());
    private boolean isRunning = true;

    public void run()
    {
        logger.log(Level.INFO, "Starting worker thread");
        while (isRunning)
        {
            pollAndHandleTranscriptionFiles();
        }
        logger.log(Level.INFO, "Stopping worker thread");
    }

    public void stop()
    {
        isRunning = false;
    }

    private void pollAndHandleTranscriptionFiles()
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
        }
        catch (InterruptedException e)
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
            final String seriesEventId = transcriptionFile.getName().substring(0, transcriptionFile.getName().lastIndexOf('.'));
            uploadTranscription(transcription, seriesEventId);
            transcriptionFile.delete();
        }
        catch (IOException e)
        {
            logger.log(Level.WARNING, "Unable to access transcription file " + transcriptionFile.getName(), e);
        }
    }

    private void uploadTranscription(final String transcription, final String seriesEventId)
    {
        final SeriesEvent seriesEvent = elasticSearchPublisher.searchForSeriesEvent(seriesEventId);
        if (seriesEvent == null)
        {
            logger.log(Level.WARNING, "Unable to find seriesEventId in elastic - cannot upload transcription for: " + seriesEventId);
            return;
        }
        logger.log(Level.INFO, "Attempting to update Elastic with commentary for " + seriesEventId);
        seriesEvent.setCommentary(transcription);
        elasticSearchPublisher.updateSeriesEvent(seriesEvent);
        logger.log(Level.INFO, "Finished attempt to update Elastic with commentary for " + seriesEventId);
    }
}
