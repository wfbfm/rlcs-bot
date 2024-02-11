package com.wfbfm.rlcsbot;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

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
        options.addArguments("--window-size=1920,1080");
        WebDriver driver = new ChromeDriver(options);

        // Navigate to the Twitch stream page
        driver.get("https://www.twitch.tv/rocketleague");

        captureAndSaveScreenshot(driver, "before-doing-anything.png");

        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.chord("f")).perform();
        actions.sendKeys(Keys.chord("m")).perform();

        // Wait for the quality settings to take effect
        try
        {
            Thread.sleep(2000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        captureAndSaveScreenshot(driver, "after-keyboard-shortcuts.png");

        WebElement settingsButton = driver.findElement(By.cssSelector("button[data-a-target='player-settings-button']"));
        settingsButton.click();

        // Wait for the settings menu to appear (adjust the waiting time as needed)
        try
        {
            Thread.sleep(2000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        captureAndSaveScreenshot(driver, "after-clicking-settings.png");

        WebElement qualityMenu = driver.findElement(By.xpath("//div[contains(@class, 'Layout') and contains(text(), 'Quality')]"));
        qualityMenu.click();
        // Wait for the settings menu to appear (adjust the waiting time as needed)
        try
        {
            Thread.sleep(2000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        captureAndSaveScreenshot(driver, "after-clicking-quality.png");
        WebElement qualityOptionSource = driver.findElement(By.xpath("//div[contains(@class, 'Layout') and contains(text(), '(Source)')]"));

        // Find and click on the 1080p60 (Source) quality option
        qualityOptionSource.click();

        // Wait for the quality settings to take effect
        try
        {
            Thread.sleep(2000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        captureAndSaveScreenshot(driver, "after-clicking-1080p.png");


        // Function to capture a frame from the video feed
        // byte[] screenshotData = captureScreenshot(videoWebElement);

        // Function to extract text using Tesseract OCR
//        String scoreText = extractText(frameData);
//        System.out.println("Detected Score: " + scoreText);
        // saveScreenshot(screenshotData, "screenshot.png");

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
    private static byte[] captureScreenshot(WebElement webElement)
    {
        TakesScreenshot screenshotDriver = (TakesScreenshot) webElement;
        return screenshotDriver.getScreenshotAs(OutputType.BYTES);
    }

    private static byte[] captureScreenshot(WebDriver webDriver)
    {
        TakesScreenshot screenshotDriver = (TakesScreenshot) webDriver;
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

    private static void captureAndSaveScreenshot(final WebDriver webDriver, final String fileName)
    {
        final byte[] screenshotData = captureScreenshot(webDriver);
        saveScreenshot(screenshotData, fileName);
    }
}
