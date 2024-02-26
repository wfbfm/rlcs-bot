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
    private final LiquipediaTeamGetter liquipediaTeamGetter = new LiquipediaTeamGetter();
    private final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    private final Logger logger = Logger.getLogger(SeriesUpdateHandler.class.getName());
    private Series currentSeries = null;

    public SeriesUpdateHandler()
    {
        liquipediaTeamGetter.setLiquipediaUrl(LIQUIPEDIA_PAGE);
        liquipediaTeamGetter.updateLiquipediaRefData();
    }

    public SeriesSnapshotEvaluation evaluateSeries(final SeriesSnapshot snapshot)
    {
        // FIXME: This appears to be breaking on test data
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
        final boolean isLittleTimeRemaining = clock.isOvertime() || (GAME_TIME_SECONDS - clock.getElapsedSeconds()) < (2 * SCREENSHOT_INTERVAL_MS);
        return isTeamInLead && isLittleTimeRemaining;
    }

    private void enrichBestOf(final SeriesSnapshot snapshot)
    {
        // FIXME: This is far too volatile and needs fixing.
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
        final int snapshotGamesCompleted = snapshot.getSeriesScore().getBlueScore() + snapshot.getSeriesScore().getOrangeScore();
        final int existingGamesCompleted = currentSeries.getSeriesScore().getBlueScore() + currentSeries.getSeriesScore().getOrangeScore();
        if (snapshotGamesCompleted < existingGamesCompleted)
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

    public String getCurrentSeries()
    {
        return this.currentSeries.toString();
    }
}
