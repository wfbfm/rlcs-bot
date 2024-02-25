package com.wfbfm.rlcsbot.screenshotparser;

import com.wfbfm.rlcsbot.series.*;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.DEBUGGING_ENABLED;
import static com.wfbfm.rlcsbot.app.RuntimeConstants.LIQUIPEDIA_PAGE;

public class ScreenshotToSeriesTransformer
{
    private static final String TESSERACT_DATA_PATH = "src/main/resources/tessdata";
    private static final String TESSERACT_LANGUAGE = "eng";
    private static final SubImageType[] blueSeriesTicks = {SubImageType.BLUE_SERIES_TICK1,
            SubImageType.BLUE_SERIES_TICK2,
            SubImageType.BLUE_SERIES_TICK3,
            SubImageType.BLUE_SERIES_TICK4};
    private static final SubImageType[] orangeSeriesTicks = {SubImageType.ORANGE_SERIES_TICK1,
            SubImageType.ORANGE_SERIES_TICK2,
            SubImageType.ORANGE_SERIES_TICK3,
            SubImageType.ORANGE_SERIES_TICK4};
    private static final int TICK_COLOUR_DIFFERENTIATION_BUFFER = 50;
    private static final int TICK_FILLED_THRESHOLD = 150;
    private final Logger logger = Logger.getLogger(ScreenshotToSeriesTransformer.class.getName());
    private final SeriesSnapshotBuilder seriesSnapshotBuilder = new SeriesSnapshotBuilder();
    private final Tesseract textTesseract = new Tesseract();
    private final Tesseract clockTesseract = new Tesseract();
    private final Tesseract numberTesseract = new Tesseract();
    private GameScreenshotSubImageWrapper subImageWrapper;

    public ScreenshotToSeriesTransformer()
    {
        initialiseTesseractParsers();
    }

    private void initialiseTesseractParsers()
    {
        textTesseract.setDatapath(TESSERACT_DATA_PATH);
        textTesseract.setLanguage(TESSERACT_LANGUAGE);
        textTesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SPARSE_TEXT);

