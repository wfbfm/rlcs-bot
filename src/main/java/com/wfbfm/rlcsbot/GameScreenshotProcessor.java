package com.wfbfm.rlcsbot;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.wfbfm.rlcsbot.series.SeriesSnapshot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.RuntimeConstants.DEBUGGING_ENABLED;


public class GameScreenshotProcessor
{
    // Crops and transforms sub-images of raw 1920x1080p screenshots of Twitch feed
    // Process sub-images using Tesseract to extract series information
    private static final File INCOMING_DIRECTORY = new File("src/main/temp/incoming/");
    private static final File PROCESSING_DIRECTORY = new File("src/main/temp/processing/");
    private static final File COMPLETE_DIRECTORY = new File("src/main/temp/complete/");
    private static final File IGNORED_DIRECTORY = new File("src/main/temp/ignored/");
    private static final int POLLING_SLEEP_TIME_MS = 200;
    private static final String BROADCAST_SCHEMA_FILE_PATH = "src/main/resources/broadcast-schema.csv";
    private final List<SubImageStrategy> subImageStrategies = new ArrayList<>();
    private final Logger logger = Logger.getLogger(GameScreenshotProcessor.class.getName());
    private final GameScreenshotSubImageWrapperBuilder subImageWrapperBuilder = new GameScreenshotSubImageWrapperBuilder();
    private final ScreenshotToSeriesTransformer screenshotToSeriesTransformer = new ScreenshotToSeriesTransformer();

    public GameScreenshotProcessor()
    {
        initialiseBroadcastSchemaFromConfig();
    }

    private void initialiseBroadcastSchemaFromConfig()
    {
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(BROADCAST_SCHEMA_FILE_PATH)).withSkipLines(1).build())
        {
            String[] row;
            while ((row = csvReader.readNext()) != null)
            {
                final SubImageType subImageType = SubImageType.valueOf(row[0]);
                final int startX = Integer.parseInt(row[1]);
                final int startY = Integer.parseInt(row[2]);
                final int endX = Integer.parseInt(row[3]);
                final int endY = Integer.parseInt(row[4]);
                final boolean shouldKeepColour = Boolean.parseBoolean(row[5]);
                final boolean shouldInvertGreyscale = Boolean.parseBoolean(row[6]);
                final boolean isWhiteOnBlue = Boolean.parseBoolean(row[7]);
                final boolean isWhiteOnOrange = Boolean.parseBoolean(row[8]);
                final int rgbComparisonBuffer = Integer.parseInt(row[9]);
                final int additionalBorderSize = Integer.parseInt(row[10]);
                final int additionalCopies = Integer.parseInt(row[11]);

                final SubImageStrategy subImageStrategy = new SubImageStrategy(subImageType, startX, startY, endX, endY, shouldKeepColour,
                        isWhiteOnBlue, isWhiteOnOrange, rgbComparisonBuffer, additionalBorderSize, shouldInvertGreyscale, additionalCopies
                );

                this.subImageStrategies.add(subImageStrategy);
            }
        } catch (Exception e)
        {
            logger.log(Level.SEVERE, "Unable to read schema controlling the broadcast screenshot processing method.");
            throw new RuntimeException(e);
        }
    }

    public void run()
    {
        while (true)
        {
            pollAndHandleIncomingFiles();
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
            Thread.sleep(POLLING_SLEEP_TIME_MS);
        } catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    private void handleIncomingFile(final File incomingFile)
    {
        this.subImageWrapperBuilder.clear();
        this.subImageWrapperBuilder.withFileName(incomingFile.getName());
        transformScreenshotToSubImages(incomingFile);
        final GameScreenshotSubImageWrapper subImageWrapper = this.subImageWrapperBuilder.build();

        final SeriesSnapshot seriesSnapshot = this.screenshotToSeriesTransformer.transform(subImageWrapper);

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
//
//        if (isValidSeriesData())
//        {
//            processSeriesData();
//            moveScreenshotToFolder("processed");
//        } else
//        {
//            moveScreenshotToFolder("ignored");
//        }
//        deleteSubImages();
    }

    private void transformScreenshotToSubImages(final File incomingFile)
    {
        final Instant startTime = Instant.now();
        try
        {
            final BufferedImage originalImage = ImageIO.read(incomingFile);

            for (final SubImageStrategy subImageStrategy : this.subImageStrategies)
            {
                final BufferedImage subImage = GameScreenshotProcessorUtils.createSubImageFromStrategy(originalImage, subImageStrategy);
                this.subImageWrapperBuilder.withSubImage(subImageStrategy.getSubImageType(), subImage);
                if (DEBUGGING_ENABLED)
                {
                    final String outputPath = PROCESSING_DIRECTORY + File.separator +
                            incomingFile.getName().replace(".png", "") + "-" + subImageStrategy.getSubImageType().name() + ".png";
                    GameScreenshotProcessorUtils.saveImage(subImage, outputPath);
                }
            }
        } catch (IOException e)
        {
            logger.log(Level.WARNING, "Unable to locate incoming screenshot file.  Skipping.");
            throw new RuntimeException(e);
        }
        final Instant endTime = Instant.now();
        final long elapsedMs = endTime.toEpochMilli() - startTime.toEpochMilli();
        logger.log(Level.INFO, String.format("Time to create subImages: %d ms", elapsedMs));
    }

    private SeriesSnapshot parseSeriesDataFromSubImages()
    {
        // TODO
        return null;
    }

    private void deleteSubImages()
    {

    }

    private void processSeriesData()
    {

    }

    private void moveScreenshotToFolder(final String folder)
    {


    }

    private boolean isValidSeriesData()
    {
        return false;
    }
}
