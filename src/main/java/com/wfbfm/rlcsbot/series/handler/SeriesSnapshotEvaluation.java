package com.wfbfm.rlcsbot.series.handler;

public enum SeriesSnapshotEvaluation
{
    NOT_GAME_SCREENSHOT,
    SERIES_NOT_STARTED_YET,
    HIGHLIGHT,
    SCORE_UNCHANGED,
    GAME_SCORE_CHANGED,
    SERIES_SCORE_CHANGED,
    SERIES_COMPLETE,
    NEW_SERIES,
    INVALID_NEW_SERIES
}
