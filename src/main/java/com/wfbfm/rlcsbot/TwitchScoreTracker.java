package com.wfbfm.rlcsbot;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;

public class TwitchScoreTracker
{

    public static void main(String[] args)
    {
        // Set up the ChromeDriver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);

        // Navigate to the Twitch stream page
        driver.get("https://www.twitch.tv/rocketleague");

        // Function to capture a frame from the video feed
        byte[] screenshotData = captureScreenshot(driver);

        // Function to extract text using Tesseract OCR
//        String scoreText = extractText(frameData);
//        System.out.println("Detected Score: " + scoreText);
        saveScreenshot(screenshotData, "screenshot.png");

        // Close the browser when done
        driver.quit();
    }

    // Function to capture a frame from the video feed
    private static byte[] captureFrame(WebElement videoElement)
    {
        TakesScreenshot screenshotDriver = (TakesScreenshot) videoElement;
        byte[] screenshot = screenshotDriver.getScreenshotAs(OutputType.BYTES);

        // Convert the screenshot to a Mat (OpenCV image)
        Mat image = Imgcodecs.imdecode(new MatOfByte(screenshot), Imgcodecs.IMREAD_UNCHANGED);
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2RGB);

        // Convert the Mat back to bytes
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", image, matOfByte);
        return matOfByte.toArray();
    }

    // Function to capture a screenshot of the entire webpage
    private static byte[] captureScreenshot(WebDriver driver)
    {
        TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
        return screenshotDriver.getScreenshotAs(OutputType.BYTES);
    }

    // Function to save the screenshot data as a PNG file
    private static void saveScreenshot(byte[] screenshotData, String filePath)
    {
        try
        {
            FileUtils.writeByteArrayToFile(new File(filePath), screenshotData);
            System.out.println("Screenshot saved to: " + filePath);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
