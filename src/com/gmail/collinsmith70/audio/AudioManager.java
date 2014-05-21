package com.gmail.collinsmith70.audio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

public class AudioManager {
	private static final String PREF_AUDIO_CHANNELS = "audio_channels";
	private static final int DEFAULT_CHANNELS = 16;

	private final ExecutorService POOL;

	public AudioManager() {
		// TODO: change to use a ThreadPoolExecutor, which will give more flexability
		Preferences prefs = Preferences.systemNodeForPackage(AudioManager.class);
		int numChannels = prefs.getInt(PREF_AUDIO_CHANNELS, DEFAULT_CHANNELS);
		numChannels = Math.max(numChannels, 1);
		this.POOL = Executors.newFixedThreadPool(numChannels, r -> {
			Thread t = new Thread(r);
			t.setName(String.format("%s Thread #%d", AudioManager.class.getSimpleName(), t.getId()));
			return t;
		});
	}

	public <E extends Playable&Runnable> void play(E audioClip) {
		POOL.execute(audioClip);
	}
}
