package com.wfbfm.rlcsbot.series;

import java.time.LocalDate;

public class SeriesMetaData
{
    private int seriesId;
    private LocalDate date;
    private String seriesDescription;
    private String liquipediaPage;

    public SeriesMetaData(final int seriesId, final LocalDate date, final String seriesDescription, final String liquipediaPage)
    {
        this.seriesId = seriesId;
        this.date = date;
        this.seriesDescription = seriesDescription;
        this.liquipediaPage = liquipediaPage;
    }

    public int getSeriesId()
    {
        return seriesId;
    }

    public void setSeriesId(final int seriesId)
    {
        this.seriesId = seriesId;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public void setDate(final LocalDate date)
    {
        this.date = date;
    }

    public String getSeriesDescription()
    {
        return seriesDescription;
    }

    public void setSeriesDescription(final String seriesDescription)
    {
        this.seriesDescription = seriesDescription;
    }

    public String getLiquipediaPage()
    {
        return liquipediaPage;
    }

    public void setLiquipediaPage(final String liquipediaPage)
    {
        this.liquipediaPage = liquipediaPage;
    }
}
