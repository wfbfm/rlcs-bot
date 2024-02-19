package com.wfbfm.rlcsbot.series;

public class SeriesSnapshot
{
    private SeriesMetaData seriesMetaData;
    private Game currentGame;
    private int currentGameNumber;
    private Score seriesScore;
    private Team blueTeam;
    private Team orangeTeam;
    private int bestOf;

    public SeriesSnapshot(final SeriesMetaData seriesMetaData, final Game currentGame, final int currentGameNumber, final Score seriesScore, final Team blueTeam, final Team orangeTeam, final int bestOf)
    {
        this.seriesMetaData = seriesMetaData;
        this.currentGame = currentGame;
        this.currentGameNumber = currentGameNumber;
        this.seriesScore = seriesScore;
        this.blueTeam = blueTeam;
        this.orangeTeam = orangeTeam;
        this.bestOf = bestOf;
    }

    public SeriesMetaData getSeriesMetaData()
    {
        return seriesMetaData;
    }

    public void setSeriesMetaData(final SeriesMetaData seriesMetaData)
    {
        this.seriesMetaData = seriesMetaData;
    }

    public Game getCurrentGame()
    {
        return currentGame;
    }

    public void setCurrentGame(final Game currentGame)
    {
        this.currentGame = currentGame;
    }

    public int getCurrentGameNumber()
    {
        return currentGameNumber;
    }

    public void setCurrentGameNumber(final int currentGameNumber)
    {
        this.currentGameNumber = currentGameNumber;
    }

    public Score getSeriesScore()
    {
        return seriesScore;
    }

    public void setSeriesScore(final Score seriesScore)
    {
        this.seriesScore = seriesScore;
    }

    public Team getBlueTeam()
    {
        return blueTeam;
    }

    public void setBlueTeam(final Team blueTeam)
    {
        this.blueTeam = blueTeam;
    }

    public Team getOrangeTeam()
    {
        return orangeTeam;
    }

    public void setOrangeTeam(final Team orangeTeam)
    {
        this.orangeTeam = orangeTeam;
    }

    public int getBestOf()
    {
        return bestOf;
    }

    public void setBestOf(final int bestOf)
    {
        this.bestOf = bestOf;
    }
}
