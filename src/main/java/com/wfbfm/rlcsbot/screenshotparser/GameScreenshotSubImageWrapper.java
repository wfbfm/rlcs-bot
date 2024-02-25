package com.wfbfm.rlcsbot.screenshotparser;

import java.awt.image.BufferedImage;

public class GameScreenshotSubImageWrapper
{
    private final String fileName;
    private final BufferedImage blueGameScore;
    private final BufferedImage orangeGameScore;
    private final BufferedImage blueSeriesTick1;
    private final BufferedImage blueSeriesTick2;
    private final BufferedImage blueSeriesTick3;
    private final BufferedImage blueSeriesTick4;
    private final BufferedImage orangeSeriesTick1;
    private final BufferedImage orangeSeriesTick2;
    private final BufferedImage orangeSeriesTick3;
    private final BufferedImage orangeSeriesTick4;
    private final BufferedImage blueTeam;
    private final BufferedImage orangeTeam;
    private final BufferedImage bluePlayer1;
    private final BufferedImage bluePlayer2;
    private final BufferedImage bluePlayer3;
    private final BufferedImage orangePlayer1;
    private final BufferedImage orangePlayer2;
    private final BufferedImage orangePlayer3;
    private final BufferedImage clock;
    private final BufferedImage bestOf;
    private final BufferedImage gameNumber;
    private final BufferedImage description;

    public GameScreenshotSubImageWrapper(final String fileName, final BufferedImage blueGameScore, final BufferedImage orangeGameScore,
                                         final BufferedImage blueSeriesTick1, final BufferedImage blueSeriesTick2,
                                         final BufferedImage blueSeriesTick3, final BufferedImage blueSeriesTick4,
                                         final BufferedImage orangeSeriesTick1, final BufferedImage orangeSeriesTick2,
                                         final BufferedImage orangeSeriesTick3, final BufferedImage orangeSeriesTick4,
                                         final BufferedImage blueTeam, final BufferedImage orangeTeam, final BufferedImage bluePlayer1,
                                         final BufferedImage bluePlayer2, final BufferedImage bluePlayer3,
                                         final BufferedImage orangePlayer1, final BufferedImage orangePlayer2,
                                         final BufferedImage orangePlayer3, final BufferedImage clock, final BufferedImage bestOf,
                                         final BufferedImage gameNumber, final BufferedImage description)
    {
        this.fileName = fileName;
        this.blueGameScore = blueGameScore;
        this.orangeGameScore = orangeGameScore;
        this.blueSeriesTick1 = blueSeriesTick1;
        this.blueSeriesTick2 = blueSeriesTick2;
        this.blueSeriesTick3 = blueSeriesTick3;
        this.blueSeriesTick4 = blueSeriesTick4;
        this.orangeSeriesTick1 = orangeSeriesTick1;
        this.orangeSeriesTick2 = orangeSeriesTick2;
        this.orangeSeriesTick3 = orangeSeriesTick3;
        this.orangeSeriesTick4 = orangeSeriesTick4;
        this.blueTeam = blueTeam;
        this.orangeTeam = orangeTeam;
        this.bluePlayer1 = bluePlayer1;
        this.bluePlayer2 = bluePlayer2;
        this.bluePlayer3 = bluePlayer3;
        this.orangePlayer1 = orangePlayer1;
        this.orangePlayer2 = orangePlayer2;
        this.orangePlayer3 = orangePlayer3;
        this.clock = clock;
        this.bestOf = bestOf;
        this.gameNumber = gameNumber;
        this.description = description;
    }

    public BufferedImage getSubImageByType(final SubImageType subImageType)
    {
        return switch (subImageType)
                {
                    case BLUE_GAME_SCORE -> getBlueGameScore();
                    case ORANGE_GAME_SCORE -> getOrangeGameScore();
                    case BLUE_SERIES_TICK1 -> getBlueSeriesTick1();
                    case BLUE_SERIES_TICK2 -> getBlueSeriesTick2();
                    case BLUE_SERIES_TICK3 -> getBlueSeriesTick3();
                    case BLUE_SERIES_TICK4 -> getBlueSeriesTick4();
                    case ORANGE_SERIES_TICK1 -> getOrangeSeriesTick1();
                    case ORANGE_SERIES_TICK2 -> getOrangeSeriesTick2();
                    case ORANGE_SERIES_TICK3 -> getOrangeSeriesTick3();
                    case ORANGE_SERIES_TICK4 -> getOrangeSeriesTick4();
                    case BLUE_TEAM -> getBlueTeam();
                    case ORANGE_TEAM -> getOrangeTeam();
                    case BLUE_PLAYER1 -> getBluePlayer1();
                    case BLUE_PLAYER2 -> getBluePlayer2();
                    case BLUE_PLAYER3 -> getBluePlayer3();
                    case ORANGE_PLAYER1 -> getOrangePlayer1();
                    case ORANGE_PLAYER2 -> getOrangePlayer2();
                    case ORANGE_PLAYER3 -> getOrangePlayer3();
                    case CLOCK -> getClock();
                    case BEST_OF -> getBestOf();
                    case GAME_NUMBER -> getGameNumber();
                    case DESCRIPTION -> getDescription();
                };
    }

    public String getFileName()
    {
        return fileName;
    }

    public BufferedImage getBlueGameScore()
    {
        return blueGameScore;
    }

    public BufferedImage getOrangeGameScore()
    {
        return orangeGameScore;
    }

    public BufferedImage getBlueSeriesTick1()
    {
        return blueSeriesTick1;
    }

    public BufferedImage getBlueSeriesTick2()
    {
        return blueSeriesTick2;
    }

    public BufferedImage getBlueSeriesTick3()
    {
        return blueSeriesTick3;
    }

    public BufferedImage getBlueSeriesTick4()
    {
        return blueSeriesTick4;
    }

    public BufferedImage getOrangeSeriesTick1()
    {
        return orangeSeriesTick1;
    }

    public BufferedImage getOrangeSeriesTick2()
    {
        return orangeSeriesTick2;
    }

    public BufferedImage getOrangeSeriesTick3()
    {
        return orangeSeriesTick3;
    }

    public BufferedImage getOrangeSeriesTick4()
    {
        return orangeSeriesTick4;
    }

    public BufferedImage getBlueTeam()
    {
        return blueTeam;
    }

    public BufferedImage getOrangeTeam()
    {
        return orangeTeam;
    }

    public BufferedImage getBluePlayer1()
    {
        return bluePlayer1;
    }

    public BufferedImage getBluePlayer2()
    {
        return bluePlayer2;
    }

    public BufferedImage getBluePlayer3()
    {
        return bluePlayer3;
    }

    public BufferedImage getOrangePlayer1()
    {
        return orangePlayer1;
    }

    public BufferedImage getOrangePlayer2()
    {
        return orangePlayer2;
    }

    public BufferedImage getOrangePlayer3()
    {
        return orangePlayer3;
    }

    public BufferedImage getClock()
    {
        return clock;
    }

    public BufferedImage getBestOf()
    {
        return bestOf;
    }

    public BufferedImage getGameNumber()
    {
        return gameNumber;
    }

    public BufferedImage getDescription()
    {
        return description;
    }
}
