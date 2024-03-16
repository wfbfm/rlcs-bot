package com.wfbfm.rlcsbot.series;

public class Score
{
    private int blueScore;
    private int orangeScore;
    private TeamColour teamInLead;

    public Score()
    {
        // default constructor for Jackson deserialisation
        this.blueScore = 0;
        this.orangeScore = 0;
        this.teamInLead = TeamColour.NONE;
    }

    public Score(final int blueScore, final int orangeScore)
    {
        this.blueScore = blueScore;
        this.orangeScore = orangeScore;
        assignTeamInLead();
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
        assignTeamInLead();
    }

    private void assignTeamInLead()
    {
        if (blueScore > orangeScore)
        {
            this.teamInLead = TeamColour.BLUE;
        }
        else if (blueScore < orangeScore)
        {
            this.teamInLead = TeamColour.ORANGE;
        }
        else
        {
            this.teamInLead = TeamColour.NONE;
        }
    }

    public TeamColour getTeamInLead()
    {
        assignTeamInLead();
        return teamInLead;
    }
}
