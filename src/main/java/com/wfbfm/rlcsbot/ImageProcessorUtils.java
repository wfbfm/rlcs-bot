package com.wfbfm.rlcsbot;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageProcessorUtils
{
    private static final int BUFFER = 30;

    public static void processImages(String imagePath, String csvFilePath) throws CsvValidationException
    {
        try
        {
            // Read CSV file using OpenCSV
            try (CSVReader csvReader = new CSVReader(new FileReader(csvFilePath)))
            {
                String[] row;
                while ((row = csvReader.readNext()) != null)
                {
                    String name = row[0];
                    int startX = Integer.parseInt(row[1]);
                    int startY = Integer.parseInt(row[2]);
                    int endX = Integer.parseInt(row[3]);
                    int endY = Integer.parseInt(row[4]);
                    boolean invert = Boolean.parseBoolean(row[5]);
                    boolean isWhiteOnBlue = Boolean.parseBoolean(row[6]);
                    boolean isWhiteOnOrange = Boolean.parseBoolean(row[7]);

                    // Crop, convert to greyscale, and save the image
                    processAndSaveImage(imagePath, name, startX, startY, endX, endY, invert, isWhiteOnBlue, isWhiteOnOrange);
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void processAndSaveImage(String imagePath, String name, int startX, int startY, int endX, int endY, boolean invert, boolean isWhiteOnBlue, boolean isWhiteOnOrange)
    {
        try
        {
            // Load the original image
            BufferedImage originalImage = ImageIO.read(new File(imagePath));

            // Crop the image
            BufferedImage croppedImage = cropImage(originalImage, startX, startY, endX, endY);

            BufferedImage processedImage = croppedImage;
            if (isWhiteOnBlue)
            {
                processWhiteOnBlue(processedImage);
                processedImage = addWhiteBorder(processedImage, 200);
            }
            else if (isWhiteOnOrange)
            {
                processWhiteOnOrange(processedImage);
                processedImage = addWhiteBorder(processedImage, 200);
            }
            else
            {
                // Convert to greyscale
                processedImage = convertToGreyscale(croppedImage);

                // Invert greyscale if needed
                if (invert)
                {
                    invertGreyscale(processedImage);
                }
            }

            // Save the processed image
            saveImage(processedImage, name);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static BufferedImage addWhiteBorder(BufferedImage image, int borderSize) {
        int width = image.getWidth() + 2 * borderSize;
        int height = image.getHeight() + 2 * borderSize;

        BufferedImage imageWithBorder = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = imageWithBorder.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.drawImage(image, borderSize, borderSize, null);
        g2d.dispose();

        return imageWithBorder;
    }

    private static void processWhiteOnBlue(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Iterate through each pixel
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                // Get the color of the current pixel
                Color color = new Color(image.getRGB(x, y));

                // Check if the pixel is within the blue channel
                if (color.getBlue() > (color.getRed() + BUFFER))
                {
                    // Set everything in the Blue channel to White
                    image.setRGB(x, y, Color.WHITE.getRGB());
                } else
                {
                    // Set everything else to Black
                    image.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
    }

    private static void processWhiteOnOrange(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Iterate through each pixel
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                // Get the color of the current pixel
                Color color = new Color(image.getRGB(x, y));

                // Check if the pixel is within the orange channel
                if (color.getRed() > (color.getBlue() + BUFFER))
                {
                    // Set everything in the Red
                    image.setRGB(x, y, Color.WHITE.getRGB());
                } else
                {
                    // Set everything else to Black
                    image.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
    }

    private static BufferedImage cropImage(BufferedImage originalImage, int startX, int startY, int endX, int endY)
    {
        return originalImage.getSubimage(startX, startY, endX - startX, endY - startY);
    }

    private static BufferedImage convertToGreyscale(BufferedImage colorImage)
    {
        BufferedImage greyscaleImage = new BufferedImage(colorImage.getWidth(), colorImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        greyscaleImage.getGraphics().drawImage(colorImage, 0, 0, null);
        return greyscaleImage;
    }

    private static void invertGreyscale(BufferedImage greyscaleImage)
    {
        int width = greyscaleImage.getWidth();
        int height = greyscaleImage.getHeight();

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

    private static void saveImage(BufferedImage image, String name) throws IOException
    {
        Path outputPath = Paths.get("src/main/resources/" + name + ".png");
        ImageIO.write(image, "png", outputPath.toFile());
    }
}
