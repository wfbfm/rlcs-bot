package com.wfbfm.rlcsbot.series;

import java.util.Objects;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.GAME_TIME_SECONDS;

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

    public Clock(final int elapsedSeconds, final boolean isOvertime)
    {
        this.displayedTime = calculateDisplayedTime(elapsedSeconds, isOvertime);
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

    private String calculateDisplayedTime(final int elapsedSeconds, final boolean isOvertime)
    {
        if (isOvertime)
        {
            final int gameTime = elapsedSeconds - GAME_TIME_SECONDS;
            final int minutes = gameTime / 60;
            final int seconds = gameTime % 60;
            return "+" + String.valueOf(minutes) + ":" + String.format("%02d", seconds);
        }
        else
        {
            if (elapsedSeconds >= GAME_TIME_SECONDS)
            {
                return "0:00";
            }
            else
            {
                final int gameTime = GAME_TIME_SECONDS - elapsedSeconds;
                final int minutes = gameTime / 60;
                final int seconds = gameTime % 60;
                return String.valueOf(minutes) + ":" + String.format("%02d", seconds);
            }
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        final Clock other = (Clock) obj;
        return elapsedSeconds == other.elapsedSeconds &&
                isOvertime == other.isOvertime &&
                Objects.equals(displayedTime, other.displayedTime);
    }
}
