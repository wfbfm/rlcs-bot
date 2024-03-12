package com.wfbfm.rlcsbot.app;

import java.io.File;

public class RuntimeConstants
{
    public static final String LIQUIPEDIA_PAGE = "https://liquipedia.net/rocketleague/Rocket_League_Championship_Series/2024/Major_1/Europe/Open_Qualifier_2";
    public static boolean DEBUGGING_ENABLED = true;
    public static boolean RETAIN_PROCESSING_FILES = false;
    public static final int LEVENSHTEIN_MINIMUM_DISTANCE = 2;
    public static final int GAME_TIME_SECONDS = 300;
    public static final int SCREENSHOT_INTERVAL_MS = 10_000;
    public static final File INCOMING_DIRECTORY = new File("src/main/temp/incoming/");
    public static final File PROCESSING_DIRECTORY = new File("src/main/temp/processing/");
    public static final File COMPLETE_DIRECTORY = new File("src/main/temp/complete/");
    public static final File AUDIO_DIRECTORY = new File("src/main/temp/audio/");
    public static final int INCOMING_POLLING_SLEEP_TIME_MS = 200;
    public static final String BROADCAST_SCHEMA_FILE_PATH = "src/main/resources/broadcast-schema.csv";
    public static final String PYTHON_VENV_PATH = "venv" + File.separator + "Scripts" + File.separator + "python.exe";
    public static final String PYTHON_SCRIPT = "python_scripts" + File.separator + "transcribe_commentary.py";
    public static final File FULL_AUDIO_FILE = new File("src/main/temp/audio/full-audio.wav");
    public static final int TRANSCRIPTION_WAIT_TIME_MS = 10_000;
    public static final int TRANSCRIPTION_FILE_SECONDS = 30;
}
