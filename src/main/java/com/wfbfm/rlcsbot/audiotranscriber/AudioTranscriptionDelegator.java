package com.wfbfm.rlcsbot.audiotranscriber;

import com.wfbfm.rlcsbot.series.Series;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.*;

public class AudioTranscriptionDelegator
{
    private static final String FULL_AUDIO_PATH = FULL_AUDIO_FILE.getAbsolutePath();
    private static final String TRIMMED_FILE_PATH = FULL_AUDIO_FILE.getParentFile().getAbsolutePath() + File.separator + "trimmed-audio-%d.wav";
    private static final String TRANSCRIPTION_FILENAME = FULL_AUDIO_FILE.getParentFile().getAbsolutePath() + File.separator + "TRANSCRIPTION-%d.txt";
    private final Logger logger = Logger.getLogger(AudioTranscriptionDelegator.class.getName());
    private int numberOfTranscriptions = 0;

    public void delegateAudioTranscription(final Series series)
    {
        numberOfTranscriptions++;
        if (!FULL_AUDIO_FILE.exists())
        {
            logger.log(Level.SEVERE, "Unable to transcribe audio - the full audio file does not exist!");
            return;
        }
        final String initialPrompt = generateInitialPrompt(series);
        logger.log(Level.INFO, String.format("In %d ms, attempting audio transcription #%d", TRANSCRIPTION_WAIT_TIME_MS, numberOfTranscriptions));

        final Thread transcriptionScriptThread = createTranscriptionThread(initialPrompt);
        transcriptionScriptThread.start();
    }

    private Thread createTranscriptionThread(final String initialPrompt)
    {
        return new Thread(() ->
        {
            try
            {
                Thread.sleep(TRANSCRIPTION_WAIT_TIME_MS);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
            try
            {
                final ProcessBuilder processBuilder = createTranscriptionProcess(initialPrompt);
                final Process process = processBuilder.start();
                final int exitCode = process.waitFor();
                logger.log(Level.INFO, String.format("Python transcription script exited with code: %d", exitCode));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    private ProcessBuilder createTranscriptionProcess(final String initialPrompt)
    {
        return new ProcessBuilder(
                PYTHON_VENV_PATH,
                PYTHON_SCRIPT,
                FULL_AUDIO_PATH,
                String.format(TRIMMED_FILE_PATH, numberOfTranscriptions),
                String.valueOf(TRANSCRIPTION_FILE_SECONDS),
                String.format(TRANSCRIPTION_FILENAME, numberOfTranscriptions),
                "\"" + initialPrompt + "\"" // spaces in the initialPrompt need to be escaped
        );
    }

    private String generateInitialPrompt(final Series series)
    {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(series.getBlueTeam().getTeamName()).append(" ");
        stringBuilder.append(series.getBlueTeam().getPlayer1().getName()).append(" ");
        stringBuilder.append(series.getBlueTeam().getPlayer2().getName()).append(" ");
        stringBuilder.append(series.getBlueTeam().getPlayer3().getName()).append(" ");
        stringBuilder.append(series.getOrangeTeam().getTeamName()).append(" ");
        stringBuilder.append(series.getOrangeTeam().getPlayer1().getName()).append(" ");
        stringBuilder.append(series.getOrangeTeam().getPlayer2().getName()).append(" ");
        stringBuilder.append(series.getOrangeTeam().getPlayer3().getName());
        return stringBuilder.toString();
    }
}
