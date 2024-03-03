package com.wfbfm.rlcsbot.audiotranscriber;

import com.wfbfm.rlcsbot.series.Series;

import static com.wfbfm.rlcsbot.app.RuntimeConstants.PYTHON_SCRIPT;
import static com.wfbfm.rlcsbot.app.RuntimeConstants.PYTHON_VENV_PATH;

public class AudioTranscriber
{

    public static void main(String[] args)
    {
        transcribeAudio(null);
    }

    public static void transcribeAudio(final Series series)
    {
        // final String initialPrompt = generateInitialPrompt(series);
        final String initialPrompt = "ApparentlyJack FirstKiller GenG";
        final String inputFilePath = "src/main/temp/audio/full-audio.wav";
        final String outputFilePath = "src/main/temp/audio/trimmed-audio.wav";

        // Create a new thread for running the Python script
        Thread pythonScriptThread = new Thread(() ->
        {
            try
            {
                ProcessBuilder processBuilder = new ProcessBuilder(
                        PYTHON_VENV_PATH,
                        PYTHON_SCRIPT,
                        inputFilePath,
                        outputFilePath,
                        "\"" + initialPrompt + "\"" // this arg contains spaces
                );
                Process process = processBuilder.start();

                // Wait for the process to complete (optional)
                int exitCode = process.waitFor();
                System.out.println("Python script exited with code: " + exitCode);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        // Start the Python script thread
        pythonScriptThread.start();

        // Continue with the rest of your GameScreenshotProcessor logic
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
