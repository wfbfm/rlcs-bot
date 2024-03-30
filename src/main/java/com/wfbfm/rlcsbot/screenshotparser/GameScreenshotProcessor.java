package com.wfbfm.rlcsbot.screenshotparser;

import com.wfbfm.rlcsbot.audiotranscriber.AudioTranscriptionDelegator;
import com.wfbfm.rlcsbot.elastic.ElasticSearchPublisher;
import com.wfbfm.rlcsbot.liquipedia.LiquipediaTeamGetter;
import com.wfbfm.rlcsbot.series.SeriesEvent;
import com.wfbfm.rlcsbot.series.SeriesSnapshot;
import com.wfbfm.rlcsbot.series.handler.SeriesSnapshotEvaluation;
import com.wfbfm.rlcsbot.series.handler.SeriesUpdateHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.*;


public class GameScreenshotProcessor
{
    // Crops and transforms sub-images of raw 1920x1080p screenshots of Twitch feed
    // Process sub-images using Tesseract to extract series information
    private final Logger logger = Logger.getLogger(GameScreenshotProcessor.class.getName());
    private final ScreenshotToSubImageTransformer screenshotToSubImageTransformer = new ScreenshotToSubImageTransformer();
    private final SubImageToSeriesSnapshotTransformer subImageToSeriesSnapshotTransformer = new SubImageToSeriesSnapshotTransformer();
    private final LiquipediaTeamGetter liquipediaTeamGetter = new LiquipediaTeamGetter();
    private final SeriesUpdateHandler seriesUpdateHandler = new SeriesUpdateHandler(liquipediaTeamGetter);
    private final AudioTranscriptionDelegator audioTranscriptionDelegator = new AudioTranscriptionDelegator();
    private final ElasticSearchPublisher elasticSearchPublisher = new ElasticSearchPublisher();

    public GameScreenshotProcessor()
    {
        this.liquipediaTeamGetter.setLiquipediaUrl(LIQUIPEDIA_PAGE);
        this.liquipediaTeamGetter.updateLiquipediaRefData();
    }

    public void run()
    {
        while (true)
        {
            try
            {
                pollAndHandleIncomingFiles();
            }
            catch (Exception e)
            {
                logger.log(Level.SEVERE, "Unable to handle incoming file - stopping feed.", e);
                break;
            }
        }
    }

    private void pollAndHandleIncomingFiles()
    {
        try
        {
            final File[] incomingFiles = INCOMING_DIRECTORY.listFiles();
            if (incomingFiles != null && incomingFiles.length > 0)
            {
                logger.log(Level.INFO, String.format("Found %d files in incoming directory - beginning processing.", incomingFiles.length));
                Arrays.sort(incomingFiles, Comparator.comparingLong(File::lastModified));
                for (final File incomingFile : incomingFiles)
                {
                    logger.log(Level.INFO, "Beginning processing: " + incomingFile.getName());
                    handleIncomingFile(incomingFile);
                }
            }
            Thread.sleep(INCOMING_POLLING_SLEEP_TIME_MS);
        } catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    private void handleIncomingFile(final File incomingFile)
    {
        final GameScreenshotSubImageWrapper subImageWrapper = this.screenshotToSubImageTransformer.transformScreenshotToSubImages(incomingFile);
        final SeriesSnapshot seriesSnapshot = this.subImageToSeriesSnapshotTransformer.transform(subImageWrapper);
        final SeriesSnapshotEvaluation evaluation = seriesUpdateHandler.evaluateSeries(seriesSnapshot);

        logger.log(Level.INFO, "Evaluation Result: " + evaluation.name());
        logger.log(Level.INFO, "Current Series Status: " + seriesUpdateHandler.getCurrentSeriesAsString());

        final SeriesEvent seriesEvent;
        switch (evaluation)
        {
            case NEW_SERIES:
                seriesEvent = new SeriesEvent(seriesUpdateHandler.getCurrentSeries(), evaluation);
                if (ELASTIC_ENABLED)
                {
                    elasticSearchPublisher.uploadNewSeriesEvent(seriesEvent);
                    elasticSearchPublisher.uploadNewSeries(seriesUpdateHandler.getCurrentSeries());
                }
                if (TRANSCRIPTION_ENABLED)
                {
                    audioTranscriptionDelegator.delegateAudioTranscription(seriesUpdateHandler.getCurrentSeries(), seriesEvent.getEventId());
                }
                break;
            case BLUE_GAME:
            case ORANGE_GAME:
            case BLUE_GOAL:
            case ORANGE_GOAL:
            case SERIES_COMPLETE:
                seriesEvent = new SeriesEvent(seriesUpdateHandler.getMostRecentCompletedSeries(), evaluation);
                if (ELASTIC_ENABLED)
                {
                    elasticSearchPublisher.uploadNewSeriesEvent(seriesEvent);
                    elasticSearchPublisher.updateSeries(seriesUpdateHandler.getMostRecentCompletedSeries());
                }
                if (TRANSCRIPTION_ENABLED)
                {
                    audioTranscriptionDelegator.delegateAudioTranscription(seriesUpdateHandler.getMostRecentCompletedSeries(), seriesEvent.getEventId());
                }
                break;
            case SCORE_UNCHANGED:
                if (ELASTIC_ENABLED)
                {
                    elasticSearchPublisher.updateSeries(seriesUpdateHandler.getCurrentSeries());
                }
                break;
            default:
                break;
        }

        if (RETAIN_SCREENSHOTS)
        {
            moveFileToCompletedDirectory(incomingFile);
        }
        else
        {
            incomingFile.delete();
        }
    }

    private void moveFileToCompletedDirectory(final File incomingFile)
    {
        try
        {
            final Path sourceFilePath = incomingFile.toPath();
            final Path targetFilePath = COMPLETE_DIRECTORY.toPath().resolve(incomingFile.getName());
            Files.move(sourceFilePath, targetFilePath);
        }
        catch (IOException e)
        {
            logger.log(Level.WARNING, "Unable to move screenshot " + incomingFile.getName() + " to completed folder.", e);
        }
    }
}
