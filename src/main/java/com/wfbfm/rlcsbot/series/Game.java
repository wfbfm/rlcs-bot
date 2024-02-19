package com.wfbfm.rlcsbot.series;

public class Game
{
    private Score score;
    private Clock clock;
    private TeamColour winner;

    public Game()
    {
        this.score = new Score(0, 0);
        this.winner = null;
    }

    public Score getScore()
    {
        return score;
    }

    public void setScore(final Score score)
    {
        this.score = score;
    }

    public Clock getClock()
    {
        return clock;
    }

    public void setClock(final Clock clock)
    {
        this.clock = clock;
    }

    public TeamColour getWinner()
    {
        return winner;
    }

    public void setWinner(final TeamColour winner)
    {
        this.winner = winner;
    }
}
