package com.wfbfm.rlcsbot.series;

import java.time.LocalDate;

public class SeriesMetaData
{
    private LocalDate date;
    private String seriesDescription;
    private String liquipediaPage;

    public SeriesMetaData()
    {
        // default constructor for Jackson deserialisation
        this.date = LocalDate.now();
        this.seriesDescription = null;
        this.liquipediaPage = null;
    }

    public SeriesMetaData(final LocalDate date, final String seriesDescription, final String liquipediaPage)
    {
        this.date = date;
        this.seriesDescription = seriesDescription;
        this.liquipediaPage = liquipediaPage;
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
