package com.wfbfm.rlcsbot.series;

public class Game
{
    private Score score;
    private Clock clock;
    private TeamColour winner;

    public Game()
    {
        this.score = new Score(0, 0);
        this.clock = new Clock("5:00", 0, false);
        this.winner = TeamColour.NONE;
    }

    public Game(final Score score, final Clock clock, final TeamColour winner)
    {
        this.score = score;
        this.clock = clock;
        this.winner = winner;
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

    @Override
    public String toString()
    {
        return "Game Score: " + score.getBlueScore() + " - " + score.getOrangeScore() + " (Time: " + clock.getDisplayedTime() + ")";
    }
}
