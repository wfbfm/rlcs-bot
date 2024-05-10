package com.wfbfm.rlcsbot.screenshotparser;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.*;


public class ScreenshotToSubImageTransformer
{
    // Crops and transforms sub-images of raw 1920x1080p screenshots of Twitch feed
    // Process sub-images using Tesseract to extract series information
    private final List<SubImageStrategy> subImageStrategies = new ArrayList<>();
    private final Logger logger = Logger.getLogger(ScreenshotToSubImageTransformer.class.getName());
    private final GameScreenshotSubImageWrapperBuilder subImageWrapperBuilder = new GameScreenshotSubImageWrapperBuilder();

    public ScreenshotToSubImageTransformer()
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

    public GameScreenshotSubImageWrapper transformScreenshotToSubImages(final File incomingFile)
    {
        final Instant startTime = Instant.now();
        this.subImageWrapperBuilder.clear();
        this.subImageWrapperBuilder.withFileName(incomingFile.getName());
        try
        {
            final BufferedImage originalImage = ImageIO.read(incomingFile);
            for (final SubImageStrategy subImageStrategy : this.subImageStrategies)
            {
                processSubImage(incomingFile, originalImage, subImageStrategy);
            }
        } catch (IOException e)
        {
            logger.log(Level.WARNING, "Unable to locate incoming screenshot file.  Skipping.");
            throw new RuntimeException(e);
        }
        final Instant endTime = Instant.now();
        final long elapsedMs = endTime.toEpochMilli() - startTime.toEpochMilli();
        logger.log(Level.INFO, String.format("Time to create subImages: %d ms", elapsedMs));
        return this.subImageWrapperBuilder.build();
    }

    private void processSubImage(File incomingFile, BufferedImage originalImage, SubImageStrategy subImageStrategy) throws IOException
    {
        final BufferedImage subImage = GameScreenshotProcessorUtils.createSubImageFromStrategy(originalImage, subImageStrategy);
        if (subImage != null)
        {
            this.subImageWrapperBuilder.withSubImage(subImageStrategy.getSubImageType(), subImage);
            if (RETAIN_PROCESSING_FILES)
            {
                final String outputPath = PROCESSING_DIRECTORY + File.separator +
                        incomingFile.getName().replace(".png", "") + "-" + subImageStrategy.getSubImageType().name() + ".png";
                GameScreenshotProcessorUtils.saveImage(subImage, outputPath);
            }
        }
        else
        {
            logger.log(Level.WARNING, "Unable to create subImage: " + incomingFile.getName());
        }
    }
}
