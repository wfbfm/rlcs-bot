package com.wfbfm.rlcsbot.audiotranscriber;

import com.wfbfm.rlcsbot.app.ApplicationContext;
import org.junit.jupiter.api.Test;

public class CommentaryRecorderTest
{
    @Test
    public void testCommentaryRecorder() throws InterruptedException
    {
        final String streamUrl = "https://www.twitch.tv/rocketleague";
        final ApplicationContext applicationContext = new ApplicationContext(streamUrl, "test", true);
        final CommentaryRecorder commentaryRecorder = new CommentaryRecorder(applicationContext);
        commentaryRecorder.run();
        Thread.sleep(10_000);
    }
}