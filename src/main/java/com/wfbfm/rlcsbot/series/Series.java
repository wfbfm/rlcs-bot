package com.wfbfm.rlcsbot.series;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Series
{
    private final String seriesId;
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
        this.seriesId = createSeriesId();
    }

    public Series(final SeriesSnapshot snapshot)
    {
        this.seriesMetaData = snapshot.getSeriesMetaData();
        this.completedGames = new ArrayList<>();
        this.currentGame = snapshot.getCurrentGame();
        this.currentGameNumber = snapshot.getCurrentGameNumber();
        this.seriesScore = snapshot.getSeriesScore();
        this.blueTeam = snapshot.getBlueTeam();
        this.orangeTeam = snapshot.getOrangeTeam();
        this.bestOf = snapshot.getBestOf();
        this.seriesId = createSeriesId();
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
            this.currentGameNumber++;
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

    private String createSeriesId()
    {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.blueTeam.getTeamName());
        stringBuilder.append(" | ");
        stringBuilder.append(this.orangeTeam.getTeamName());
        stringBuilder.append(" | ");
        stringBuilder.append(this.seriesMetaData.getDate());
        stringBuilder.append(" | ");
        stringBuilder.append(Instant.now().toEpochMilli());
        return stringBuilder.toString();
    }

    public String getSeriesId()
    {
        return seriesId;
    }

    @Override
    public String toString()
    {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append(blueTeam.getTeamName() + " [" + blueTeam.getPlayerNames() + "]" + " vs "
                + orangeTeam.getTeamName() + " [" + orangeTeam.getPlayerNames() + "]");
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append(currentGame.toString());
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Series Score: " + seriesScore.getBlueScore() + " - " + seriesScore.getOrangeScore());
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("bestOf=" + bestOf + " currentGameNumber=" + currentGameNumber + " isComplete=" + isComplete +
                " numberOfCompletedGames=" + completedGames.size());
        return stringBuilder.toString();
    }
}
