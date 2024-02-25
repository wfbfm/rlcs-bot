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

public class LiquipediaTeamGetter
{
    private static final String CLASS = "class";
    private static final String TEAMCARD_COLUMN = "teamcard-column";
    private static final String CENTER = "center";
    private static final String TEAMCARD_INNER = "teamcard-inner";
    private Map<String, Map<String, String>> teamToPlayerAndCoachMap = new HashMap<>();
    private Map<String, Set<String>> teamToPlayerNameMap = new HashMap<>();
    private Map<String, String> playerToTeamNameMap = new HashMap<>();
    private Map<String, String> uppercasePlayerNameMap = new HashMap<>();
    private Map<String, String> uppercaseTeamNameMap = new HashMap<>();
    private String liquipediaUrl;

    public LiquipediaTeamGetter()
    {
        this.liquipediaUrl = null;
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

    public boolean updateLiquipediaRefData()
    {
        final Document document = getDocumentFromLiquipediaUrl(liquipediaUrl);
        if (document == null)
        {
            return false;
        }
        final Elements teamCards = document.getElementsByAttributeValueContaining(CLASS, TEAMCARD_COLUMN);

        if (teamCards.size() == 0)
        {
            return false;
        }
        else
        {
            parseTeamToPlayerMap(teamCards.get(0));
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
