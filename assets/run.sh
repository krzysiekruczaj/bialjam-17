#!/bin/bash

ffmpeg -i hit00.mp3.flac -ab 320k -map_metadata 0 -id3v2_version 3 hit0.mp3
ffmpeg -i hit01.mp3.flac -ab 320k -map_metadata 0 -id3v2_version 3 hit1.mp3
ffmpeg -i hit02.mp3.flac -ab 320k -map_metadata 0 -id3v2_version 3 hit2.mp3
ffmpeg -i hit03.mp3.flac -ab 320k -map_metadata 0 -id3v2_version 3 hit3.mp3
ffmpeg -i hit04.mp3.flac -ab 320k -map_metadata 0 -id3v2_version 3 hit4.mp3
ffmpeg -i hit05.mp3.flac -ab 320k -map_metadata 0 -id3v2_version 3 hit5.mp3
ffmpeg -i hit06.mp3.flac -ab 320k -map_metadata 0 -id3v2_version 3 hit6.mp3
ffmpeg -i hit07.mp3.flac -ab 320k -map_metadata 0 -id3v2_version 3 hit7.mp3
ffmpeg -i hit08.mp3.flac -ab 320k -map_metadata 0 -id3v2_version 3 hit8.mp3
ffmpeg -i hit09.mp3.flac -ab 320k -map_metadata 0 -id3v2_version 3 hit9.mp3

ffmpeg -i hit0.mp3 -acodec pcm_s16le -ac 1 -ar 16000 hit0.wav
ffmpeg -i hit1.mp3 -acodec pcm_s16le -ac 1 -ar 16000 hit1.wav
ffmpeg -i hit2.mp3 -acodec pcm_s16le -ac 1 -ar 16000 hit2.wav
ffmpeg -i hit3.mp3 -acodec pcm_s16le -ac 1 -ar 16000 hit3.wav
ffmpeg -i hit4.mp3 -acodec pcm_s16le -ac 1 -ar 16000 hit4.wav
ffmpeg -i hit5.mp3 -acodec pcm_s16le -ac 1 -ar 16000 hit5.wav
ffmpeg -i hit6.mp3 -acodec pcm_s16le -ac 1 -ar 16000 hit6.wav
ffmpeg -i hit7.mp3 -acodec pcm_s16le -ac 1 -ar 16000 hit7.wav
ffmpeg -i hit8.mp3 -acodec pcm_s16le -ac 1 -ar 16000 hit8.wav
ffmpeg -i hit9.mp3 -acodec pcm_s16le -ac 1 -ar 16000 hit9.wav
