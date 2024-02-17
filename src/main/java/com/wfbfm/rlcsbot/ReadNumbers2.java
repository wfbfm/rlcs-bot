package com.wfbfm.rlcsbot;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadNumbers2
{
    public static void main(String[] args) throws IOException, TesseractException
    {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
        // Load the image as Mat
        String imagePath = "src/main/resources/test-screenshot.png";
        System.out.println("Loading file " + imagePath);
        Mat image = Imgcodecs.imread(imagePath);

        // Convert Mat to grayscale
        Mat greyImage = new Mat();
        Imgproc.cvtColor(image, greyImage, Imgproc.COLOR_BGR2GRAY);
        System.out.println("Converted to greyscale.");

        // Perform OCR on the grayscale image
        String result = performOCR(greyImage);
        System.out.println(result);
    }

    private static String performOCR(Mat matImage) throws IOException, TesseractException
    {
        // Convert Mat to BufferedImage
        BufferedImage bufferedImage = matToBufferedImage(matImage);

        // Convert BufferedImage to byte array
        byte[] imageBytes = imageToBytes(bufferedImage);

        // Perform OCR on the byte array
        return performOCR(imageBytes);
    }

    private static String performOCR(byte[] imageBytes) throws IOException, TesseractException
    {
        // Perform OCR using Tesseract
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata");
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(1);
        tesseract.setOcrEngineMode(1);

        InputStream inputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        return tesseract.doOCR(bufferedImage);
    }

    private static BufferedImage matToBufferedImage(Mat mat)
    {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", mat, matOfByte);
        byte[] byteArray = matOfByte.toArray();

        try
        {
            InputStream in = new ByteArrayInputStream(byteArray);
            return ImageIO.read(in);
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] imageToBytes(BufferedImage image) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }
}
