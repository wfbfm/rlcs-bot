import subprocess
import whisper
import sys
import os


def trim_audio_segment(full_audio_file, trimmed_audio_file, start_seconds, seconds_to_capture):
    # Use ffmpeg to create a trimmed copy of the input file
    subprocess.run([
        "ffmpeg",
        "-y",  # Overwrite output file if it exists
        "-ss", str(start_seconds),
        "-i", full_audio_file,
        "-t", str(seconds_to_capture),  # Set the duration of the output
        "-c", "pcm_s16le",  # Use PCM codec for compatibility
        trimmed_audio_file
    ])


def get_file_duration(file_path):
    # Use ffprobe to get the duration of the input file
    result = subprocess.run([
        "ffprobe",
        "-v", "error",
        "-show_entries", "format=duration",
        "-of", "default=noprint_wrappers=1:nokey=1",
        file_path
    ], capture_output=True, text=True)

    try:
        return float(result.stdout.strip())
    except ValueError:
        return 0


def transcribe_audio_file(file_path, initial_prompt):
    model = whisper.load_model("base")
    result = model.transcribe(file_path, initial_prompt=initial_prompt, fp16=False)
    return result['text']


if __name__ == "__main__":
    full_audio_filename = sys.argv[1]
    trimmed_audio_filename = sys.argv[2]
    seconds_to_capture = int(sys.argv[3])
    start_seconds = int(sys.argv[4])
    transcription_filename = sys.argv[5]
    initial_prompt = sys.argv[6]

    if start_seconds == 0:
        # if start_seconds has not been set - get the last seconds_to_capture of the clip
        start_seconds = max(0, get_file_duration(full_audio_filename) - seconds_to_capture)

    trim_audio_segment(full_audio_filename, trimmed_audio_filename, start_seconds, seconds_to_capture)
    transcription = transcribe_audio_file(trimmed_audio_filename, initial_prompt)
    input_directory = os.path.dirname(full_audio_filename)
    transcription_file = os.path.join(input_directory, transcription_filename)
    with open(transcription_file, 'w') as txt_file:
        txt_file.write(transcription)
