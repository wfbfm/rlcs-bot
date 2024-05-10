package com.wfbfm.rlcsbot.app;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.wfbfm.rlcsbot.series.TeamColour;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.*;

public class ApplicationContext
{
    private String broadcastUrl;
    private String liquipediaUrl;
    private boolean isBroadcastLive;
    private boolean isMidSeriesAllowed;
    private boolean flushWebSocket;
    private boolean abandonSeries;
    private int samplingRateMs;
    private int transcriptionWaitMs;
    private int bestOf;
    private TeamColour gameWinnerOverride = TeamColour.NONE;
    private Map<String, String> uppercaseDisplayToLiquipediaName = new HashMap<>();

    public ApplicationContext(final String broadcastUrl, final String liquipediaUrl, final boolean isBroadcastLive)
    {
        this.broadcastUrl = broadcastUrl;
        this.liquipediaUrl = liquipediaUrl;
        this.isBroadcastLive = isBroadcastLive;
        this.isMidSeriesAllowed = false;
        this.samplingRateMs = SCREENSHOT_INTERVAL_MS;
        this.transcriptionWaitMs = TRANSCRIPTION_WAIT_TIME_MS;
        initialiseDisplayNameCache();
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

    public boolean isMidSeriesAllowed()
    {
        return isMidSeriesAllowed;
    }

    public void setMidSeriesAllowed(final boolean midSeriesAllowed)
    {
        isMidSeriesAllowed = midSeriesAllowed;
    }

    public Map<String, String> getUppercaseDisplayToLiquipediaName()
    {
        return uppercaseDisplayToLiquipediaName;
    }

    public boolean flushWebSocket()
    {
        return flushWebSocket;
    }

    public void setFlushWebSocket(final boolean flushWebSocket)
    {
        this.flushWebSocket = flushWebSocket;
    }

    public boolean abandonSeries()
    {
        return abandonSeries;
    }

    public void setAbandonSeries(final boolean abandonSeries)
    {
        this.abandonSeries = abandonSeries;
    }

    public int getSamplingRateMs()
    {
        return samplingRateMs;
    }

    public void setSamplingRateMs(final int samplingRateMs)
    {
        this.samplingRateMs = samplingRateMs;
    }

    public int getTranscriptionWaitMs()
    {
        return transcriptionWaitMs;
    }

    public void setTranscriptionWaitMs(final int transcriptionWaitMs)
    {
        this.transcriptionWaitMs = transcriptionWaitMs;
    }

    public int getBestOf()
    {
        return bestOf;
    }

    public void setBestOf(final int bestOf)
    {
        this.bestOf = bestOf;
    }

    public TeamColour getGameWinnerOverride()
    {
        return gameWinnerOverride;
    }

    public void setGameWinnerOverride(final TeamColour gameWinnerOverride)
    {
        this.gameWinnerOverride = gameWinnerOverride;
    }

    private void initialiseDisplayNameCache()
    {
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(DISPLAY_NAME_MAPPINGS)).build())
        {
            String[] row;
            while ((row = csvReader.readNext()) != null)
            {
                this.uppercaseDisplayToLiquipediaName.put(row[0], row[1]);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
