package com.wfbfm.rlcsbot.app;

public class ApplicationContext
{
    private String broadcastUrl;
    private String liquipediaUrl;
    private boolean isBroadcastLive;

    public ApplicationContext(final String broadcastUrl, final String liquipediaUrl, final boolean isBroadcastLive)
    {
        this.broadcastUrl = broadcastUrl;
        this.liquipediaUrl = liquipediaUrl;
        this.isBroadcastLive = isBroadcastLive;
    }

    public String getBroadcastUrl()
    {
        return broadcastUrl;
    }

    public void setBroadcastUrl(final String broadcastUrl)
    {
        this.broadcastUrl = broadcastUrl;
    }

    public String getLiquipediaUrl()
    {
        return liquipediaUrl;
    }

    public void setLiquipediaUrl(final String liquipediaUrl)
    {
        this.liquipediaUrl = liquipediaUrl;
    }

    public boolean isBroadcastLive()
    {
        return isBroadcastLive;
    }

    public void setBroadcastLive(final boolean broadcastLive)
    {
        isBroadcastLive = broadcastLive;
    }
}
