package com.wfbfm.rlcsbot.screenshotparser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GameScreenshotProcessorUtils
{
    public static BufferedImage createSubImageFromStrategy(final BufferedImage originalImage, final SubImageStrategy strategy)
    {
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

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int rgb = greyscaleImage.getRGB(x, y);
                int invertedRgb = ~rgb & 0xFFFFFF; // Invert each pixel
                greyscaleImage.setRGB(x, y, invertedRgb);
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
}
