package com.wfbfm.rlcsbot.screenshotparser;

import org.junit.jupiter.api.Test;

import static com.wfbfm.rlcsbot.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

public class ScreenshotToSubImageTransformerTest
{
    private final ScreenshotToSubImageTransformer transformer = new ScreenshotToSubImageTransformer();

    @Test
    public void testTransformScreenshot()
    {
        final GameScreenshotSubImageWrapper outputSubImageWrapper = transformer.transformScreenshotToSubImages(GAME_1_SCORE_0_0);
        assertNotNull(outputSubImageWrapper);
        assertEquals("game_1_score_0_0.png", outputSubImageWrapper.getFileName());
        assertAllSubImagesArePopulated(outputSubImageWrapper);
    }

    @Test
    public void testTransformSeveralScreenshotsInSuccession()
    {
        final GameScreenshotSubImageWrapper output1 = transformer.transformScreenshotToSubImages(GAME_1_SCORE_0_0);
        assertNotNull(output1);
        assertEquals("game_1_score_0_0.png", output1.getFileName());
        assertAllSubImagesArePopulated(output1);

        final GameScreenshotSubImageWrapper output2 = transformer.transformScreenshotToSubImages(GAME_1_SCORE_1_0);
        assertNotNull(output2);
        assertEquals("game_1_score_1_0.png", output2.getFileName());
        assertAllSubImagesArePopulated(output2);

        final GameScreenshotSubImageWrapper output3 = transformer.transformScreenshotToSubImages(GAME_1_SCORE_2_0);
        assertNotNull(output3);
        assertEquals("game_1_score_2_0.png", output3.getFileName());
        assertAllSubImagesArePopulated(output3);

        assertNotEquals(output1.getBestOf(), output2.getBestOf());
        assertNotEquals(output1.getBestOf(), output3.getBestOf());
        assertNotEquals(output2.getBestOf(), output3.getBestOf());
    }

    @Test
    public void testLookupBySubImageType()
    {
        final GameScreenshotSubImageWrapper outputSubImageWrapper = transformer.transformScreenshotToSubImages(GAME_1_SCORE_0_0);
        assertEquals(outputSubImageWrapper.getDescription(), outputSubImageWrapper.getSubImageByType(SubImageType.DESCRIPTION));
        assertEquals(outputSubImageWrapper.getBestOf(), outputSubImageWrapper.getSubImageByType(SubImageType.BEST_OF));
        assertEquals(outputSubImageWrapper.getBlueGameScore(), outputSubImageWrapper.getSubImageByType(SubImageType.BLUE_GAME_SCORE));
        assertEquals(outputSubImageWrapper.getOrangeGameScore(), outputSubImageWrapper.getSubImageByType(SubImageType.ORANGE_GAME_SCORE));
    }

    private void assertAllSubImagesArePopulated(final GameScreenshotSubImageWrapper subImageWrapper)
    {
        assertNotNull(subImageWrapper.getDescription());
        assertNotNull(subImageWrapper.getBestOf());
        assertNotNull(subImageWrapper.getBlueGameScore());
        assertNotNull(subImageWrapper.getBluePlayer1());
        assertNotNull(subImageWrapper.getBluePlayer2());
        assertNotNull(subImageWrapper.getBluePlayer3());
        assertNotNull(subImageWrapper.getBlueSeriesTick1());
        assertNotNull(subImageWrapper.getBlueSeriesTick2());
        assertNotNull(subImageWrapper.getBlueSeriesTick3());
        assertNotNull(subImageWrapper.getBlueSeriesTick4());
        assertNotNull(subImageWrapper.getBlueTeam());
        assertNotNull(subImageWrapper.getClock());
        assertNotNull(subImageWrapper.getGameNumber());
        assertNotNull(subImageWrapper.getOrangeGameScore());
        assertNotNull(subImageWrapper.getOrangePlayer1());
        assertNotNull(subImageWrapper.getOrangePlayer2());
        assertNotNull(subImageWrapper.getOrangePlayer3());
        assertNotNull(subImageWrapper.getOrangeSeriesTick1());
        assertNotNull(subImageWrapper.getOrangeSeriesTick2());
        assertNotNull(subImageWrapper.getOrangeSeriesTick3());
        assertNotNull(subImageWrapper.getOrangeSeriesTick4());
        assertNotNull(subImageWrapper.getOrangeTeam());
    }
}