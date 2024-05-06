package com.wfbfm.rlcsbot.series;

import java.util.Objects;

public class Team
{
    private Player player1;
    private Player player2;
    private Player player3;

    private TeamColour teamColour;
    private String teamName;
    private String playerNames;

    public Team()
    {
        // default constructor for Jackson deserialisation
    }

    public Team(final String teamName,
                final Player player1,
                final Player player2,
                final Player player3,
                final TeamColour teamColour)
    {
        this.teamName = teamName;
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.teamColour = teamColour;
        this.playerNames = setPlayerNames();
    }

    public String getTeamName()
    {
        return teamName;
    }

    public void setTeamName(final String teamName)
    {
        this.teamName = teamName;
    }

    public TeamColour getTeamColour()
    {
        return teamColour;
    }

    public void setTeamColour(final TeamColour teamColour)
    {
        this.teamColour = teamColour;
    }

    public Player getPlayer1()
    {
        return player1;
    }

    public void setPlayer1(final Player player1)
    {
        this.player1 = player1;
    }

    public Player getPlayer2()
    {
        return player2;
    }

    public void setPlayer2(final Player player2)
    {
        this.player2 = player2;
    }

    public Player getPlayer3()
    {
        return player3;
    }

    public void setPlayer3(final Player player3)
    {
        this.player3 = player3;
    }

    public String setPlayerNames()
    {
        return this.player1.getName() + " | " + this.player2.getName() + " | " + this.player3.getName();
    }

    public String getPlayerNames()
    {
        return this.playerNames;
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
        final Team other = (Team) obj;
        return Objects.equals(player1, other.player1) &&
                Objects.equals(player2, other.player2) &&
                Objects.equals(player3, other.player3) &&
                teamColour == other.teamColour &&
                Objects.equals(teamName, other.teamName) &&
                Objects.equals(playerNames, other.playerNames);
    }
}
