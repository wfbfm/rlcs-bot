package com.wfbfm.rlcsbot.screenshotparser;


import java.awt.image.BufferedImage;

public class GameScreenshotSubImageWrapperBuilder
{
    private String fileName;
    private BufferedImage blueGameScore;
    private BufferedImage orangeGameScore;
    private BufferedImage blueSeriesTick1;
    private BufferedImage blueSeriesTick2;
    private BufferedImage blueSeriesTick3;
    private BufferedImage blueSeriesTick4;
    private BufferedImage orangeSeriesTick1;
    private BufferedImage orangeSeriesTick2;
    private BufferedImage orangeSeriesTick3;
    private BufferedImage orangeSeriesTick4;
    private BufferedImage blueTeam;
    private BufferedImage orangeTeam;
    private BufferedImage bluePlayer1;
    private BufferedImage bluePlayer2;
    private BufferedImage bluePlayer3;
    private BufferedImage orangePlayer1;
    private BufferedImage orangePlayer2;
    private BufferedImage orangePlayer3;
    private BufferedImage clock;
    private BufferedImage bestOf;
    private BufferedImage gameNumber;
    private BufferedImage description;

    public GameScreenshotSubImageWrapperBuilder withSubImage(final SubImageType subImageType, final BufferedImage subImage)
    {
        switch (subImageType)
        {
            case BLUE_GAME_SCORE -> this.blueGameScore = subImage;
            case ORANGE_GAME_SCORE -> this.orangeGameScore = subImage;
            case BLUE_SERIES_TICK1 -> this.blueSeriesTick1 = subImage;
            case BLUE_SERIES_TICK2 -> this.blueSeriesTick2 = subImage;
            case BLUE_SERIES_TICK3 -> this.blueSeriesTick3 = subImage;
            case BLUE_SERIES_TICK4 -> this.blueSeriesTick4 = subImage;
            case ORANGE_SERIES_TICK1 -> this.orangeSeriesTick1 = subImage;
            case ORANGE_SERIES_TICK2 -> this.orangeSeriesTick2 = subImage;
            case ORANGE_SERIES_TICK3 -> this.orangeSeriesTick3 = subImage;
            case ORANGE_SERIES_TICK4 -> this.orangeSeriesTick4 = subImage;
            case BLUE_TEAM -> this.blueTeam = subImage;
            case ORANGE_TEAM -> this.orangeTeam = subImage;
            case BLUE_PLAYER1 -> this.bluePlayer1 = subImage;
            case BLUE_PLAYER2 -> this.bluePlayer2 = subImage;
            case BLUE_PLAYER3 -> this.bluePlayer3 = subImage;
            case ORANGE_PLAYER1 -> this.orangePlayer1 = subImage;
            case ORANGE_PLAYER2 -> this.orangePlayer2 = subImage;
            case ORANGE_PLAYER3 -> this.orangePlayer3 = subImage;
            case CLOCK -> this.clock = subImage;
            case BEST_OF -> this.bestOf = subImage;
            case GAME_NUMBER -> this.gameNumber = subImage;
            case DESCRIPTION -> this.description = subImage;
        }
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withFileName(final String fileName)
    {
        this.fileName = fileName;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withBlueGameScore(final BufferedImage blueGameScore)
    {
        this.blueGameScore = blueGameScore;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withOrangeGameScore(final BufferedImage orangeGameScore)
    {
        this.orangeGameScore = orangeGameScore;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withBlueSeriesTick1(final BufferedImage blueSeriesTick1)
    {
        this.blueSeriesTick1 = blueSeriesTick1;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withBlueSeriesTick2(final BufferedImage blueSeriesTick2)
    {
        this.blueSeriesTick2 = blueSeriesTick2;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withBlueSeriesTick3(final BufferedImage blueSeriesTick3)
    {
        this.blueSeriesTick3 = blueSeriesTick3;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withBlueSeriesTick4(final BufferedImage blueSeriesTick4)
    {
        this.blueSeriesTick4 = blueSeriesTick4;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withOrangeSeriesTick1(final BufferedImage orangeSeriesTick1)
    {
        this.orangeSeriesTick1 = orangeSeriesTick1;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withOrangeSeriesTick2(final BufferedImage orangeSeriesTick2)
    {
        this.orangeSeriesTick2 = orangeSeriesTick2;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withOrangeSeriesTick3(final BufferedImage orangeSeriesTick3)
    {
        this.orangeSeriesTick3 = orangeSeriesTick3;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withOrangeSeriesTick4(final BufferedImage orangeSeriesTick4)
    {
        this.orangeSeriesTick4 = orangeSeriesTick4;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withBlueTeam(final BufferedImage blueTeam)
    {
        this.blueTeam = blueTeam;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withOrangeTeam(final BufferedImage orangeTeam)
    {
        this.orangeTeam = orangeTeam;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withBluePlayer1(final BufferedImage bluePlayer1)
    {
        this.bluePlayer1 = bluePlayer1;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withBluePlayer2(final BufferedImage bluePlayer2)
    {
        this.bluePlayer2 = bluePlayer2;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withBluePlayer3(final BufferedImage bluePlayer3)
    {
        this.bluePlayer3 = bluePlayer3;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withOrangePlayer1(final BufferedImage orangePlayer1)
    {
        this.orangePlayer1 = orangePlayer1;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withOrangePlayer2(final BufferedImage orangePlayer2)
    {
        this.orangePlayer2 = orangePlayer2;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withOrangePlayer3(final BufferedImage orangePlayer3)
    {
        this.orangePlayer3 = orangePlayer3;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withClock(final BufferedImage clock)
    {
        this.clock = clock;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withBestOf(final BufferedImage bestOf)
    {
        this.bestOf = bestOf;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withGameNumber(final BufferedImage gameNumber)
    {
        this.gameNumber = gameNumber;
        return this;
    }

    public GameScreenshotSubImageWrapperBuilder withDescription(final BufferedImage description)
    {
        this.description = description;
        return this;
    }

    public GameScreenshotSubImageWrapper build()
    {
        return new GameScreenshotSubImageWrapper(
                fileName, blueGameScore, orangeGameScore,
                blueSeriesTick1, blueSeriesTick2, blueSeriesTick3, blueSeriesTick4,
                orangeSeriesTick1, orangeSeriesTick2, orangeSeriesTick3, orangeSeriesTick4,
                blueTeam, orangeTeam, bluePlayer1, bluePlayer2, bluePlayer3,
                orangePlayer1, orangePlayer2, orangePlayer3,
                clock, bestOf, gameNumber, description
        );
    }

    public void clear()
    {
        this.fileName = null;
        this.blueGameScore = null;
        this.orangeGameScore = null;
        this.blueSeriesTick1 = null;
        this.blueSeriesTick2 = null;
        this.blueSeriesTick3 = null;
        this.blueSeriesTick4 = null;
        this.orangeSeriesTick1 = null;
        this.orangeSeriesTick2 = null;
        this.orangeSeriesTick3 = null;
        this.orangeSeriesTick4 = null;
        this.blueTeam = null;
        this.orangeTeam = null;
        this.bluePlayer1 = null;
        this.bluePlayer2 = null;
        this.bluePlayer3 = null;
        this.orangePlayer1 = null;
        this.orangePlayer2 = null;
        this.orangePlayer3 = null;
        this.clock = null;
        this.bestOf = null;
        this.gameNumber = null;
        this.description = null;
    }
}
