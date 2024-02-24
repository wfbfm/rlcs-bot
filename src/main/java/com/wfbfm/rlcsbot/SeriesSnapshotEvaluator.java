package com.wfbfm.rlcsbot;

import com.wfbfm.rlcsbot.series.Series;
import com.wfbfm.rlcsbot.series.SeriesSnapshot;

public class SeriesSnapshotEvaluator
{
    public static SeriesSnapshotEvaluation evaluateSeries(final SeriesSnapshot snapshot, final Series existingSeries)
    {
        return SeriesSnapshotEvaluation.HIGHLIGHT;
    }
}
