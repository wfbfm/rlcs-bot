package com.wfbfm.rlcsbot.series;

public class Clock
{
    private String displayedTime;
    private int elapsedSeconds;
    private boolean isOvertime;

    public Clock()
    {
        // default constructor for Jackson deserialisation
        this.displayedTime = null;
        this.elapsedSeconds = 0;
        this.isOvertime = false;
    }

    public Clock(final String displayedTime, final int elapsedSeconds, final boolean isOvertime)
    {
        this.displayedTime = displayedTime;
        this.elapsedSeconds = elapsedSeconds;
        this.isOvertime = isOvertime;
    }

    public String getDisplayedTime()
    {
        return displayedTime;
    }

    public void setDisplayedTime(final String displayedTime)
    {
        this.displayedTime = displayedTime;
    }

    public int getElapsedSeconds()
    {
        return elapsedSeconds;
    }

    public void setElapsedSeconds(final int elapsedSeconds)
    {
        this.elapsedSeconds = elapsedSeconds;
    }

    public boolean isOvertime()
    {
        return isOvertime;
    }

    public void setOvertime(final boolean overtime)
    {
        isOvertime = overtime;
    }
}
