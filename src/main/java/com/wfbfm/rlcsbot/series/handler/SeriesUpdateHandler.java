package com.wfbfm.rlcsbot.series.handler;

import com.wfbfm.rlcsbot.liquipedia.LiquipediaTeamGetter;
import com.wfbfm.rlcsbot.series.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.*;

public class SeriesUpdateHandler
{
    private static final Set<Integer> allowableBestOf = new HashSet<>(Set.of(3, 5, 7));
    private final List<Series> completedSeries = new ArrayList<>();
    private final LiquipediaTeamGetter liquipediaTeamGetter;
    private final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    private final Logger logger = Logger.getLogger(SeriesUpdateHandler.class.getName());
    private Series currentSeries = null;

    public SeriesUpdateHandler(final LiquipediaTeamGetter liquipediaTeamGetter)
    {
        this.liquipediaTeamGetter = liquipediaTeamGetter;
    }

    public SeriesSnapshotEvaluation evaluateSeries(final SeriesSnapshot snapshot)
    {
        // TODO - recovery mechanism in case we get the names wrong on the first assignment?
        // TODO - ditto gameNumber
        if (!enrichAllNamesFromTeams(snapshot))
        {
            if (!enrichAllNamesFromPlayers(snapshot))
            {
                return handleNonGameScreenshot();
            }
        }
        return handleGameScreenshot(snapshot);
    }

    private SeriesSnapshotEvaluation handleNonGameScreenshot()
    {
        if (currentSeries == null)
        {
            return SeriesSnapshotEvaluation.SERIES_NOT_STARTED_YET;
        }
        if (isGameCompletable())
        {
            return handleCompletedGame();
        }
        return SeriesSnapshotEvaluation.NOT_GAME_SCREENSHOT;
    }

    private SeriesSnapshotEvaluation handleCompletedGame()
    {
        currentSeries.handleCompletedGame();
        if (currentSeries.isComplete())
        {
            this.completedSeries.add(currentSeries);
            currentSeries = null;
            return SeriesSnapshotEvaluation.SERIES_COMPLETE;
        }
        else
        {
            return SeriesSnapshotEvaluation.SERIES_SCORE_CHANGED;
        }
    }

    private SeriesSnapshotEvaluation handleGameScreenshot(final SeriesSnapshot snapshot)
    {
        if (currentSeries == null)
        {
            if (isValidNewSeries(snapshot))
            {
                currentSeries = new Series(snapshot);
                return SeriesSnapshotEvaluation.NEW_SERIES;
            }
            else
            {
                // TODO - what will cause this / do we need to handle?
                return SeriesSnapshotEvaluation.INVALID_NEW_SERIES;
            }
        }
        enrichBestOf(snapshot);
        if (isHighlight(snapshot))
        {
            if (isGameCompletable())
            {
                return handleCompletedGame();
            }
            return  SeriesSnapshotEvaluation.HIGHLIGHT;
        }
        return handleGameUpdate(snapshot);
    }

    private boolean isValidNewSeries(final SeriesSnapshot snapshot)
    {
        // TODO
        return true;
    }

    private SeriesSnapshotEvaluation handleGameUpdate(final SeriesSnapshot snapshot)
    {
        // TODO:
        // update clock; update scores; sense-check the series score
        senseCheckSeriesScore(snapshot);
        currentSeries.getCurrentGame().setClock(snapshot.getCurrentGame().getClock());

        final Score snapshotGameScore = snapshot.getCurrentGame().getScore();
        final Score existingGameScore = currentSeries.getCurrentGame().getScore();

        final boolean hasBlueScoreChanged = snapshotGameScore.getBlueScore() > existingGameScore.getBlueScore();
        final boolean hasOrangeScoreChanged = snapshotGameScore.getOrangeScore() > existingGameScore.getOrangeScore();
        if (hasBlueScoreChanged)
        {
            existingGameScore.setBlueScore(snapshotGameScore.getBlueScore());
        }
        if (hasOrangeScoreChanged)
        {
            existingGameScore.setOrangeScore(snapshotGameScore.getOrangeScore());
        }
        if (hasBlueScoreChanged || hasOrangeScoreChanged)
        {
            return SeriesSnapshotEvaluation.GAME_SCORE_CHANGED;
        }

        return SeriesSnapshotEvaluation.SCORE_UNCHANGED;
    }

    private void senseCheckSeriesScore(final SeriesSnapshot snapshot)
    {
        // FIXME: When the screenshot feed is interrupted - i.e. we miss a game, we need a recovery mechanism.
        // This is identifying the problematic series, but it's not handling them in any special way
        final Score snapshotSeriesScore = snapshot.getSeriesScore();
        final Score currentSeriesScore = currentSeries.getSeriesScore();

        if (snapshotSeriesScore.getBlueScore() != currentSeriesScore.getBlueScore())
        {
            logger.log(Level.WARNING, "Conflict between cached vs. snapshot blueSeriesScore: " + currentSeriesScore.getBlueScore() +
                    " vs. " + snapshotSeriesScore.getBlueScore());
        }

        if (snapshotSeriesScore.getOrangeScore() != currentSeriesScore.getOrangeScore())
        {
            logger.log(Level.WARNING, "Conflict between cached vs. snapshot orangeSeriesScore: " + currentSeriesScore.getOrangeScore() +
                    " vs. " + snapshotSeriesScore.getOrangeScore());
        }

        if (snapshot.getCurrentGameNumber() != currentSeries.getCurrentGameNumber())
        {
            logger.log(Level.WARNING, "Conflict between cached vs. snapshot gameNumber: " + currentSeries.getCurrentGameNumber() +
                    " vs. " + snapshot.getCurrentGameNumber());
        }
    }

