package com.wfbfm.rlcsbot.audiotranscriber;

import org.junit.jupiter.api.Test;

public class CommentaryRecorderTest
{
    @Test
    public void testCommentaryRecorder() throws InterruptedException
    {
        final String streamUrl = "https://www.twitch.tv/rocketleague";
        final CommentaryRecorder commentaryRecorder = new CommentaryRecorder(streamUrl);
        commentaryRecorder.run();
        Thread.sleep(10_000);
    }
}