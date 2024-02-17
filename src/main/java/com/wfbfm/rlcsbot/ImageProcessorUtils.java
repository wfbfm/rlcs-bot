package com.wfbfm.rlcsbot;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageProcessorUtils
{
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

                    // Crop, convert to greyscale, and save the image
                    processAndSaveImage(imagePath, name, startX, startY, endX, endY, invert);
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void processAndSaveImage(String imagePath, String name, int startX, int startY, int endX, int endY, boolean invert)
    {
        try
        {
            // Load the original image
            BufferedImage originalImage = ImageIO.read(new File(imagePath));

            // Crop the image
            BufferedImage croppedImage = cropImage(originalImage, startX, startY, endX, endY);

            // Convert to greyscale
            BufferedImage greyscaleImage = convertToGreyscale(croppedImage);

            // Invert greyscale if needed
            if (invert)
            {
                invertGreyscale(greyscaleImage);
            }

            // Save the processed image
            saveImage(greyscaleImage, name);
        } catch (IOException e)
        {
            e.printStackTrace();
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
