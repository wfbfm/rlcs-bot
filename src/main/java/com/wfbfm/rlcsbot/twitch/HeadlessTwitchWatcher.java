package com.wfbfm.rlcsbot.twitch;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HeadlessTwitchWatcher
{
    private static final String BROADCAST_URL = "https://www.twitch.tv/rocketleague";
    private static final int SCREENSHOT_SLEEP_TIME_MS = 10_000;
    private static final File INCOMING_DIRECTORY = new File("src/main/temp/incoming/");
    private final Logger logger = Logger.getLogger(HeadlessTwitchWatcher.class.getName());
    private final WebDriver webDriver;
    private final Actions actions;
    private final TakesScreenshot screenshotDriver;

    public HeadlessTwitchWatcher()
    {
        // Set up the ChromeDriver
        final ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--window-size=1920,1080");
        webDriver = new ChromeDriver(options);
        actions = new Actions(webDriver);
        screenshotDriver = (TakesScreenshot) webDriver;
    }

    private void getStreamInFullScreen()
    {
        webDriver.get(BROADCAST_URL);
        actions.sendKeys(Keys.chord("f")).perform();
        sleepForMs(1000);

        final WebElement settingsButton = webDriver.findElement(By.cssSelector("button[data-a-target='player-settings-button']"));
        settingsButton.click();
        sleepForMs(1000);

        final WebElement qualityMenu = webDriver.findElement(By.xpath("//div[contains(@class, 'Layout') and contains(text(), 'Quality')]"));
        qualityMenu.click();
        sleepForMs(1000);

        // (Source) quality = highest
        final WebElement qualityOptionSource = webDriver.findElement(By.xpath("//div[contains(@class, 'Layout') and contains(text(), '(Source)')]"));
        qualityOptionSource.click();
        sleepForMs(3000);
    }

    private void sleepForMs(final int millis)
    {
        try
        {
            Thread.sleep(millis);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        getStreamInFullScreen();

        while (true)
        {
            try
            {
                captureAndSaveScreenshot("screenshot-" + Instant.now().toEpochMilli() + ".png");
            }
            catch (IOException e)
            {
                logger.log(Level.SEVERE, "Unable to take screenshot - stopping feed.", e);
                break;
            }
            sleepForMs(SCREENSHOT_SLEEP_TIME_MS);
        }
        webDriver.quit();
    }

    private void captureAndSaveScreenshot(final String fileName) throws IOException
    {
        final byte[] screenshotData = screenshotDriver.getScreenshotAs(OutputType.BYTES);
        final String filePath = INCOMING_DIRECTORY + File.separator + fileName;
        FileUtils.writeByteArrayToFile(new File(filePath), screenshotData);
    }
}
