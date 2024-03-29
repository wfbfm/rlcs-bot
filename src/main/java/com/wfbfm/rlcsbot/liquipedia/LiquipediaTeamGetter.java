package com.wfbfm.rlcsbot.liquipedia;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LiquipediaTeamGetter
{
    private static final String CLASS = "class";
    private static final String TEAMCARD_COLUMN = "teamcard-column";
    private static final String CENTER = "center";
    private static final String TEAMCARD_INNER = "teamcard-inner";
    private final Logger logger = Logger.getLogger(LiquipediaTeamGetter.class.getName());
    private Map<String, Map<String, String>> teamToPlayerAndCoachMap = new HashMap<>();
    private Map<String, Set<String>> teamToPlayerNameMap = new HashMap<>();
    private Map<String, String> playerToTeamNameMap = new HashMap<>();
    private Map<String, String> uppercasePlayerNameMap = new HashMap<>();
    private Map<String, String> uppercaseTeamNameMap = new HashMap<>();
    private Map<String, String> uppercaseDisplayToLiquipediaName = new HashMap<>();
    private String liquipediaUrl;

    public LiquipediaTeamGetter()
    {
        this.liquipediaUrl = null;
        initialiseDisplayNameCache();
    }

    private void initialiseDisplayNameCache()
    {
        // TODO: flesh this out, derive from config file
        this.uppercaseDisplayToLiquipediaName.put("FURIA", "FURIA Esports");
        this.uppercaseDisplayToLiquipediaName.put("YANXNZ^^", "yANXNZ");
        this.uppercaseDisplayToLiquipediaName.put("RADOSINHO", "Radosin");

        this.uppercaseDisplayToLiquipediaName.put("GENG MOBIL1", "Gen.G Mobil1 Racing");
    }

    public String getLiquipediaUrl()
    {
        return liquipediaUrl;
    }

    public void setLiquipediaUrl(String liquipediaUrl)
    {
        this.liquipediaUrl = liquipediaUrl;
    }

    public Map<String, Map<String, String>> getTeamToPlayerAndCoachMap()
    {
        return teamToPlayerAndCoachMap;
    }

    public Map<String, Set<String>> getTeamToPlayerNameMap()
    {
        return teamToPlayerNameMap;
    }

    public Map<String, String> getPlayerToTeamNameMap()
    {
        return playerToTeamNameMap;
    }

    public Map<String, String> getUppercasePlayerNameMap()
    {
        return uppercasePlayerNameMap;
    }

    public Map<String, String> getUppercaseTeamNameMap()
    {
        return uppercaseTeamNameMap;
    }

    public Map<String, String> getUppercaseDisplayToLiquipediaName()
    {
        return uppercaseDisplayToLiquipediaName;
    }

    public boolean updateLiquipediaRefData()
    {
        logger.log(Level.INFO, "Attempting to fetch player/team data from Liquipedia: " + liquipediaUrl);
        final Document document = getDocumentFromLiquipediaUrl(liquipediaUrl);
        if (document == null)
        {
            return false;
        }
        final Elements teamCards = document.getElementsByAttributeValueContaining(CLASS, TEAMCARD_COLUMN);

        if (teamCards.size() == 0)
        {
            logger.log(Level.SEVERE, "Unable to find player/team data from Liquipedia: " + liquipediaUrl);
            return false;
        }
        else
        {
            parseTeamToPlayerMap(teamCards.get(0));
            logger.log(Level.INFO, "Fetched " + uppercasePlayerNameMap.size() + " players from Liquipedia.");
            logger.log(Level.INFO, "Fetched " + uppercaseTeamNameMap.size() + " teams from Liquipedia.");
            return true;
        }
    }

    private void parseTeamToPlayerMap(Element teamCard)
    {
        teamToPlayerAndCoachMap.clear();

        final Elements teamNameHolders = teamCard.select(CENTER);

        final Elements teamCardInners = teamCard.getElementsByAttributeValue(CLASS, TEAMCARD_INNER);


        for (int i = 0; i < teamCardInners.size(); i++)
        {
            Element teamCardInner = teamCardInners.get(i);
            // parse teamCardInner for player names
            // data-toggle-area-content=1 ignores any Substitute data
            final Element table = teamCardInner.select("table[data-toggle-area-content=1]").get(0);

            final Elements rows = table.select("tr");

            final Map<String, String> playerAndCoachMap = new HashMap<String, String>();
            final Set<String> playerSet = new HashSet<>();
            for (int j = 0; j < rows.size(); j++)
            {
                final Elements colVals = rows.get(j).select("th,td");
                final String playerId = colVals.get(0).text();
                if (playerId.length() > 1)
                {
                    // ignore any substitutes row interfering with this data
                    continue;
                }
                final String playerName = colVals.get(1).text();

                playerAndCoachMap.put(playerId, playerName);
                if (!playerId.equals("C"))
                {
                    playerSet.add(playerName);
                }
                uppercasePlayerNameMap.put(playerName.toUpperCase(), playerName);
            }

            // parse teamNameHolder for teamName
            String teamName = null;
            if (teamNameHolders.get(i).select("a").size() == 0)
            {
                // case where team is TBD
                teamName = teamNameHolders.get(i).text();
            }
            if (teamNameHolders.get(i).select("a").size() == 1)
            {
                // usual case where teamName is nested
                teamName = teamNameHolders.get(i).select("a").get(0).text();
            }
            if (teamNameHolders.get(i).select("a").size() > 1)
            {
                // skip over the Flag that is added for international event pages
                teamName = teamNameHolders.get(i).select("a").get(1).text();
            }

            // Add to maps
            teamToPlayerAndCoachMap.put(teamName, playerAndCoachMap);
            teamToPlayerNameMap.put(teamName, playerSet);
            uppercaseTeamNameMap.put(teamName.toUpperCase(), teamName);
            for (final String player : playerSet)
            {
                playerToTeamNameMap.put(player, teamName);
            }
        }
    }

    private static Document getDocumentFromLiquipediaUrl(String url)
    {
        final Connection connection = Jsoup.connect(url)
                .header("User-Agent", "RLCommentaryService")
                .header("Accept-Encoding", "gzip");
        try
        {
            return connection.get();
        }
        catch (IOException e)
        {
            return null;
        }
    }
}
