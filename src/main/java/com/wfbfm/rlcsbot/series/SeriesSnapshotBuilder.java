package com.wfbfm.rlcsbot.series;

public class SeriesSnapshotBuilder
{
    private SeriesMetaData seriesMetaData;
    private Game currentGame;
    private int currentGameNumber;
    private Score seriesScore;
    private Team blueTeam;
    private Team orangeTeam;
    private int bestOf;

    public SeriesSnapshotBuilder withSeriesMetaData(final SeriesMetaData seriesMetaData)
    {
        this.seriesMetaData = seriesMetaData;
        return this;
    }

    public SeriesSnapshotBuilder withCurrentGame(final Game currentGame)
    {
        this.currentGame = currentGame;
        return this;
    }

    public SeriesSnapshotBuilder withCurrentGameNumber(final int currentGameNumber)
    {
        this.currentGameNumber = currentGameNumber;
        return this;
    }

    public SeriesSnapshotBuilder withSeriesScore(final Score seriesScore)
    {
        this.seriesScore = seriesScore;
        return this;
    }

    public SeriesSnapshotBuilder withBlueTeam(final Team blueTeam)
    {
        this.blueTeam = blueTeam;
        return this;
    }

    public SeriesSnapshotBuilder withOrangeTeam(final Team orangeTeam)
    {
        this.orangeTeam = orangeTeam;
        return this;
    }

    public SeriesSnapshotBuilder withBestOf(final int bestOf)
    {
        this.bestOf = bestOf;
        return this;
    }

    public SeriesSnapshot build()
    {
        return new SeriesSnapshot(seriesMetaData, currentGame, currentGameNumber, seriesScore, blueTeam, orangeTeam, bestOf);
    }

    public void clear()
    {
        this.seriesMetaData = null;
        this.currentGame = null;
        this.currentGameNumber = 0;
        this.seriesScore = null;
        this.blueTeam = null;
        this.orangeTeam = null;
        this.bestOf = 0;
    }
}