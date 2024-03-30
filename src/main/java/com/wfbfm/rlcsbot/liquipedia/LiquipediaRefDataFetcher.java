package com.wfbfm.rlcsbot.liquipedia;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.LOGO_DIRECTORY;

public class LiquipediaRefDataFetcher
{
    private static final String LIQUIPEDIA_BASE_URL = "https://liquipedia.net";
    private static final String CLASS = "class";
    private static final String TEAMCARD_COLUMN = "teamcard-column";
    private static final String CENTER = "center";
    private static final String TEAMCARD_INNER = "teamcard-inner";
    private static final String LOGO_TABLE = "wikitable wikitable-bordered logo";
    private final Logger logger = Logger.getLogger(LiquipediaRefDataFetcher.class.getName());
    private Map<String, Map<String, String>> teamToPlayerAndCoachMap = new HashMap<>();
    private Map<String, Set<String>> teamToPlayerNameMap = new HashMap<>();
    private Map<String, String> playerToTeamNameMap = new HashMap<>();
    private Map<String, String> uppercasePlayerNameMap = new HashMap<>();
    private Map<String, String> uppercaseTeamNameMap = new HashMap<>();
    private Map<String, String> uppercaseDisplayToLiquipediaName = new HashMap<>();
    private String liquipediaUrl;

    public LiquipediaRefDataFetcher()
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
        this.uppercaseDisplayToLiquipediaName.put("M8 ALPINE", "Gentle Mates Alpine");
        this.uppercaseDisplayToLiquipediaName.put("COMPLEXITY", "Complexity Gaming");
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
            parseTeamCardData(teamCards.get(0));
            logger.log(Level.INFO, "Fetched " + uppercasePlayerNameMap.size() + " players from Liquipedia.");
            logger.log(Level.INFO, "Fetched " + uppercaseTeamNameMap.size() + " teams from Liquipedia.");
            return true;
        }
    }

    private void parseTeamCardData(final Element teamCard)
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

            final Element logoTable = teamCardInner.getElementsByAttributeValue(CLASS, LOGO_TABLE).get(0);

            final Elements imgElements = logoTable.select("img");
            for (final Element img : imgElements)
            {
                final String imageUrl = img.attr("src");
                try
                {
                    downloadImage(LIQUIPEDIA_BASE_URL + imageUrl, teamName);
                } catch (IOException e)
                {
                    logger.log(Level.INFO, "Unable to download team logo for " + teamName, e);
                }
            }

            // download logos
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

    private static void downloadImage(final String imageUrl, final String teamName) throws IOException
    {
        final URL url = new URL(imageUrl);
        final InputStream in = url.openStream();

        final String fileName;
        if (imageUrl.contains("lightmode"))
        {
            fileName = teamName + "_lightmode.png";
        }
        else if (imageUrl.contains("darkmode"))
        {
            fileName = teamName + "_darkmode.png";
        }
        else
        {
            fileName = teamName + "_default.png";
        }
        final FileOutputStream out = new FileOutputStream(LOGO_DIRECTORY + File.separator + fileName);

        // Copy the image from the input stream to the output stream
        final byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, bytesRead);
        }

        // Close streams
        out.close();
        in.close();
    }
}
