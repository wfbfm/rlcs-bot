import subprocess
import time
import whisper


def capture_last_seconds(input_file, output_file, duration=30):
    # Use ffmpeg to create a trimmed copy of the input file
    subprocess.run([
        "ffmpeg",
        "-y",  # Overwrite output file if it exists
        "-ss", str(max(0, get_file_duration(input_file) - duration)),  # Start trimming from the last 30 seconds
        "-i", input_file,
        "-t", str(duration),  # Set the duration of the output
        "-c", "pcm_s16le",  # Use PCM codec for compatibility
        output_file
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
    input_file = r"../src/main/temp/audio/full-audio.wav"
    output_file = r"../src/main/temp/audio/trimmed-audio.wav"
    conditioning_words = ["ApparentlyJack", "FirstKiller", "GenG"]
    initial_prompt = " ".join(conditioning_words)

    capture_last_seconds(input_file, output_file)
    print(transcribe_audio_file(output_file, initial_prompt))
