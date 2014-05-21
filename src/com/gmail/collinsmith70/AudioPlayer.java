package com.gmail.collinsmith70;

import com.gmail.collinsmith70.audio.AudioManager;
import com.gmail.collinsmith70.audio.StreamedClip;
import java.nio.file.Paths;

public class AudioPlayer {
	public static void main(String[] args) {
		StreamedClip audioClip = new StreamedClip(Paths.get(".", "res", "test.wav"));
		AudioManager am = new AudioManager();
		am.play(audioClip);
	}
}
