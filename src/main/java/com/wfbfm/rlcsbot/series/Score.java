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
        switch (teamColour)
        {
            case BLUE:
                return this.blueScore;
            case ORANGE:
                return this.orangeScore;
            default:
                return 0;
        }
    }

    public void setTeamScore(final int score, final TeamColour teamColour)
    {
        switch (teamColour)
        {
            case BLUE:
                this.blueScore = score;
                break;
            case ORANGE:
                this.orangeScore = score;
                break;
        }
    }

    public TeamColour getTeamInLead()
    {
        if (blueScore > orangeScore)
        {
            return TeamColour.BLUE;
        }
        else if (blueScore < orangeScore)
        {
            return TeamColour.ORANGE;
        }
        else
        {
            return TeamColour.NONE;
        }
    }

    public int getHighestScore()
    {
        if (blueScore > orangeScore)
        {
            return blueScore;
        }
        else
        {
            return orangeScore;
        }
    }
}
