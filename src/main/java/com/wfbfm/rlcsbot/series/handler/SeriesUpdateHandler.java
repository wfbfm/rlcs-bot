package com.wfbfm.rlcsbot.series.handler;

import com.wfbfm.rlcsbot.liquipedia.LiquipediaTeamGetter;
import com.wfbfm.rlcsbot.series.Series;
import com.wfbfm.rlcsbot.series.SeriesSnapshot;
import com.wfbfm.rlcsbot.series.Team;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.LEVENSHTEIN_MINIMUM_DISTANCE;
import static com.wfbfm.rlcsbot.app.RuntimeConstants.LIQUIPEDIA_PAGE;

public class SeriesUpdateHandler
{
    private final LiquipediaTeamGetter liquipediaTeamGetter = new LiquipediaTeamGetter();
    private final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    private final Logger logger = Logger.getLogger(SeriesUpdateHandler.class.getName());

    public SeriesUpdateHandler()
    {
        liquipediaTeamGetter.setLiquipediaUrl(LIQUIPEDIA_PAGE);
        liquipediaTeamGetter.updateLiquipediaRefData();
    }

    public SeriesSnapshotEvaluation evaluateSeries(final SeriesSnapshot snapshot, final Series existingSeries)
    {
        if (!enrichAllNamesFromTeams(snapshot))
        {
            if (!enrichAllNamesFromPlayers(snapshot))
            {
                return handleNonGameScreenshot(existingSeries);
            }
        }
        return handleGameScreenshot(snapshot, existingSeries);
    }

    private SeriesSnapshotEvaluation handleNonGameScreenshot(final Series existingSeries)
    {
        // TODO
        return SeriesSnapshotEvaluation.NOT_GAME_SCREENSHOT;
    }

    private SeriesSnapshotEvaluation handleGameScreenshot(final SeriesSnapshot snapshot, final Series existingSeries)
    {
        // TODO
        return SeriesSnapshotEvaluation.GAME_SCREENSHOT;
    }

    private boolean enrichAllNamesFromTeams(final SeriesSnapshot snapshot)
    {
        final Team blueTeam = snapshot.getBlueTeam();
        final Team orangeTeam = snapshot.getOrangeTeam();
        if (StringUtils.isEmpty(blueTeam.getTeamName()) || StringUtils.isEmpty(orangeTeam.getTeamName()))
        {
            return false;
        }

        final String blueTeamName = lookupImperfectName(liquipediaTeamGetter.getUppercaseTeamNameMap(),
                blueTeam.getTeamName().toUpperCase());
        final String orangeTeamName = lookupImperfectName(liquipediaTeamGetter.getUppercaseTeamNameMap(),
                orangeTeam.getTeamName().toUpperCase());

        if (blueTeamName == null)
        {
            logger.log(Level.INFO, "Unable to resolve blue team: " + blueTeam.getTeamName());
            return false;
        }
        if (orangeTeamName == null)
        {
            logger.log(Level.INFO, "Unable to resolve orange team: " + orangeTeam.getTeamName());
            return false;
        }
        blueTeam.setTeamName(blueTeamName);
        orangeTeam.setTeamName(orangeTeamName);
        enrichPlayerNamesFromTeams(snapshot);
        return true;
    }

    private void enrichPlayerNamesFromTeams(final SeriesSnapshot snapshot)
    {
        final Map<String, String> bluePlayerByPositionMap = liquipediaTeamGetter.getTeamToPlayerAndCoachMap()
                .get(snapshot.getBlueTeam().getTeamName());
        snapshot.getBlueTeam().getPlayer1().setName(bluePlayerByPositionMap.get("1"));
        snapshot.getBlueTeam().getPlayer2().setName(bluePlayerByPositionMap.get("2"));
        snapshot.getBlueTeam().getPlayer3().setName(bluePlayerByPositionMap.get("3"));

        final Map<String, String> orangePlayerByPositionMap = liquipediaTeamGetter.getTeamToPlayerAndCoachMap()
                .get(snapshot.getOrangeTeam().getTeamName());
        snapshot.getOrangeTeam().getPlayer1().setName(orangePlayerByPositionMap.get("1"));
        snapshot.getOrangeTeam().getPlayer2().setName(orangePlayerByPositionMap.get("2"));
        snapshot.getOrangeTeam().getPlayer3().setName(orangePlayerByPositionMap.get("3"));
    }

    private boolean enrichAllNamesFromPlayers(final SeriesSnapshot snapshot)
    {
        final Team blueTeam = snapshot.getBlueTeam();
        final Team orangeTeam = snapshot.getOrangeTeam();
        final String bluePlayerName = resolveImperfectPlayerName(blueTeam);
        final String orangePlayerName = resolveImperfectPlayerName(orangeTeam);

        if (bluePlayerName == null)
        {
            logger.log(Level.INFO, "Unable to resolve blue player from: " + blueTeam.getPlayerNames());
            return false;
        }

        if (orangePlayerName == null)
        {
            logger.log(Level.INFO, "Unable to resolve orange player from: " + blueTeam.getPlayerNames());
            return false;
        }

        final String blueTeamName = liquipediaTeamGetter.getPlayerToTeamNameMap().get(bluePlayerName);
        final String orangeTeamName = liquipediaTeamGetter.getPlayerToTeamNameMap().get(orangePlayerName);

        blueTeam.setTeamName(bluePlayerName);
        orangeTeam.setTeamName(orangeTeamName);
        enrichPlayerNamesFromTeams(snapshot);
        return true;
    }

    private String resolveImperfectPlayerName(final Team team)
    {
        String resolvedPlayerName = lookupImperfectName(liquipediaTeamGetter.getUppercasePlayerNameMap(),
                team.getPlayer1().getName().toUpperCase());
        if (resolvedPlayerName == null)
        {
            resolvedPlayerName = lookupImperfectName(liquipediaTeamGetter.getUppercasePlayerNameMap(),
                    team.getPlayer2().getName().toUpperCase());
        }
        if (resolvedPlayerName == null)
        {
            resolvedPlayerName = lookupImperfectName(liquipediaTeamGetter.getUppercasePlayerNameMap(),
                    team.getPlayer3().getName().toUpperCase());
        }
        return resolvedPlayerName;
    }

    private String lookupImperfectName(final Map<String, String> nameMap, final String inputName)
    {
        String bestMatch = null;
        int minDistance = Integer.MAX_VALUE;

        for (Map.Entry<String, String> entry : nameMap.entrySet())
        {
            String name = entry.getKey();
            int distance = levenshteinDistance.apply(inputName.toLowerCase(), name.toLowerCase());

            if (distance < minDistance && distance <= LEVENSHTEIN_MINIMUM_DISTANCE)
            {
                minDistance = distance;
                bestMatch = entry.getValue();
            }
        }
        return bestMatch;
    }
}