        clockTesseract.setDatapath(TESSERACT_DATA_PATH);
        clockTesseract.setLanguage(TESSERACT_LANGUAGE);
        clockTesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SPARSE_TEXT);
        clockTesseract.setVariable("tessedit_char_whitelist", "0123456789:+");

        numberTesseract.setDatapath(TESSERACT_DATA_PATH);
        numberTesseract.setLanguage(TESSERACT_LANGUAGE);
        numberTesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SPARSE_TEXT);
        numberTesseract.setVariable("tessedit_char_whitelist", "0123456789");
    }

    public SeriesSnapshot transform(final GameScreenshotSubImageWrapper subImageWrapper)
    {
        final Instant startTime = Instant.now();
        this.subImageWrapper = subImageWrapper;
        seriesSnapshotBuilder.clear();
        parseSeriesMetadata();
        parseCurrentGame();
        parseCurrentGameNumber();
        parseSeriesScore();
        parseTeams();
        parseBestOf();
        final Instant endTime = Instant.now();
        final long elapsedMs = endTime.toEpochMilli() - startTime.toEpochMilli();
        logger.log(Level.INFO, String.format("Time to parse subImages: %d ms", elapsedMs));
        return seriesSnapshotBuilder.build();
    }

    private String parseImage(final Tesseract tesseract, final SubImageType subImageType)
    {
        final BufferedImage image = this.subImageWrapper.getSubImageByType(subImageType);
        try
        {
            final String text = tesseract.doOCR(image);
            if (DEBUGGING_ENABLED)
            {
                logger.log(Level.INFO, subImageWrapper.getFileName() + "|" + subImageType.name() + ": " + text);
            }
            return text.trim();
        }
        catch (TesseractException e)
        {
            logger.log(Level.INFO, "Error parsing image " + subImageWrapper.getFileName() + "|" + subImageType.name(), e);
            return null;
        }
    }

    private void parseSeriesMetadata()
    {
        final String description = parseImage(textTesseract, SubImageType.DESCRIPTION);
        final SeriesMetaData seriesMetaData = new SeriesMetaData(LocalDate.now(), description, LIQUIPEDIA_PAGE);
        this.seriesSnapshotBuilder.withSeriesMetaData(seriesMetaData);
    }

    private void parseCurrentGame()
    {
        final Clock clock = parseClock();
        final TeamColour winner = TeamColour.NONE; // game presumed still in progress - winner is unknown
        final Score gameScore = parseGameScore();
        final Game currentGame = new Game(gameScore, clock, winner);
        this.seriesSnapshotBuilder.withCurrentGame(currentGame);
    }

    private void parseCurrentGameNumber()
    {
        final String gameNumberString = parseImage(textTesseract, SubImageType.GAME_NUMBER);
        if (StringUtils.isEmpty(gameNumberString))
        {
            logger.log(Level.INFO, "Unable to determine game number - defaulting to 0.");
            this.seriesSnapshotBuilder.withCurrentGameNumber(0);
            return;
        }
        final String gameNumber = gameNumberString.replaceAll("[^0-9]", "");
        if (StringUtils.isNumeric(gameNumber))
        {
            this.seriesSnapshotBuilder.withCurrentGameNumber(Integer.parseInt(gameNumber));
        }
        else
        {
            logger.log(Level.INFO, "Unable to determine game number - defaulting to 0.");
        }
    }

    private Score parseGameScore()
    {
        final String blueScoreString = parseImage(numberTesseract, SubImageType.BLUE_GAME_SCORE);
        final String orangeScoreString = parseImage(numberTesseract, SubImageType.ORANGE_GAME_SCORE);

        final int blueScore = StringUtils.isEmpty(blueScoreString) ? 0 : Character.getNumericValue(blueScoreString.charAt(0));
        final int orangeScore = StringUtils.isEmpty(orangeScoreString) ? 0 : Character.getNumericValue(orangeScoreString.charAt(0));

        return new Score(blueScore, orangeScore);
    }

    private void parseSeriesScore()
    {
        final int blueSeriesScore = parseSeriesTicks(TeamColour.BLUE, blueSeriesTicks);
        final int orangeSeriesScore = parseSeriesTicks(TeamColour.ORANGE, orangeSeriesTicks);
        logger.log(Level.INFO, "Parsed series score: " + blueSeriesScore + "-" + orangeSeriesScore);
        this.seriesSnapshotBuilder.withSeriesScore(new Score(blueSeriesScore, orangeSeriesScore));
    }

    private int parseSeriesTicks(final TeamColour teamColour, final SubImageType[] seriesTicks)
    {
        for (int i = seriesTicks.length; i > 0; i--)
        {
            if (isSeriesTickFilled(seriesTicks[i - 1], teamColour))
            {
                return i;
            }
        }
        return 0;
    }

    private boolean isSeriesTickFilled(final SubImageType subImageType, final TeamColour teamColour)
    {
        final BufferedImage image = this.subImageWrapper.getSubImageByType(subImageType);

        final int width = image.getWidth();
        final int height = image.getHeight();

        int filledPixelCount = 0;
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                final Color pixelColour = new Color(image.getRGB(x, y));
                if (isFilledPixel(pixelColour, teamColour))
                {
                    filledPixelCount++;
                }
            }
        }
        return filledPixelCount > TICK_FILLED_THRESHOLD;
    }

    private boolean isFilledPixel(final Color pixelColour, final TeamColour teamColour)
    {
        final int blue = pixelColour.getBlue();
        final int green = pixelColour.getGreen();
        final int red = pixelColour.getRed();

        switch (teamColour)
        {
            case BLUE:
                return blue > (green + TICK_COLOUR_DIFFERENTIATION_BUFFER) && blue > (red + TICK_COLOUR_DIFFERENTIATION_BUFFER);
            case ORANGE:
                return red > (green + TICK_COLOUR_DIFFERENTIATION_BUFFER) && red > (blue + TICK_COLOUR_DIFFERENTIATION_BUFFER);
            default:
                return false;
        }
    }

    private void parseTeams()
    {
        final String blueTeamName = parseImage(textTesseract, SubImageType.BLUE_TEAM);
        final Player bluePlayer1 = new Player(parseImage(textTesseract, SubImageType.BLUE_PLAYER1));
        final Player bluePlayer2 = new Player(parseImage(textTesseract, SubImageType.BLUE_PLAYER2));
        final Player bluePlayer3 = new Player(parseImage(textTesseract, SubImageType.BLUE_PLAYER3));
        final Team blueTeam = new Team(blueTeamName, bluePlayer1, bluePlayer2, bluePlayer3, TeamColour.BLUE);
        this.seriesSnapshotBuilder.withBlueTeam(blueTeam);

        final String orangeTeamName = parseImage(textTesseract, SubImageType.ORANGE_TEAM);
        final Player orangePlayer1 = new Player(parseImage(textTesseract, SubImageType.ORANGE_PLAYER1));
        final Player orangePlayer2 = new Player(parseImage(textTesseract, SubImageType.ORANGE_PLAYER2));
        final Player orangePlayer3 = new Player(parseImage(textTesseract, SubImageType.ORANGE_PLAYER3));
        final Team orangeTeam = new Team(orangeTeamName, orangePlayer1, orangePlayer2, orangePlayer3, TeamColour.ORANGE);
        this.seriesSnapshotBuilder.withOrangeTeam(orangeTeam);
    }

    private Clock parseClock()
    {
        final String displayedTime = parseImage(clockTesseract, SubImageType.CLOCK);
        final boolean isOvertime = displayedTime.contains("+");

        final String[] clockParts = displayedTime.split(":");

        int elapsedSeconds = 300;
        if (clockParts.length != 2)
        {
            logger.log(Level.INFO, "Unable to parse game time from: " + displayedTime);
        }
        else
        {
            if (isOvertime) // clock is counting up
            {
                elapsedSeconds += (Integer.parseInt(clockParts[0]) * 60);
                elapsedSeconds += Integer.parseInt(clockParts[1]);
            }
            else // clock is counting down
            {
                elapsedSeconds -= (Integer.parseInt(clockParts[0]) * 60);
                elapsedSeconds -= Integer.parseInt(clockParts[1]);
            }
        }
        return new Clock(displayedTime, elapsedSeconds, isOvertime);
    }

    private void parseBestOf()
    {
        final String bestOfString = parseImage(textTesseract, SubImageType.GAME_NUMBER);
        if (StringUtils.isEmpty(bestOfString))
        {
            logger.log(Level.INFO, "Unable to determine bestOf - defaulting to 0.");
            this.seriesSnapshotBuilder.withBestOf(0);
            return;
        }
        final String bestOf = bestOfString.replaceAll("[^0-9]", "");
        if (StringUtils.isNumeric(bestOf))
        {
            this.seriesSnapshotBuilder.withBestOf(Integer.parseInt(bestOf));
        }
        else
        {
            logger.log(Level.INFO, "Unable to determine bestOf - defaulting to 0.");
        }
    }
}
