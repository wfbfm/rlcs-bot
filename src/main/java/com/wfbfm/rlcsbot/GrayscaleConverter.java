package com.wfbfm.rlcsbot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GrayscaleConverter
{
    public static void main(String[] args)
    {
        try
        {
            // Load the image
            final String filePath = "src/main/resources/test-crop-just-score.png";
            File input = new File(filePath); // Replace with the actual path
            BufferedImage image = ImageIO.read(input);

            // Convert the image to grayscale
            BufferedImage grayscaleImage = convertToGrayscale(image);

            // Save the grayscale image
            final String newFilePath = "src/main/resources/test-crop-just-score-grey.png";
            File output = new File(newFilePath); // Replace with the desired output path
            ImageIO.write(grayscaleImage, "jpg", output);

            System.out.println("Conversion to grayscale completed.");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static BufferedImage convertToGrayscale(BufferedImage colorImage)
    {
        int width = colorImage.getWidth();
        int height = colorImage.getHeight();

        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                Color color = new Color(colorImage.getRGB(x, y));
                int grayValue = (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
                int grayPixel = new Color(grayValue, grayValue, grayValue).getRGB();
                grayscaleImage.setRGB(x, y, grayPixel);
            }
        }

        return grayscaleImage;
    }
}

