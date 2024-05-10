package com.wfbfm.rlcsbot.screenshotparser;

import com.wfbfm.rlcsbot.series.Clock;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.GAME_TIME_SECONDS;

public class GameScreenshotProcessorUtils
{
    public static BufferedImage createSubImageFromStrategy(final BufferedImage originalImage, final SubImageStrategy strategy)
    {
        if (originalImage == null)
        {
            return null;
        }
        BufferedImage subImage = cropImage(originalImage, strategy.getCropStartX(), strategy.getCropStartY(),
                strategy.getCropEndX(), strategy.getCropEndY());

        if (strategy.isWhiteOnBlue())
        {
            processWhiteOnBlue(subImage, strategy.getRgbComparisonBuffer());
        }
        else if (strategy.isWhiteOnOrange())
        {
            processWhiteOnOrange(subImage, strategy.getRgbComparisonBuffer());
        }
        else if (!strategy.shouldKeepColour())
        {
            subImage = convertToGreyscale(subImage);
        }

        if (strategy.shouldInvertGreyscale())
        {
            invertGreyscale(subImage);
        }

        if (strategy.getAdditionalCopies() > 0)
        {
            subImage = createSideBySideImage(subImage, strategy.getAdditionalCopies());
        }

        if (strategy.getAdditionalBorderSize() > 0)
        {
            subImage = addWhiteBorder(subImage, strategy.getAdditionalBorderSize());
        }
        return subImage;
    }

    public static void saveImage(final BufferedImage image, final String outputPathString) throws IOException
    {
        final Path outputPath = Paths.get(outputPathString);
        ImageIO.write(image, "png", outputPath.toFile());
    }

    private static BufferedImage addWhiteBorder(final BufferedImage originalImage, final int borderSize)
    {
        final int originalWidth = originalImage.getWidth();
        final int originalHeight = originalImage.getHeight();
        final int newWidth = originalWidth + 2 * borderSize;
        final int newHeight = originalHeight + 2 * borderSize;

        final BufferedImage imageWithBorder = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        final Graphics2D newGraphics = imageWithBorder.createGraphics();
        newGraphics.setColor(Color.WHITE);
        newGraphics.fillRect(0, 0, newWidth, newHeight);
        newGraphics.drawImage(originalImage, borderSize, borderSize, null);
        newGraphics.dispose();

        return imageWithBorder;
    }

    private static void processWhiteOnBlue(final BufferedImage image, final int rgbComparisonBuffer)
    {
        final int width = image.getWidth();
        final int height = image.getHeight();

        // Iterate through each pixel
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                // Get the colour of the current pixel
                final Color color = new Color(image.getRGB(x, y));

                // Check if the pixel is within the blue channel
                if (color.getBlue() > (color.getRed() + rgbComparisonBuffer))
                {
                    // Set everything in the Blue channel to White
                    image.setRGB(x, y, Color.WHITE.getRGB());
                }
                else
                {
                    // Set everything else to Black
                    image.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
    }

    private static void processWhiteOnOrange(final BufferedImage image, final int rgbComparisonBuffer)
    {
        final int width = image.getWidth();
        final int height = image.getHeight();

        // Iterate through each pixel
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                // Get the color of the current pixel
                Color color = new Color(image.getRGB(x, y));

                // Check if the pixel is within the orange channel
                if (color.getRed() > (color.getBlue() + rgbComparisonBuffer))
                {
                    // Set everything in the Orange channel to White
                    image.setRGB(x, y, Color.WHITE.getRGB());
                }
                else
                {
                    // Set everything else to Black
                    image.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
    }

    private static BufferedImage cropImage(final BufferedImage originalImage,
                                           final int startX,
                                           final int startY,
                                           final int endX,
                                           final int endY)
    {
        return originalImage.getSubimage(startX, startY, endX - startX, endY - startY);
    }

    private static BufferedImage convertToGreyscale(final BufferedImage colourImage)
    {
        BufferedImage greyscaleImage = new BufferedImage(colourImage.getWidth(), colourImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        greyscaleImage.getGraphics().drawImage(colourImage, 0, 0, null);
        return greyscaleImage;
    }

    private static void invertGreyscale(final BufferedImage greyscaleImage)
    {
        final int width = greyscaleImage.getWidth();
        final int height = greyscaleImage.getHeight();
        final int threshold = 128;

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int rgb = greyscaleImage.getRGB(x, y);
                int greyValue = (rgb >> 16) & 0xFF; // Extract the red component assuming greyscale
                int binaryValue = greyValue < threshold ? 0 : 255; // Set binary value based on threshold
                int invertedBinaryValue = binaryValue == 0 ? 255 : 0; // Invert the binary value
                int newRgb = (invertedBinaryValue << 16) | (invertedBinaryValue << 8) | invertedBinaryValue; // Create new RGB value
                greyscaleImage.setRGB(x, y, newRgb);
            }
        }
    }

    public static BufferedImage createSideBySideImage(final BufferedImage inputImage, final int additionalCopies)
    {
        final int numberOfCopies = additionalCopies + 1;
        final int inputWidth = inputImage.getWidth();
        final int inputHeight = inputImage.getHeight();
        final int resultWidth = inputWidth * numberOfCopies;

        final BufferedImage resultImage = new BufferedImage(resultWidth, inputHeight, BufferedImage.TYPE_BYTE_GRAY);
        final Graphics g = resultImage.getGraphics();

        for (int i = 0; i < numberOfCopies; i++)
        {
            int xPosition = i * inputWidth;
            g.drawImage(inputImage, xPosition, 0, null);
        }
        g.dispose();
        return resultImage;
    }

    public static Clock parseClockFromTime(String displayedTime)
    {
        final boolean isOvertime = displayedTime.contains("+");

        final String[] clockParts = displayedTime.split(":");

        int elapsedSeconds = GAME_TIME_SECONDS;
        if (clockParts.length != 2)
        {
            elapsedSeconds = 0;
        }
        else
        {
            final int minutes = StringUtils.isNumeric(clockParts[0]) ? Integer.parseInt(clockParts[0]) : 0;
            final int seconds = StringUtils.isNumeric(clockParts[1]) ? Integer.parseInt(clockParts[1]) : 0;
            if (isOvertime) // clock is counting up
            {
                elapsedSeconds += (minutes * 60);
                elapsedSeconds += seconds;
            }
            else // clock is counting down
            {
                elapsedSeconds -= (minutes * 60);
                elapsedSeconds -= seconds;
            }
        }
        return new Clock(displayedTime, elapsedSeconds, isOvertime);
    }
}
