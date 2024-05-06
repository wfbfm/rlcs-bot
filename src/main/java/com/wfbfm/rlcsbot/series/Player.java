package com.wfbfm.rlcsbot.series;

import java.util.Objects;

public class Player
{
    private String name;

    public Player()
    {
        // default constructor for Jackson deserialisation
    }

    public Player(final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
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
        final Player other = (Player) obj;
        return Objects.equals(name, other.name);
    }
}
