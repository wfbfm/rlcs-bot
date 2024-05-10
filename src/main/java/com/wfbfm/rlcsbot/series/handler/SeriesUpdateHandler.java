package com.wfbfm.rlcsbot.series.handler;

import com.google.common.annotations.VisibleForTesting;
import com.wfbfm.rlcsbot.app.ApplicationContext;
import com.wfbfm.rlcsbot.liquipedia.LiquipediaRefDataFetcher;
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
    private final ApplicationContext applicationContext;
    private final List<Series> completedSeries = new ArrayList<>();
    private final LiquipediaRefDataFetcher liquipediaRefDataFetcher;
    private final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    private final Logger logger = Logger.getLogger(SeriesUpdateHandler.class.getName());
    private Series currentSeries = null;
    private SeriesSnapshot snapshotWithIllogicalScore = null;

    public SeriesUpdateHandler(final ApplicationContext applicationContext, final LiquipediaRefDataFetcher liquipediaRefDataFetcher)
    {
        this.applicationContext = applicationContext;
        this.liquipediaRefDataFetcher = liquipediaRefDataFetcher;
    }

    public SeriesSnapshotEvaluation evaluateSeries(final SeriesSnapshot snapshot)
    {
        if (applicationContext.getGameWinnerOverride() != TeamColour.NONE)
        {
            return handleSeriesWinnerOverride();
        }

        if (applicationContext.abandonSeries())
        {
            // TODO; this should probably delete in elastic, as well
            this.currentSeries = null;
            applicationContext.setAbandonSeries(false);
            return SeriesSnapshotEvaluation.SERIES_NOT_STARTED_YET;
        }

        if (!enrichAllNamesFromTeams(snapshot))
        {
            if (!enrichAllNamesFromPlayers(snapshot))
            {
                snapshotWithIllogicalScore = null;
                return handleNonGameScreenshot();
            }
        }
        return handleGameScreenshot(snapshot);
    }

    private SeriesSnapshotEvaluation handleSeriesWinnerOverride()
    {
        logger.log(Level.WARNING, "Applying game winner override: " + currentSeries.toString());
        final Score currentGameScore = currentSeries.getCurrentGame().getScore();
        final int newGameScore = currentGameScore.getTeamScore(applicationContext.getGameWinnerOverride()) + 1;
        currentGameScore.setTeamScore(newGameScore, applicationContext.getGameWinnerOverride());

        applicationContext.setGameWinnerOverride(TeamColour.NONE);
        return handleCompletedGame();
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
        final TeamColour winningTeam = currentSeries.handleCompletedGame();
        if (currentSeries.isComplete())
        {
            this.completedSeries.add(currentSeries);
            currentSeries = null;
            return SeriesSnapshotEvaluation.SERIES_COMPLETE;
        }
        else
        {
            if (winningTeam == TeamColour.BLUE)
            {
                return SeriesSnapshotEvaluation.BLUE_GAME;
            }
            else
            {
                return SeriesSnapshotEvaluation.ORANGE_GAME;
            }
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
                return SeriesSnapshotEvaluation.INVALID_NEW_SERIES;
            }
        }
        else
        {
            enrichBestOf(snapshot);
            if (!snapshot.getBlueTeam().getTeamName().equals(currentSeries.getBlueTeam().getTeamName()) ||
                    !snapshot.getOrangeTeam().getTeamName().equals(currentSeries.getOrangeTeam().getTeamName()))
            {
                if (isValidNewSeries(snapshot) && isSeriesMatchPoint())
                {
                    logger.log(Level.SEVERE, "Abandoning current series: " + currentSeries.toString());
                    currentSeries = new Series(snapshot);
                    logger.log(Level.INFO, "Replacing with new series: " + currentSeries.toString());
                    return SeriesSnapshotEvaluation.NEW_SERIES;
                }
            }
        }
        if (isHighlight(snapshot))
        {
            snapshotWithIllogicalScore = null;
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
        if (applicationContext.isMidSeriesAllowed())
        {
            return true;
        }
        else
        {
            final boolean littleTimeElapsed = snapshot.getCurrentGame().getClock().getElapsedSeconds() < 100;
            final boolean zeroSeriesScore = snapshot.getSeriesScore().getBlueScore() == 0 && snapshot.getSeriesScore().getOrangeScore() == 0;
            return littleTimeElapsed && zeroSeriesScore && !isSeriesAlreadyComplete(snapshot);
        }
    }

    private boolean isSeriesAlreadyComplete(final SeriesSnapshot snapshot)
    {
        final String blueTeam = snapshot.getBlueTeam().getTeamName();
        final String orangeTeam = snapshot.getOrangeTeam().getTeamName();
        final String liquipediaPage = snapshot.getSeriesMetaData().getLiquipediaPage();
        final int bestOf = snapshot.getBestOf();

        final boolean[] alreadyExists = {false};
        completedSeries.forEach(series ->
        {
            if (!series.getBlueTeam().getTeamName().equals(blueTeam))
            {
                return;
            }
            if (!series.getOrangeTeam().getTeamName().equals(orangeTeam))
            {
                return;
            }
            if (!series.getSeriesMetaData().getLiquipediaPage().equals(liquipediaPage))
            {
                return;
            }
            if (series.getBestOf() == bestOf)
            {
                alreadyExists[0] = true;
            }
        });
        return alreadyExists[0];
    }

    private SeriesSnapshotEvaluation handleGameUpdate(final SeriesSnapshot snapshot)
    {
        updateClockTime(snapshot);
        if (!senseCheckSeriesScore(snapshot, currentSeries.getSeriesScore(), currentSeries.getCurrentGame().getScore(),
                currentSeries.getCurrentGameNumber()))
        {
            if (snapshotWithIllogicalScore != null)
            {
                return evaluateCorrection(snapshot);
            }
            else
            {
                snapshotWithIllogicalScore = snapshot;
                return SeriesSnapshotEvaluation.SCORE_UNCHANGED;
            }
        }

        snapshotWithIllogicalScore = null;

        final Score snapshotGameScore = snapshot.getCurrentGame().getScore();
        final Score existingGameScore = currentSeries.getCurrentGame().getScore();

        // TODO - recovery logic here, in case we miss a goal
        // only allows single goal upticks - i.e. assumes any goal always captured within the sampling timeframe
        if (snapshotGameScore.getBlueScore() - existingGameScore.getBlueScore() == 1)
        {
            existingGameScore.setBlueScore(snapshotGameScore.getBlueScore());
            return SeriesSnapshotEvaluation.BLUE_GOAL;
        }
        if (snapshotGameScore.getOrangeScore() - existingGameScore.getOrangeScore() == 1)
        {
            existingGameScore.setOrangeScore(snapshotGameScore.getOrangeScore());
            return SeriesSnapshotEvaluation.ORANGE_GOAL;
        }

        return SeriesSnapshotEvaluation.SCORE_UNCHANGED;
    }

    private SeriesSnapshotEvaluation evaluateCorrection(final SeriesSnapshot latestSnapshot)
    {
        if (senseCheckSeriesScore(latestSnapshot,
                snapshotWithIllogicalScore.getSeriesScore(), snapshotWithIllogicalScore.getCurrentGame().getScore(),
                snapshotWithIllogicalScore.getCurrentGameNumber()))
        {
            final Score currentGameScore = currentSeries.getCurrentGame().getScore();
            currentGameScore.setBlueScore(latestSnapshot.getCurrentGame().getScore().getBlueScore());
            currentGameScore.setOrangeScore(latestSnapshot.getCurrentGame().getScore().getOrangeScore());
            final Score currentSeriesScore = currentSeries.getSeriesScore();
            currentSeriesScore.setBlueScore(latestSnapshot.getSeriesScore().getBlueScore());
            currentSeriesScore.setOrangeScore(latestSnapshot.getSeriesScore().getOrangeScore());
            snapshotWithIllogicalScore = null;
            return SeriesSnapshotEvaluation.CORRECTION;
        }
        snapshotWithIllogicalScore = null;
        return SeriesSnapshotEvaluation.SCORE_UNCHANGED;
    }

    private void updateClockTime(final SeriesSnapshot snapshot)
    {
        // The image parsing cannot be trusted to give an accurate time.  Sometimes, the model gives a time in the past
        final Clock currentClock = currentSeries.getCurrentGame().getClock();
        if (currentClock.getElapsedSeconds() > snapshot.getCurrentGame().getClock().getElapsedSeconds())
        {
            final int approxElapsedSeconds = currentClock.getElapsedSeconds() + (applicationContext.getSamplingRateMs() / 1_000);
            final Clock newClock = new Clock(approxElapsedSeconds, currentClock.isOvertime());
            currentSeries.getCurrentGame().setClock(newClock);
            return;
        }
        currentSeries.getCurrentGame().setClock(snapshot.getCurrentGame().getClock());
    }

    private boolean senseCheckSeriesScore(final SeriesSnapshot snapshot,
                                          final Score existingSeriesScore,
                                          final Score existingGameScore,
                                          final int existingGameNumber)
    {
        // FIXME: When the screenshot feed is interrupted - i.e. we miss a game, we need a recovery mechanism.
        // This is identifying the problematic series, but it's not handling them in any special way
        final Score snapshotSeriesScore = snapshot.getSeriesScore();

        if (snapshotSeriesScore.getBlueScore() != existingSeriesScore.getBlueScore())
        {
            logger.log(Level.WARNING, "Conflict between cached vs. snapshot blueSeriesScore: " + existingSeriesScore.getBlueScore() +
                    " vs. " + snapshotSeriesScore.getBlueScore());
            return false;
        }

        if (snapshotSeriesScore.getOrangeScore() != existingSeriesScore.getOrangeScore())
        {
            logger.log(Level.WARNING, "Conflict between cached vs. snapshot orangeSeriesScore: " + existingSeriesScore.getOrangeScore() +
                    " vs. " + snapshotSeriesScore.getOrangeScore());
            return false;
        }

        if (snapshot.getCurrentGameNumber() != existingGameNumber)
        {
            logger.log(Level.WARNING, "Conflict between cached vs. snapshot gameNumber: " + currentSeries.getCurrentGameNumber() +
                    " vs. " + snapshot.getCurrentGameNumber());
            return false;
        }

        final Score snapshotGameScore = snapshot.getCurrentGame().getScore();
        if (snapshotGameScore.getBlueScore() - existingGameScore.getBlueScore() > 1)
        {
            logger.log(Level.WARNING, "Conflict between cached vs. snapshot blueGameScore: " + existingGameScore.getBlueScore() +
                    " vs. " + snapshotGameScore.getBlueScore());
            // TODO: is it safe to issue corrections to game score?
            return false;
        }

        if (snapshotGameScore.getOrangeScore() - existingGameScore.getOrangeScore() > 1)
        {
            logger.log(Level.WARNING, "Conflict between cached vs. snapshot orangeGameScore: " + existingGameScore.getOrangeScore() +
                    " vs. " + snapshotGameScore.getOrangeScore());
            // TODO: is it safe to issue corrections to game score?
            return false;
        }

        return true;
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
        final boolean isLittleTimeRemaining = clock.isOvertime() ||
                (GAME_TIME_SECONDS - clock.getElapsedSeconds()) < (2 * applicationContext.getSamplingRateMs() / 1_000);
        return isTeamInLead && isLittleTimeRemaining;
    }

    private boolean isSeriesMatchPoint()
    {
        final Score seriesScore = currentSeries.getSeriesScore();
        final int matchPoint = currentSeries.getSeriesWinningGameScore() - 1;
        final Clock clock = currentSeries.getCurrentGame().getClock();
        final boolean isLittleTimeRemaining = clock.isOvertime() ||
                (GAME_TIME_SECONDS - clock.getElapsedSeconds()) < (2 * applicationContext.getSamplingRateMs() / 1_000);
        return isLittleTimeRemaining && (seriesScore.getBlueScore() >= matchPoint || seriesScore.getOrangeScore() >= matchPoint);
    }

    private boolean isSeriesCompletable() // not viable.
    {
        // attempt to automatically close out a series if we miss the winning goal.
        if (currentSeries == null || currentSeries.getCurrentGame() == null)
        {
            return false;
        }
        final boolean isGameScoreLevel = currentSeries.getCurrentGame().getScore().getTeamInLead() == TeamColour.NONE;
        final Clock clock = currentSeries.getCurrentGame().getClock();
        final boolean isLittleTimeRemaining = clock.isOvertime() ||
                (GAME_TIME_SECONDS - clock.getElapsedSeconds()) < (2 * applicationContext.getSamplingRateMs() / 1_000);
        // in the case where scores are level, i.e. 7th game of Bo7 - you can't automatically complete the series
        // those cases will require a lookup against Liquipedia or manual input
        if (isGameScoreLevel && isLittleTimeRemaining && isSeriesMatchPoint())
        {
            return currentSeries.getSeriesScore().getTeamInLead() != TeamColour.NONE;
        }
        return false;
    }

    private void enrichBestOf(final SeriesSnapshot snapshot)
    {
        // image recognition isn't the best, sometimes we need to correct what we parse
        // override from config
        if (applicationContext.getBestOf() > 0)
        {
            currentSeries.setBestOf(applicationContext.getBestOf());
        }
        else if (!allowableBestOf.contains(currentSeries.getBestOf()) && allowableBestOf.contains(snapshot.getBestOf()))
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
        // removed check on clock time
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

        final String blueTeamName = lookupImperfectName(liquipediaRefDataFetcher.getUppercaseTeamNameMap(),
                applicationContext.getUppercaseDisplayToLiquipediaName(),
                blueTeam.getTeamName().toUpperCase());
        final String orangeTeamName = lookupImperfectName(liquipediaRefDataFetcher.getUppercaseTeamNameMap(),
                applicationContext.getUppercaseDisplayToLiquipediaName(),
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
        if (blueTeamName.equals(orangeTeamName))
        {
            logger.log(Level.INFO, "Unable to resolve team - same team parsed:" + orangeTeam.getTeamName());
            return false;
        }
        blueTeam.setTeamName(blueTeamName);
        orangeTeam.setTeamName(orangeTeamName);
        enrichPlayerNamesFromTeams(snapshot);
        return true;
    }

    private void enrichPlayerNamesFromTeams(final SeriesSnapshot snapshot)
    {
        final Map<String, String> bluePlayerByPositionMap = liquipediaRefDataFetcher.getTeamToPlayerAndCoachMap()
                .get(snapshot.getBlueTeam().getTeamName());
        snapshot.getBlueTeam().getPlayer1().setName(bluePlayerByPositionMap.get("1"));
        snapshot.getBlueTeam().getPlayer2().setName(bluePlayerByPositionMap.get("2"));
        snapshot.getBlueTeam().getPlayer3().setName(bluePlayerByPositionMap.get("3"));

        final Map<String, String> orangePlayerByPositionMap = liquipediaRefDataFetcher.getTeamToPlayerAndCoachMap()
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
        final String resolvedPlayerName1 = lookupImperfectName(liquipediaRefDataFetcher.getUppercasePlayerNameMap(),
                applicationContext.getUppercaseDisplayToLiquipediaName(),
                team.getPlayer1().getName().toUpperCase());
        final String resolvedPlayerName2 = lookupImperfectName(liquipediaRefDataFetcher.getUppercasePlayerNameMap(),
                applicationContext.getUppercaseDisplayToLiquipediaName(),
                team.getPlayer2().getName().toUpperCase());
        final String resolvedPlayerName3 = lookupImperfectName(liquipediaRefDataFetcher.getUppercasePlayerNameMap(),
                applicationContext.getUppercaseDisplayToLiquipediaName(),
                team.getPlayer3().getName().toUpperCase());

        //  Only return a team name if the resolved players are all part of the same team.  At least 2 players must be resolved.
        int countResolvedPlayers = 0;
        final Set<String> resolvedPlayerNames = new HashSet<>();
        final Set<String> impliedTeamNames = new HashSet<>();
        if (resolvedPlayerName1 != null)
        {
            countResolvedPlayers++;
            resolvedPlayerNames.add(resolvedPlayerName1);
            impliedTeamNames.add(liquipediaRefDataFetcher.getPlayerToTeamNameMap().get(resolvedPlayerName1));
        }
        if (resolvedPlayerName2 != null)
        {
            countResolvedPlayers++;
            resolvedPlayerNames.add(resolvedPlayerName2);
            impliedTeamNames.add(liquipediaRefDataFetcher.getPlayerToTeamNameMap().get(resolvedPlayerName2));
        }
        if (resolvedPlayerName3 != null)
        {
            countResolvedPlayers++;
            resolvedPlayerNames.add(resolvedPlayerName3);
            impliedTeamNames.add(liquipediaRefDataFetcher.getPlayerToTeamNameMap().get(resolvedPlayerName3));
        }
        if (countResolvedPlayers > 1 && countResolvedPlayers == resolvedPlayerNames.size() && impliedTeamNames.size() == 1)
        {
            return impliedTeamNames.iterator().next();
        }
        return null;
    }

    private String lookupImperfectName(final Map<String, String> liquipediaNameMap,
                                       final Map<String, String> displayNameOverrideMap,
                                       final String inputName)
    {
        if (StringUtils.isEmpty(inputName))
        {
            return null;
        }
        String bestMatch = null;
        int minDistance = Integer.MAX_VALUE;

        for (Map.Entry<String, String> entry : liquipediaNameMap.entrySet())
        {
            String name = entry.getKey();
            int distance = levenshteinDistance.apply(inputName.toLowerCase(), name.toLowerCase());

            if (distance < minDistance && distance <= LEVENSHTEIN_MINIMUM_DISTANCE)
            {
                minDistance = distance;
                bestMatch = entry.getValue();
            }
        }

        if (bestMatch == null)
        {
            // TODO - refactor to common method
            for (Map.Entry<String, String> entry : displayNameOverrideMap.entrySet())
            {
                String name = entry.getKey();
                int distance = levenshteinDistance.apply(inputName.toLowerCase(), name.toLowerCase());

                if (distance < minDistance && distance <= LEVENSHTEIN_MINIMUM_DISTANCE)
                {
                    minDistance = distance;
                    bestMatch = entry.getValue();
                }
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

    public Series getMostRecentCompletedSeries()
    {
        final int numberOfCompletedSeries = completedSeries.size();
        if (numberOfCompletedSeries == 0)
        {
            return null;
        }
        return completedSeries.get(numberOfCompletedSeries - 1);
    }

    public Series getCurrentSeries()
    {
        return currentSeries;
    }

    @VisibleForTesting
    public void setCurrentSeries(final Series series)
    {
        this.currentSeries = series;
    }

    public SeriesSnapshot getSnapshotWithIllogicalScore()
    {
        return snapshotWithIllogicalScore;
    }
}
