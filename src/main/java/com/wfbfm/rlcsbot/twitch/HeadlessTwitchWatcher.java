package com.wfbfm.rlcsbot.twitch;

import com.wfbfm.rlcsbot.app.ApplicationContext;
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

import static com.wfbfm.rlcsbot.app.RuntimeConstants.*;

public class HeadlessTwitchWatcher
{
    private final ApplicationContext applicationContext;
    private final Logger logger = Logger.getLogger(HeadlessTwitchWatcher.class.getName());
    private final WebDriver webDriver;
    private final Actions actions;
    private final TakesScreenshot screenshotDriver;

    public HeadlessTwitchWatcher(final ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
        // Set up the ChromeDriver
        final ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--window-size=1920,1080");
        // options.add_argument("--mute-audio")
        webDriver = new ChromeDriver(options);
        actions = new Actions(webDriver);
        screenshotDriver = (TakesScreenshot) webDriver;
    }

    public void run()
    {
        Runtime.getRuntime().addShutdownHook(new Thread(webDriver::quit));

        logger.log(Level.INFO, "Starting worker thread");
        getStreamInFullScreen();

        while (applicationContext.isBroadcastLive())
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
            sleepForMs(SCREENSHOT_INTERVAL_MS);
        }

        logger.log(Level.INFO, "Stopping worker thread");
        webDriver.quit();
    }

    private void getStreamInFullScreen()
    {
        webDriver.get(BROADCAST_URL);
        sleepForMs(5000);
        actions.sendKeys(Keys.chord("f")).perform();

        final WebElement settingsButton = webDriver.findElement(By.cssSelector("button[data-a-target='player-settings-button']"));
        settingsButton.click();
        sleepForMs(1000);

        final WebElement qualityMenu = webDriver.findElement(By.xpath("//div[contains(@class, 'Layout') and contains(text(), 'Quality')]"));
        qualityMenu.click();
        sleepForMs(1000);

        // (Source) quality = highest
        final WebElement qualityOptionSource = webDriver.findElement(By.xpath("//div[contains(@class, 'Layout') and contains(text(), '(Source)')]"));
        qualityOptionSource.click();
        sleepForMs(10_000);
    }

    private void sleepForMs(final int millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void captureAndSaveScreenshot(final String fileName) throws IOException
    {
        final byte[] screenshotData = screenshotDriver.getScreenshotAs(OutputType.BYTES);
        final String filePath = INCOMING_DIRECTORY + File.separator + fileName;
        FileUtils.writeByteArrayToFile(new File(filePath), screenshotData);
    }
}
