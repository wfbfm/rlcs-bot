package com.wfbfm.rlcsbot.audiotranscriber;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TranscriptionPollerTest
{
    // TODO: uplift this test once the upload to elastic is built.
    @Test
    public void testPollTranscriptionFile()
    {
        final TranscriptionPoller transcriptionPoller = new TranscriptionPoller();
        transcriptionPoller.run();
    }
}