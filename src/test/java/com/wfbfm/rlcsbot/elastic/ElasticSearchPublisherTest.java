package com.wfbfm.rlcsbot.elastic;

import com.wfbfm.rlcsbot.screenshotparser.GameScreenshotProcessorUtils;
import com.wfbfm.rlcsbot.series.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ElasticSearchPublisherTest
{
    private final Player bluePlayer1 = new Player("bluePlayer1");
    private final Player bluePlayer2 = new Player("bluePlayer2");
    private final Player bluePlayer3 = new Player("bluePlayer3");
    private final Player orangePlayer1 = new Player("orangePlayer1");
    private final Player orangePlayer2 = new Player("orangePlayer2");
    private final Player orangePlayer3 = new Player("orangePlayer3");
    private final Team blueTeam = new Team("blueTeam", bluePlayer1, bluePlayer2, bluePlayer3, TeamColour.BLUE);
    private final Team orangeTeam = new Team("orangeTeam", orangePlayer1, orangePlayer2, orangePlayer3, TeamColour.ORANGE);

    private final ElasticSearchPublisher elasticSearchPublisher = new ElasticSearchPublisher();

    @Test
    public void testElasticPublisher()
    {
        elasticSearchPublisher.uploadSeriesSnapshot(mockSeriesSnapshot(0, 0, 0, 0, 7, "5:00"), "snapshot1");
        elasticSearchPublisher.uploadSeriesSnapshot(mockSeriesSnapshot(3, 2, 1, 2, 7, "3:19"), "snapshot2");
        elasticSearchPublisher.uploadSeriesSnapshot(mockSeriesSnapshot(0, 1, 2, 2, 7, "4:31"), "snapshot3");
    }

    @Test
    public void testJacksonDeserialisation()
    {
        final SeriesEvent seriesEvent = elasticSearchPublisher.searchForSeriesEvent("Event1-Karmine Corp-Team Vitality-2024-03-16-1710552480311");
        assertNotNull(seriesEvent);
        seriesEvent.setCommentary("Test-Commentary");
        elasticSearchPublisher.updateSeriesEvent(seriesEvent);
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
        return new SeriesSnapshot(metaData, game, currentGameNumber, new Score(blueSeriesScore, orangeSeriesScore), blueTeam, orangeTeam, bestOf);
    }
}