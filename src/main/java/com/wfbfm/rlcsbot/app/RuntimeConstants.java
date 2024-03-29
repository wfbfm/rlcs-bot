package com.wfbfm.rlcsbot.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RuntimeConstants
{
    public final static boolean BROADCAST_ENABLED = true;
    public final static boolean TRANSCRIPTION_ENABLED = true;
    public final static boolean LIVE_COMMENTARY_RECORDING_ENABLED = true;
    public final static boolean WEBSOCKET_ENABLED = true;
    public final static boolean ELASTIC_ENABLED = true;
    public static final String BROADCAST_URL = "https://www.twitch.tv/rocketleague/";
    public static final String LIQUIPEDIA_PAGE = "https://liquipedia.net/rocketleague/Rocket_League_Championship_Series/2024/Major_1";
    public static boolean DEBUGGING_ENABLED = true;
    public static boolean RETAIN_PROCESSING_FILES = false;
    public static boolean RETAIN_SCREENSHOTS = true;
    public static final int LEVENSHTEIN_MINIMUM_DISTANCE = 2;
    public static final int GAME_TIME_SECONDS = 300;
    public static final int SCREENSHOT_INTERVAL_MS = 10_000;
    public static final File TEMP_DIRECTORY = new File("src/main/temp/");
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
    public static final String ELASTIC_SEARCH_SERVER = "https://localhost:9200";
    public static final String ELASTIC_API_KEY;
    public static final String ELASTIC_INDEX_SERIES = "series";
    public static final String ELASTIC_INDEX_SERIES_EVENT = "seriesevent";
    public static final int WEBSOCKET_PORT = 8887;
    public static final int DEFAULT_BEST_OF = 5;

    static
    {
        try
        {
            ELASTIC_API_KEY = readApiKeyFromFile("appconfig/elastic_api_key.txt");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static String readApiKeyFromFile(final String filePath) throws IOException
    {
        return Files.readString(Paths.get(filePath)).trim();
    }
}
