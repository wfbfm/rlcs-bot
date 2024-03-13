package com.wfbfm.rlcsbot.audiotranscriber;

import com.wfbfm.rlcsbot.series.Player;
import com.wfbfm.rlcsbot.series.Series;
import com.wfbfm.rlcsbot.series.Team;
import com.wfbfm.rlcsbot.series.TeamColour;
import org.junit.jupiter.api.Test;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.TRANSCRIPTION_WAIT_TIME_MS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AudioTranscriptionDelegatorTest
{
    private final AudioTranscriptionDelegator transcriber = new AudioTranscriptionDelegator();

    @Test
    public void testTranscribeAudio() throws InterruptedException
    {
        final Series series = mock(Series.class);
        final Team blueTeam = new Team("GenG",
                new Player("ApparentlyJack"),
                new Player("Chronic"),
                new Player("FirstKiller"),
                TeamColour.BLUE
        );
        final Team orangeTeam = new Team("OG",
                new Player("noly"),
                new Player("JKnaps"),
                new Player("Comm"),
                TeamColour.ORANGE
        );
        when(series.getBlueTeam()).thenReturn(blueTeam);
        when(series.getOrangeTeam()).thenReturn(orangeTeam);

        transcriber.delegateAudioTranscription(series);

        // TODO: Find a better way to assert this
        Thread.sleep(TRANSCRIPTION_WAIT_TIME_MS + 5_000);
    }
}