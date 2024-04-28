package com.wfbfm.rlcsbot.series.handler;

import com.wfbfm.rlcsbot.app.ApplicationContext;
import com.wfbfm.rlcsbot.liquipedia.LiquipediaRefDataFetcher;
import com.wfbfm.rlcsbot.screenshotparser.GameScreenshotProcessorUtils;
import com.wfbfm.rlcsbot.series.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class SeriesUpdateHandlerTest
{
    private static final String PLAYER_ALPHA54 = "Alpha54";
    private static final String PLAYER_RADOSIN = "Radosin";
    private static final String PLAYER_ZEN = "zen";
    private static final String PLAYER_M0NKEY_M00N = "M0nkey M00n";
    private static final String PLAYER_DRALII = "dralii";
    private static final String PLAYER_EXOTIIK = "ExoTiiK";
    private static final String TEAM_VITALITY = "Team Vitality";
    private static final String TEAM_BDS = "Team BDS";
    private Team teamVitality;
    private Team teamBds;

    @Mock
    private LiquipediaRefDataFetcher liquipediaRefDataFetcher;

    private SeriesUpdateHandler seriesUpdateHandler;

    @BeforeEach
    public void setUp()
    {
        Map<String, Map<String, String>> teamToPlayerAndCoachMap = new HashMap<>();
        Map<String, Set<String>> teamToPlayerNameMap = new HashMap<>();
        Map<String, String> playerToTeamNameMap = new HashMap<>();
        Map<String, String> uppercasePlayerNameMap = new HashMap<>();
        Map<String, String> uppercaseTeamNameMap = new HashMap<>();

        Map<String, String> vitalityPlayerMap = Map.of("1", PLAYER_ALPHA54, "2", PLAYER_RADOSIN, "3", PLAYER_ZEN);
        Map<String, String> bdsPlayerMap = Map.of("1", PLAYER_M0NKEY_M00N, "2", PLAYER_DRALII, "3", PLAYER_EXOTIIK);
        teamToPlayerAndCoachMap.put(TEAM_VITALITY, vitalityPlayerMap);
        teamToPlayerAndCoachMap.put(TEAM_BDS, bdsPlayerMap);

        teamToPlayerNameMap.put(TEAM_VITALITY, Set.of(PLAYER_ALPHA54, PLAYER_RADOSIN, PLAYER_ZEN));
        teamToPlayerNameMap.put(TEAM_BDS, Set.of(PLAYER_M0NKEY_M00N, PLAYER_DRALII, PLAYER_EXOTIIK));

        playerToTeamNameMap.put(PLAYER_ALPHA54, TEAM_VITALITY);
        playerToTeamNameMap.put(PLAYER_RADOSIN, TEAM_VITALITY);
        playerToTeamNameMap.put(PLAYER_ZEN, TEAM_VITALITY);
        playerToTeamNameMap.put(PLAYER_M0NKEY_M00N, TEAM_BDS);
        playerToTeamNameMap.put(PLAYER_DRALII, TEAM_BDS);
        playerToTeamNameMap.put(PLAYER_EXOTIIK, TEAM_BDS);

        uppercasePlayerNameMap.put(PLAYER_ALPHA54.toUpperCase(), PLAYER_ALPHA54);
        uppercasePlayerNameMap.put(PLAYER_RADOSIN.toUpperCase(), PLAYER_RADOSIN);
        uppercasePlayerNameMap.put(PLAYER_ZEN.toUpperCase(), PLAYER_ZEN);
        uppercasePlayerNameMap.put(PLAYER_M0NKEY_M00N.toUpperCase(), PLAYER_M0NKEY_M00N);
        uppercasePlayerNameMap.put(PLAYER_DRALII.toUpperCase(), PLAYER_DRALII);
        uppercasePlayerNameMap.put(PLAYER_EXOTIIK.toUpperCase(), PLAYER_EXOTIIK);

        uppercaseTeamNameMap.put(TEAM_VITALITY.toUpperCase(), TEAM_VITALITY);
        uppercaseTeamNameMap.put(TEAM_BDS.toUpperCase(), TEAM_BDS);

        lenient().when(liquipediaRefDataFetcher.getTeamToPlayerAndCoachMap()).thenReturn(teamToPlayerAndCoachMap);
        lenient().when(liquipediaRefDataFetcher.getTeamToPlayerNameMap()).thenReturn(teamToPlayerNameMap);
        lenient().when(liquipediaRefDataFetcher.getPlayerToTeamNameMap()).thenReturn(playerToTeamNameMap);
        lenient().when(liquipediaRefDataFetcher.getUppercasePlayerNameMap()).thenReturn(uppercasePlayerNameMap);
        lenient().when(liquipediaRefDataFetcher.getUppercaseTeamNameMap()).thenReturn(uppercaseTeamNameMap);

        final ApplicationContext applicationContext = new ApplicationContext("test", "test", false);
        seriesUpdateHandler = new SeriesUpdateHandler(applicationContext, liquipediaRefDataFetcher);
        teamVitality = new Team(TEAM_VITALITY, new Player(PLAYER_ALPHA54), new Player(PLAYER_RADOSIN), new Player(PLAYER_ZEN), TeamColour.BLUE);
        teamBds = new Team(TEAM_BDS, new Player(PLAYER_M0NKEY_M00N), new Player(PLAYER_DRALII), new Player(PLAYER_EXOTIIK), TeamColour.ORANGE);
    }

    @Test
    public void testEnterNewSeries()
    {
        final SeriesSnapshot snapshot = mockSeriesSnapshot(0, 0, 0, 0, 7, "5:00");
        assertEquals(SeriesSnapshotEvaluation.NEW_SERIES, seriesUpdateHandler.evaluateSeries(snapshot));
        final Series series = seriesUpdateHandler.getCurrentSeries();

        assertSeriesValues(snapshot, series);
    }

    @Test
    public void testSeriesWithUpdates()
    {
        final SeriesSnapshot startSnapshot = mockSeriesSnapshot(0, 0, 0, 0, 7, "5:00");
        assertEquals(SeriesSnapshotEvaluation.NEW_SERIES, seriesUpdateHandler.evaluateSeries(startSnapshot));
        final Series startSeries = seriesUpdateHandler.getCurrentSeries();
        assertSeriesValues(startSnapshot, startSeries);

        final SeriesSnapshot blueGoalSnapshot = mockSeriesSnapshot(1, 0, 0, 0, 7, "2:59");
        assertEquals(SeriesSnapshotEvaluation.BLUE_GOAL, seriesUpdateHandler.evaluateSeries(blueGoalSnapshot));
        final Series blueGoalSeries = seriesUpdateHandler.getCurrentSeries();
        assertSeriesValues(blueGoalSnapshot, blueGoalSeries);

        final SeriesSnapshot noChangeSnapshot = mockSeriesSnapshot(1, 0, 0, 0, 7, "1:09");
        assertEquals(SeriesSnapshotEvaluation.SCORE_UNCHANGED, seriesUpdateHandler.evaluateSeries(noChangeSnapshot));
        final Series noChangeSeries = seriesUpdateHandler.getCurrentSeries();
        assertSeriesValues(noChangeSnapshot, noChangeSeries);

        final SeriesSnapshot orangeGoalSnapshot = mockSeriesSnapshot(1, 1, 0, 0, 7, "0:00");
        assertEquals(SeriesSnapshotEvaluation.ORANGE_GOAL, seriesUpdateHandler.evaluateSeries(orangeGoalSnapshot));
        final Series orangeGoalSeries = seriesUpdateHandler.getCurrentSeries();
        assertSeriesValues(orangeGoalSnapshot, orangeGoalSeries);

        final SeriesSnapshot overTimeSnapshot = mockSeriesSnapshot(1, 2, 0, 0, 7, "+0:37");
        assertEquals(SeriesSnapshotEvaluation.ORANGE_GOAL, seriesUpdateHandler.evaluateSeries(overTimeSnapshot));
        final Series overTimeSeries = seriesUpdateHandler.getCurrentSeries();
        assertSeriesValues(overTimeSnapshot, overTimeSeries);

        final SeriesSnapshot nonGameSnapshot = mockNonGameSnapshot();
        assertEquals(SeriesSnapshotEvaluation.ORANGE_GAME, seriesUpdateHandler.evaluateSeries(nonGameSnapshot));
        final Series postGameSeries = seriesUpdateHandler.getCurrentSeries();
        final SeriesSnapshot expectedPostGameSnapshot = mockSeriesSnapshot(0, 0, 0, 1, 7, "5:00");
        assertSeriesValues(expectedPostGameSnapshot, postGameSeries);

        final SeriesSnapshot highlightSnapshot = mockSeriesSnapshot(1, 2, 0, 0, 7, "+0:37");
        assertEquals(SeriesSnapshotEvaluation.HIGHLIGHT, seriesUpdateHandler.evaluateSeries(highlightSnapshot));
        final Series postHighlightSeries = seriesUpdateHandler.getCurrentSeries();
        assertSeriesValues(expectedPostGameSnapshot, postHighlightSeries);
    }

    @Test
    public void testHighlightCanCompleteGame()
    {
        final SeriesSnapshot startSnapshot = mockSeriesSnapshot(0, 0, 0, 0, 7, "5:00");
        assertEquals(SeriesSnapshotEvaluation.NEW_SERIES, seriesUpdateHandler.evaluateSeries(startSnapshot));
        final Series startSeries = seriesUpdateHandler.getCurrentSeries();
        assertSeriesValues(startSnapshot, startSeries);

        final SeriesSnapshot blueGoalSnapshot = mockSeriesSnapshot(1, 0, 0, 0, 7, "1:00");
        assertEquals(SeriesSnapshotEvaluation.BLUE_GOAL, seriesUpdateHandler.evaluateSeries(blueGoalSnapshot));
        final Series blueGoalSeries = seriesUpdateHandler.getCurrentSeries();
        assertSeriesValues(blueGoalSnapshot, blueGoalSeries);

        final SeriesSnapshot lateGameSnapshot = mockSeriesSnapshot(1, 0, 0, 0, 7, "0:01");
        assertEquals(SeriesSnapshotEvaluation.SCORE_UNCHANGED, seriesUpdateHandler.evaluateSeries(lateGameSnapshot));
        final Series lateGameSeries = seriesUpdateHandler.getCurrentSeries();
        assertSeriesValues(lateGameSnapshot, lateGameSeries);

        final SeriesSnapshot firstHighlightSnapshot = mockSeriesSnapshot(0, 0, 0, 0, 7, "1:30");
        assertEquals(SeriesSnapshotEvaluation.BLUE_GAME, seriesUpdateHandler.evaluateSeries(firstHighlightSnapshot));
        final Series postFirstHighlightSeries = seriesUpdateHandler.getCurrentSeries();
        final SeriesSnapshot expectedPostGameSnapshot = mockSeriesSnapshot(0, 0, 1, 0, 7, "5:00");
        assertSeriesValues(expectedPostGameSnapshot, postFirstHighlightSeries);

        final SeriesSnapshot secondHighlightSnapshot = mockSeriesSnapshot(1, 0, 0, 0, 7, "0:10");
        assertEquals(SeriesSnapshotEvaluation.HIGHLIGHT, seriesUpdateHandler.evaluateSeries(secondHighlightSnapshot));
        final Series postSecondHighlightSeries = seriesUpdateHandler.getCurrentSeries();
        assertSeriesValues(expectedPostGameSnapshot, postSecondHighlightSeries);
    }

    @Test
    public void testRecoverFromBadSeriesScoreState()
    {
        final SeriesSnapshot startSnapshot = mockSeriesSnapshot(0, 0, 0, 0, 7, "5:00");
        assertEquals(SeriesSnapshotEvaluation.NEW_SERIES, seriesUpdateHandler.evaluateSeries(startSnapshot));
        Series currentSeries = seriesUpdateHandler.getCurrentSeries();
        assertSeriesValues(startSnapshot, currentSeries);

        final SeriesSnapshot blueGoalSnapshot = mockSeriesSnapshot(1, 0, 0, 0, 7, "1:00");
        assertEquals(SeriesSnapshotEvaluation.BLUE_GOAL, seriesUpdateHandler.evaluateSeries(blueGoalSnapshot));
        assertSeriesValues(blueGoalSnapshot, currentSeries);

        final SeriesSnapshot lateGameSnapshot = mockSeriesSnapshot(1, 0, 0, 0, 7, "0:01");
        assertEquals(SeriesSnapshotEvaluation.SCORE_UNCHANGED, seriesUpdateHandler.evaluateSeries(lateGameSnapshot));
        assertSeriesValues(lateGameSnapshot, currentSeries);

        final SeriesSnapshot firstHighlightSnapshot = mockSeriesSnapshot(0, 0, 0, 0, 7, "1:30");
        assertEquals(SeriesSnapshotEvaluation.BLUE_GAME, seriesUpdateHandler.evaluateSeries(firstHighlightSnapshot));
        final SeriesSnapshot expectedPostGameSnapshot = mockSeriesSnapshot(0, 0, 1, 0, 7, "5:00");
        assertSeriesValues(expectedPostGameSnapshot, currentSeries);

        // it transpires that Orange actually won - now we go onto the next game
        final SeriesSnapshot secondGameFirstSnapshot = mockSeriesSnapshot(0, 0, 0, 1, 7, "5:00");
        assertEquals(SeriesSnapshotEvaluation.SCORE_UNCHANGED, seriesUpdateHandler.evaluateSeries(secondGameFirstSnapshot));
        assertNotNull(seriesUpdateHandler.getSnapshotWithIllogicalScore());
        assertSeriesValues(expectedPostGameSnapshot, currentSeries);

        final SeriesSnapshot secondGameSecondSnapshot = mockSeriesSnapshot(0, 0, 0, 1, 7, "4:50");
        assertEquals(SeriesSnapshotEvaluation.CORRECTION, seriesUpdateHandler.evaluateSeries(secondGameSecondSnapshot));
        assertNull(seriesUpdateHandler.getSnapshotWithIllogicalScore());
        final SeriesSnapshot expectedCorrectedSnapshot = mockSeriesSnapshot(0, 0, 0, 1, 7, "4:50");
        assertSeriesValues(expectedCorrectedSnapshot, currentSeries);
    }

    @Test
    public void testRecoverFromBadGameScoreState()
    {
        final SeriesSnapshot startSnapshot = mockSeriesSnapshot(0, 0, 0, 0, 7, "5:00");
        assertEquals(SeriesSnapshotEvaluation.NEW_SERIES, seriesUpdateHandler.evaluateSeries(startSnapshot));
        Series currentSeries = seriesUpdateHandler.getCurrentSeries();
        assertSeriesValues(startSnapshot, currentSeries);

        final SeriesSnapshot severalBlueGoalsSnapshot = mockSeriesSnapshot(7, 0, 0, 0, 7, "4:40");
        assertEquals(SeriesSnapshotEvaluation.SCORE_UNCHANGED, seriesUpdateHandler.evaluateSeries(severalBlueGoalsSnapshot));
        assertNotNull(seriesUpdateHandler.getSnapshotWithIllogicalScore());
        assertSeriesValues(startSnapshot, currentSeries);

        final SeriesSnapshot backToNormalSnapshot = mockSeriesSnapshot(1, 0, 0, 0, 7, "4:30");
        assertEquals(SeriesSnapshotEvaluation.BLUE_GOAL, seriesUpdateHandler.evaluateSeries(backToNormalSnapshot));
        assertNull(seriesUpdateHandler.getSnapshotWithIllogicalScore());
        assertSeriesValues(backToNormalSnapshot, currentSeries);

        final SeriesSnapshot severalOrangeGoalsSnapshot = mockSeriesSnapshot(1, 2, 0, 0, 7, "4:20");
        assertEquals(SeriesSnapshotEvaluation.SCORE_UNCHANGED, seriesUpdateHandler.evaluateSeries(severalOrangeGoalsSnapshot));
        assertNotNull(seriesUpdateHandler.getSnapshotWithIllogicalScore());
        final SeriesSnapshot backToNormalSnapshotNewTime = mockSeriesSnapshot(1, 0, 0, 0, 7, "4:20");
        assertSeriesValues(backToNormalSnapshotNewTime, currentSeries);

        final SeriesSnapshot severalOrangeGoalsSnapshotAgain = mockSeriesSnapshot(1, 2, 0, 0, 7, "4:10");
        assertEquals(SeriesSnapshotEvaluation.CORRECTION, seriesUpdateHandler.evaluateSeries(severalOrangeGoalsSnapshotAgain));
        assertNull(seriesUpdateHandler.getSnapshotWithIllogicalScore());
        assertSeriesValues(severalOrangeGoalsSnapshotAgain, currentSeries);
    }

    private void assertSeriesValues(SeriesSnapshot snapshot, Series series)
    {
        assertEquals(snapshot.getSeriesScore().getBlueScore(), series.getSeriesScore().getBlueScore());
        assertEquals(snapshot.getSeriesScore().getOrangeScore(), series.getSeriesScore().getOrangeScore());
        assertEquals(snapshot.getCurrentGame().getScore().getBlueScore(), series.getCurrentGame().getScore().getBlueScore());
        assertEquals(snapshot.getCurrentGame().getScore().getOrangeScore(), series.getCurrentGame().getScore().getOrangeScore());
        assertEquals(snapshot.getCurrentGame().getClock().getDisplayedTime(), series.getCurrentGame().getClock().getDisplayedTime());
        assertEquals(snapshot.getCurrentGame().getClock().getElapsedSeconds(), series.getCurrentGame().getClock().getElapsedSeconds());
        assertEquals(snapshot.getCurrentGame().getClock().isOvertime(), series.getCurrentGame().getClock().isOvertime());
        assertEquals(snapshot.getCurrentGame().getWinner(), series.getCurrentGame().getWinner());
        assertEquals(snapshot.getCurrentGameNumber(), series.getCurrentGameNumber());
        assertEquals(snapshot.getBestOf(), series.getBestOf());
    }

    private SeriesSnapshot mockSeriesSnapshot(final int blueGameScore,
                                              final int orangeGameScore,
                                              final int blueSeriesScore,
                                              final int orangeSeriesScore,
                                              final int bestOf,
                                              final String gameTime)
    {
        final SeriesMetaData metaData = new SeriesMetaData(LocalDate.now(), "description", "liquipediaPage");
        final Clock clock = GameScreenshotProcessorUtils.parseClockFromTime(gameTime);
        final Game game = new Game(new Score(blueGameScore, orangeGameScore), clock, TeamColour.NONE);
        final int currentGameNumber = Math.min(blueSeriesScore + orangeSeriesScore + 1, bestOf);
        return new SeriesSnapshot(metaData, game, currentGameNumber, new Score(blueSeriesScore, orangeSeriesScore), teamVitality, teamBds, bestOf);
    }

    private SeriesSnapshot mockNonGameSnapshot()
    {
        final Team blueTeam = new Team("", new Player(""), new Player(""), new Player(""), TeamColour.BLUE);
        final Team orangeTeam = new Team("", new Player(""), new Player(""), new Player(""), TeamColour.ORANGE);

        final SeriesMetaData metaData = new SeriesMetaData(LocalDate.now(), "description", "liquipediaPage");
        final Clock clock = GameScreenshotProcessorUtils.parseClockFromTime("5:00");
        final Game game = new Game(new Score(0, 0), clock, TeamColour.NONE);
        final int currentGameNumber = 0;
        return new SeriesSnapshot(metaData, game, currentGameNumber, new Score(0, 0), blueTeam, orangeTeam, 0);
    }
}