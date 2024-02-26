package com.wfbfm.rlcsbot.series;

import java.util.ArrayList;
import java.util.List;

public class Series
{
    private SeriesMetaData seriesMetaData;
    private List<Game> completedGames;
    private Game currentGame;
    private Score seriesScore;
    private Team blueTeam;
    private Team orangeTeam;
    private int bestOf;
    private int currentGameNumber;
    private boolean isComplete;

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
        this.completedGames = new ArrayList<>();
        this.currentGame = snapshot.getCurrentGame();
        this.currentGameNumber = snapshot.getCurrentGameNumber();
        this.completedGames.add(this.currentGame);
        this.seriesScore = snapshot.getSeriesScore();
        this.blueTeam = snapshot.getBlueTeam();
        this.orangeTeam = snapshot.getOrangeTeam();
        this.bestOf = snapshot.getBestOf();
    }

    public void handleCompletedGame()
    {
        final TeamColour winningTeam = currentGame.getScore().getTeamInLead();
        this.currentGame.setWinner(winningTeam);
        final int newSeriesScore = this.seriesScore.getTeamScore(winningTeam) + 1;
        this.seriesScore.setTeamScore(newSeriesScore, winningTeam);
        this.completedGames.add(this.currentGame);
        this.currentGame = null;
        if (newSeriesScore >= getSeriesWinningGameScore())
        {
            this.isComplete = true;
        }
        else
        {
            this.currentGame = new Game();
        }
    }

    public int getSeriesWinningGameScore()
    {
        return (this.bestOf + 1) / 2;
    }

    public SeriesMetaData getSeriesMetaData()
    {
        return seriesMetaData;
    }

    public void setSeriesMetaData(final SeriesMetaData seriesMetaData)
    {
        this.seriesMetaData = seriesMetaData;
    }

    public List<Game> getCompletedGames()
    {
        return completedGames;
    }

    public void setCompletedGames(final List<Game> completedGames)
    {
        this.completedGames = completedGames;
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

    public boolean isComplete()
    {
        return isComplete;
    }

    public void setComplete(final boolean complete)
    {
        isComplete = complete;
    }
}
