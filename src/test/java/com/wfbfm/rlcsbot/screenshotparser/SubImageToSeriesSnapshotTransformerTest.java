package com.wfbfm.rlcsbot.screenshotparser;

import com.wfbfm.rlcsbot.app.ApplicationContext;
import com.wfbfm.rlcsbot.series.Game;
import com.wfbfm.rlcsbot.series.SeriesSnapshot;
import com.wfbfm.rlcsbot.series.Team;
import com.wfbfm.rlcsbot.series.TeamColour;
import org.junit.jupiter.api.Test;

import static com.wfbfm.rlcsbot.TestConstants.*;
import static com.wfbfm.rlcsbot.app.RuntimeConstants.LIQUIPEDIA_PAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SubImageToSeriesSnapshotTransformerTest
{
    private final ApplicationContext applicationContext = new ApplicationContext("test", LIQUIPEDIA_PAGE, true);
    private final ScreenshotToSubImageTransformer screenshotToSubImageTransformer = new ScreenshotToSubImageTransformer();
    private final SubImageToSeriesSnapshotTransformer subImageToSeriesSnapshotTransformer = new SubImageToSeriesSnapshotTransformer(applicationContext);

    @Test
    public void testParseScorelessGameScreenshot()
    {
        final GameScreenshotSubImageWrapper subImageWrapper = screenshotToSubImageTransformer.transformScreenshotToSubImages(GAME_1_SCORE_0_0);
        final SeriesSnapshot snapshot = subImageToSeriesSnapshotTransformer.transform(subImageWrapper);

        final Game game = snapshot.getCurrentGame();
        assertEquals(TeamColour.NONE, game.getWinner());
        assertEquals(0, game.getScore().getBlueScore());
        assertEquals(0, game.getScore().getOrangeScore());
        assertEquals("4:33", game.getClock().getDisplayedTime());
        assertEquals(27, game.getClock().getElapsedSeconds());
        assertEquals(false, game.getClock().isOvertime());

        assertEquals(7, snapshot.getBestOf());
        assertEquals(0, snapshot.getSeriesScore().getOrangeScore());
        assertEquals(0, snapshot.getSeriesScore().getBlueScore());
        assertEquals(1, snapshot.getCurrentGameNumber());

        // Text recognition - particularly for Player/team names - is not perfect.
        // Player/team names are later normalised using Liquipedia reference data
        final Team blueTeam = snapshot.getBlueTeam();
        assertEquals("OXYGEN ESPORTS", blueTeam.getTeamName());
        assertEquals(TeamColour.BLUE, blueTeam.getTeamColour());
        assertEquals("ARCHIE", blueTeam.getPlayer1().getName());
        assertEquals("EEKSO", blueTeam.getPlayer2().getName());
        assertEquals("askKi", blueTeam.getPlayer3().getName());

        final Team orangeTeam = snapshot.getOrangeTeam();
        assertEquals("REDEMPTION", orangeTeam.getTeamName());
        assertEquals(TeamColour.ORANGE, orangeTeam.getTeamColour());
        // assertEquals("AZTRAL", orangeTeam.getPlayer1().getName());
        assertEquals("IVN", orangeTeam.getPlayer2().getName());
        assertEquals("KASH", orangeTeam.getPlayer3().getName());

        assertEquals("EURDOPEAN OPEN QUALIFIER 2 | DUARTERFINAL #3", snapshot.getSeriesMetaData().getSeriesDescription());
    }

    @Test
    public void testParseBo5SeriesInProgress()
    {
        final GameScreenshotSubImageWrapper subImageWrapper = screenshotToSubImageTransformer.transformScreenshotToSubImages(DIFFERENT_GAME);
        final SeriesSnapshot snapshot = subImageToSeriesSnapshotTransformer.transform(subImageWrapper);

        final Game game = snapshot.getCurrentGame();
        assertEquals(TeamColour.NONE, game.getWinner());
        assertEquals(2, game.getScore().getBlueScore());
        assertEquals(0, game.getScore().getOrangeScore());
        assertEquals("1:09", game.getClock().getDisplayedTime());
        assertEquals(231, game.getClock().getElapsedSeconds());
        assertEquals(false, game.getClock().isOvertime());

        assertEquals(5, snapshot.getBestOf());
        assertEquals(0, snapshot.getSeriesScore().getOrangeScore());
        assertEquals(1, snapshot.getSeriesScore().getBlueScore());
        assertEquals(2, snapshot.getCurrentGameNumber());

        // Text recognition - particularly for Player/team names - is not perfect.
        // Player/team names are later normalised using Liquipedia reference data
        final Team blueTeam = snapshot.getBlueTeam();
        assertEquals("PIRATES", blueTeam.getTeamName());
        assertEquals(TeamColour.BLUE, blueTeam.getTeamColour());
        assertEquals("ANDY", blueTeam.getPlayer1().getName());
        assertEquals("ARIS", blueTeam.getPlayer2().getName());
        assertEquals("FIV3UP", blueTeam.getPlayer3().getName());

        final Team orangeTeam = snapshot.getOrangeTeam();
        assertEquals("TSM", orangeTeam.getTeamName());
        assertEquals(TeamColour.ORANGE, orangeTeam.getTeamColour());
        assertEquals("CREAMZ.", orangeTeam.getPlayer1().getName());
        assertEquals("HOCKE", orangeTeam.getPlayer2().getName());
        assertEquals("WAHVEY", orangeTeam.getPlayer3().getName());

        assertEquals("NORTH AMERICAN OPEN QUALIFIER 2 | SWISS STAGE | ROUND 5 [2-2]", snapshot.getSeriesMetaData().getSeriesDescription());
    }

    @Test
    public void testNonGameScreenshot()
    {
        final GameScreenshotSubImageWrapper subImageWrapper = screenshotToSubImageTransformer.transformScreenshotToSubImages(NON_GAME_SCREENSHOT);
        final SeriesSnapshot snapshot = subImageToSeriesSnapshotTransformer.transform(subImageWrapper);

        final Game game = snapshot.getCurrentGame();
        assertEquals(TeamColour.NONE, game.getWinner());
        assertEquals(0, game.getScore().getBlueScore());
        assertEquals(0, game.getScore().getOrangeScore());
        assertEquals("", game.getClock().getDisplayedTime());
        assertEquals(0, game.getClock().getElapsedSeconds());
        assertEquals(false, game.getClock().isOvertime());

        assertEquals(5, snapshot.getBestOf());
        assertEquals(0, snapshot.getSeriesScore().getOrangeScore());
        assertEquals(0, snapshot.getSeriesScore().getBlueScore());
        assertEquals(1, snapshot.getCurrentGameNumber());

        final Team blueTeam = snapshot.getBlueTeam();
        assertEquals("", blueTeam.getTeamName());
        assertEquals(TeamColour.BLUE, blueTeam.getTeamColour());
        assertEquals("", blueTeam.getPlayer1().getName());
        assertEquals("", blueTeam.getPlayer2().getName());
        assertEquals("", blueTeam.getPlayer3().getName());

        final Team orangeTeam = snapshot.getOrangeTeam();
        assertEquals("", orangeTeam.getTeamName());
        assertEquals(TeamColour.ORANGE, orangeTeam.getTeamColour());
        assertEquals("", orangeTeam.getPlayer1().getName());
        assertEquals("", orangeTeam.getPlayer2().getName());
        assertEquals("", orangeTeam.getPlayer3().getName());

        assertEquals("", snapshot.getSeriesMetaData().getSeriesDescription());
    }

    @Test
    public void testGameScoreboard()
    {
        final GameScreenshotSubImageWrapper subImageWrapper = screenshotToSubImageTransformer.transformScreenshotToSubImages(GAME_1_SCOREBOARD);
        final SeriesSnapshot snapshot = subImageToSeriesSnapshotTransformer.transform(subImageWrapper);

        final Game game = snapshot.getCurrentGame();
        assertEquals(TeamColour.NONE, game.getWinner());
        assertEquals(1, game.getScore().getBlueScore());
        assertEquals(4, game.getScore().getOrangeScore());
        assertEquals("1", game.getClock().getDisplayedTime());
        assertEquals(0, game.getClock().getElapsedSeconds());
        assertEquals(false, game.getClock().isOvertime());

        assertEquals(5, snapshot.getBestOf());
        assertEquals(0, snapshot.getSeriesScore().getOrangeScore());
        assertEquals(0, snapshot.getSeriesScore().getBlueScore());
        assertEquals(1, snapshot.getCurrentGameNumber());

        final Team blueTeam = snapshot.getBlueTeam();
        assertEquals("|", blueTeam.getTeamName());
        assertEquals(TeamColour.BLUE, blueTeam.getTeamColour());
        assertEquals("", blueTeam.getPlayer1().getName());
        assertEquals("\\ &", blueTeam.getPlayer2().getName());
        assertEquals("", blueTeam.getPlayer3().getName());

        final Team orangeTeam = snapshot.getOrangeTeam();
        assertEquals("B :", orangeTeam.getTeamName());
        assertEquals(TeamColour.ORANGE, orangeTeam.getTeamColour());
        assertEquals("", orangeTeam.getPlayer1().getName());
        assertEquals("", orangeTeam.getPlayer2().getName());
        assertEquals("-", orangeTeam.getPlayer3().getName());

        assertEquals("ELIROPEAN NPEN NDLIALIFIER 2 | DLIARTERFINAL #3", snapshot.getSeriesMetaData().getSeriesDescription());
    }
}