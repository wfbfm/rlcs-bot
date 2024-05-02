package com.wfbfm.rlcsbot.app;

import java.io.File;

public class RuntimeConstants
{
    public final static boolean BROADCAST_ENABLED = false;
    public final static boolean TRANSCRIPTION_ENABLED = false;
    public final static boolean LIVE_COMMENTARY_RECORDING_ENABLED = false;
    public final static boolean SCREENSHOT_PROCESSING_ENABLED = true;
    public final static boolean WEBSOCKET_ENABLED = true;
    public final static boolean ADMIN_WEBSOCKET_ENABLED = true;
    public final static boolean ELASTIC_ENABLED = true;
    public static final String BROADCAST_URL = "https://www.twitch.tv/videos/2131987392?t=03h05m37s";
    public static final String LIQUIPEDIA_PAGE = "https://liquipedia.net/rocketleague/Rocket_League_Championship_Series/2024/Major_2/North_America/Open_Qualifier_4";
    public static boolean DEBUGGING_ENABLED = true;
    public static boolean RETAIN_PROCESSING_FILES = false;
    public static boolean RETAIN_SCREENSHOTS = true;
    public static final int LEVENSHTEIN_MINIMUM_DISTANCE = 2;
    public static final int GAME_TIME_SECONDS = 300;
    public static final int SCREENSHOT_INTERVAL_MS = 10_000;
    public static final File TEMP_DIRECTORY = new File("temp/");
    public static final File INCOMING_DIRECTORY = new File("temp/incoming/");
    public static final File PROCESSING_DIRECTORY = new File("temp/processing/");
    public static final File COMPLETE_DIRECTORY = new File("temp/complete/");
    public static final File AUDIO_DIRECTORY = new File("temp/audio/");
    public static final File LOGO_DIRECTORY = new File("temp/logos/");
    public static final int INCOMING_POLLING_SLEEP_TIME_MS = 200;
    public static final String BROADCAST_SCHEMA_FILE_PATH = "src/main/resources/broadcast-schema.csv";
    public static final String DISPLAY_NAME_MAPPINGS = "src/main/resources/display-name-mappings.csv";
    public static final String PYTHON_VENV_PATH = "venv" + File.separator + "bin" + File.separator + "python";
    public static final String PYTHON_SCRIPT = "python_scripts" + File.separator + "transcribe_commentary.py";
    public static final File FULL_AUDIO_FILE = new File("/temp/audio/full-audio.wav");
    public static final int TRANSCRIPTION_WAIT_TIME_MS = 10_000;
    public static final int TRANSCRIPTION_FILE_SECONDS = 20;
    public static final String ELASTICSEARCH_USERNAME = "no";
    public static final String ELASTICSEARCH_PASSWORD = "no";
    public static final String ELASTICSEARCH_HOST = "no";
    public static final String ELASTIC_INDEX_SERIES = "series";
    public static final String ELASTIC_INDEX_SERIES_EVENT = "seriesevent";
    public static final int WEBSOCKET_PORT = 1;
    public static final int SECRET_ADMIN_PORT = 1;
    public static final int DEFAULT_BEST_OF = 5;
}
