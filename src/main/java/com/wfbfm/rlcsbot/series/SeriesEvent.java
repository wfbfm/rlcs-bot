package com.wfbfm.rlcsbot.series;

import com.wfbfm.rlcsbot.series.handler.SeriesSnapshotEvaluation;

public class SeriesEvent
{
    private final String eventId;
    private final String seriesId;
    private final Game currentGame;
    private final Score seriesScore;
    private final int bestOf;
    private final int currentGameNumber;
    private final SeriesSnapshotEvaluation evaluation;
    private String commentary = null;

    public SeriesEvent(final Series series, final SeriesSnapshotEvaluation evaluation, final int updateNumber)
    {
        this.eventId = "#" + String.valueOf(updateNumber) + " | " + series.getSeriesId();
        this.seriesId = series.getSeriesId();
        this.currentGame = series.getCurrentGame();
        this.seriesScore = series.getSeriesScore();
        this.bestOf = series.getBestOf();
        this.currentGameNumber = series.getCurrentGameNumber();
        this.evaluation = evaluation;
    }

    public Game getCurrentGame()
    {
        return currentGame;
    }

    public Score getSeriesScore()
    {
        return seriesScore;
    }

    public int getBestOf()
    {
        return bestOf;
    }

    public int getCurrentGameNumber()
    {
        return currentGameNumber;
    }

    public String getSeriesId()
    {
        return seriesId;
    }

    public String getEventId()
    {
        return eventId;
    }

    public SeriesSnapshotEvaluation getEvaluation()
    {
        return this.evaluation;
    }

    public String getCommentary()
    {
        return commentary;
    }

    public void setCommentary(final String commentary)
    {
        this.commentary = commentary;
    }
}
