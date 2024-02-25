package com.wfbfm.rlcsbot.series.handler;

import com.wfbfm.rlcsbot.series.Series;
import com.wfbfm.rlcsbot.series.SeriesSnapshot;

public class SeriesUpdateHandler
{



    public SeriesSnapshotEvaluation evaluateSeries(final SeriesSnapshot snapshot, final Series existingSeries)
    {
        return SeriesSnapshotEvaluation.HIGHLIGHT;
    }

    private boolean isGameSnapshot(final SeriesSnapshot snapshot)
    {
        return false;
    }
}
