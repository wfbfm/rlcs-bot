package com.wfbfm.rlcsbot.series;

public class Score
{
    private int blueScore;
    private int orangeScore;

    public Score(final int blueScore, final int orangeScore)
    {
        this.blueScore = blueScore;
        this.orangeScore = orangeScore;
    }

    public int getBlueScore()
    {
        return blueScore;
    }

    public int getOrangeScore()
    {
        return orangeScore;
    }

    public void setBlueScore(final int blueScore)
    {
        this.blueScore = blueScore;
    }

    public void setOrangeScore(final int orangeScore)
    {
        this.orangeScore = orangeScore;
    }

    public int getTeamScore(final TeamColour teamColour)
    {
        if (teamColour == TeamColour.BLUE)
        {
            return this.blueScore;
        }
        else
        {
            return this.orangeScore;
        }
    }

    public void setTeamScore(final int score, final TeamColour teamColour)
    {
        if (teamColour == TeamColour.BLUE)
        {
            this.blueScore = score;
        }
        else
        {
            this.orangeScore = score;
        }
    }
}
