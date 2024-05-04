package com.wfbfm.rlcsbot.app;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.DISPLAY_NAME_MAPPINGS;

public class ApplicationContext
{
    private String broadcastUrl;
    private String liquipediaUrl;
    private boolean isBroadcastLive;
    private boolean isMidSeriesAllowed;
    private Map<String, String> uppercaseDisplayToLiquipediaName = new HashMap<>();

    public ApplicationContext(final String broadcastUrl, final String liquipediaUrl, final boolean isBroadcastLive)
    {
        this.broadcastUrl = broadcastUrl;
        this.liquipediaUrl = liquipediaUrl;
        this.isBroadcastLive = isBroadcastLive;
        this.isMidSeriesAllowed = false;
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