    private boolean isGameCompletable()
    {
        if (currentSeries == null || currentSeries.getCurrentGame() == null)
        {
            return false;
        }
        final Score gameScore = currentSeries.getCurrentGame().getScore();
        final boolean isTeamInLead = gameScore.getBlueScore() != gameScore.getOrangeScore();
        final Clock clock = currentSeries.getCurrentGame().getClock();
        final boolean isLittleTimeRemaining = clock.isOvertime() || (GAME_TIME_SECONDS - clock.getElapsedSeconds()) < (2 * SCREENSHOT_INTERVAL_MS / 1_000);
        return isTeamInLead && isLittleTimeRemaining;
    }

    private void enrichBestOf(final SeriesSnapshot snapshot)
    {
        // image recognition isn't the best, sometimes we need to correct what we parse
        if (!allowableBestOf.contains(currentSeries.getBestOf()) && allowableBestOf.contains(snapshot.getBestOf()))
        {
            currentSeries.setBestOf(snapshot.getBestOf());
        }
    }

    private boolean isHighlight(final SeriesSnapshot snapshot)
    {
        if (snapshot.getCurrentGameNumber() < currentSeries.getCurrentGameNumber())
        {
            return true;
        }
        final int snapshotGameBlueScore = snapshot.getCurrentGame().getScore().getTeamScore(TeamColour.BLUE);
        final int snapshotGameOrangeScore = snapshot.getCurrentGame().getScore().getTeamScore(TeamColour.ORANGE);
        final int existingGameBlueScore = currentSeries.getCurrentGame().getScore().getTeamScore(TeamColour.BLUE);
        final int existingGameOrangeScore = currentSeries.getCurrentGame().getScore().getTeamScore(TeamColour.ORANGE);
        if (snapshotGameBlueScore < existingGameBlueScore || snapshotGameOrangeScore < existingGameOrangeScore)
        {
            return true;
        }
        if (snapshot.getCurrentGame().getClock().getElapsedSeconds() < currentSeries.getCurrentGame().getClock().getElapsedSeconds())
        {
            return true;
        }
        return false;
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
        final String blueTeamName = resolveTeamFromImperfectPlayerNames(blueTeam);
        final String orangeTeamName = resolveTeamFromImperfectPlayerNames(orangeTeam);

        if (blueTeamName == null || orangeTeamName == null)
        {
            return false;
        }

        blueTeam.setTeamName(blueTeamName);
        orangeTeam.setTeamName(orangeTeamName);
        enrichPlayerNamesFromTeams(snapshot);
        return true;
    }

    private String resolveTeamFromImperfectPlayerNames(final Team team)
    {
        final String resolvedPlayerName1 = lookupImperfectName(liquipediaTeamGetter.getUppercasePlayerNameMap(),
                team.getPlayer1().getName().toUpperCase());
        final String resolvedPlayerName2 = lookupImperfectName(liquipediaTeamGetter.getUppercasePlayerNameMap(),
                team.getPlayer2().getName().toUpperCase());
        final String resolvedPlayerName3 = lookupImperfectName(liquipediaTeamGetter.getUppercasePlayerNameMap(),
                team.getPlayer3().getName().toUpperCase());

        //  Only return a team name if the resolved players are all part of the same team.  At least 2 players must be resolved.
        int countResolvedPlayers = 0;
        final Set<String> resolvedPlayerNames = new HashSet<>();
        final Set<String> impliedTeamNames = new HashSet<>();
        if (resolvedPlayerName1 != null)
        {
            countResolvedPlayers++;
            resolvedPlayerNames.add(resolvedPlayerName1);
            impliedTeamNames.add(liquipediaTeamGetter.getPlayerToTeamNameMap().get(resolvedPlayerName1));
        }
        if (resolvedPlayerName2 != null)
        {
            countResolvedPlayers++;
            resolvedPlayerNames.add(resolvedPlayerName2);
            impliedTeamNames.add(liquipediaTeamGetter.getPlayerToTeamNameMap().get(resolvedPlayerName2));
        }
        if (resolvedPlayerName3 != null)
        {
            countResolvedPlayers++;
            resolvedPlayerNames.add(resolvedPlayerName3);
            impliedTeamNames.add(liquipediaTeamGetter.getPlayerToTeamNameMap().get(resolvedPlayerName3));
        }
        if (countResolvedPlayers > 1 && countResolvedPlayers == resolvedPlayerNames.size() && impliedTeamNames.size() == 1)
        {
            return impliedTeamNames.iterator().next();
        }
        return null;
    }

    private String lookupImperfectName(final Map<String, String> nameMap, final String inputName)
    {
        if (StringUtils.isEmpty(inputName))
        {
            return null;
        }
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

    public String getCurrentSeriesAsString()
    {
        if (this.currentSeries != null)
        {
            return this.currentSeries.toString();
        }
        return null;
    }

    public List<Series> getCompletedSeries()
    {
        return completedSeries;
    }

    public Series getCurrentSeries()
    {
        return currentSeries;
    }
}
