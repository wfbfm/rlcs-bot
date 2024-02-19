package com.wfbfm.rlcsbot.series;

import java.util.ArrayList;
import java.util.List;

public class Series
{
    private SeriesMetaData seriesMetaData;
    private List<Game> games;
    private Game currentGame;
    private Score seriesScore;
    private Team blueTeam;
    private Team orangeTeam;
    private int bestOf;
    private int currentGameNumber;

    public Series(final SeriesMetaData seriesMetaData, final Team blueTeam, final Team orangeTeam, final int bestOf)
    {
        this.seriesMetaData = seriesMetaData;
        this.blueTeam = blueTeam;
        this.orangeTeam = orangeTeam;
        this.bestOf = bestOf;
    }

    public Series(final SeriesSnapshot snapshot)
    {
        this.seriesMetaData = snapshot.getSeriesMetaData();
        this.games = new ArrayList<>();
        this.currentGame = snapshot.getCurrentGame();
        this.currentGameNumber = snapshot.getCurrentGameNumber();
        this.games.add(this.currentGame);
        this.seriesScore = snapshot.getSeriesScore();
        this.blueTeam = snapshot.getBlueTeam();
        this.orangeTeam = snapshot.getOrangeTeam();
        this.bestOf = snapshot.getBestOf();
    }

    public SeriesMetaData getSeriesMetaData()
    {
        return seriesMetaData;
    }

    public void setSeriesMetaData(final SeriesMetaData seriesMetaData)
    {
        this.seriesMetaData = seriesMetaData;
    }

    public List<Game> getGames()
    {
        return games;
    }

    public void setGames(final List<Game> games)
    {
        this.games = games;
    }

    public Game getCurrentGame()
    {
        return currentGame;
    }

    public void setCurrentGame(final Game currentGame)
    {
        this.currentGame = currentGame;
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

    public int getCurrentGameNumber()
    {
        return currentGameNumber;
    }

    public void setCurrentGameNumber(final int currentGameNumber)
    {
        this.currentGameNumber = currentGameNumber;
    }
}
