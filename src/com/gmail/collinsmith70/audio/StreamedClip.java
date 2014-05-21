package com.gmail.collinsmith70.audio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class StreamedClip implements Playable, Runnable {
	private static final int BUFFER_SIZE = 4096;

	private final Path PATH;
	private final boolean PLAY_WHEN_INITIALIZED;

	private volatile Playable.State state;
	private volatile int loops;

	private long totalLength;
	private AudioInputStream audioInputStream;

	public StreamedClip(Path path) {
		this(path, true);
	}

	public StreamedClip(Path path, boolean playWhenInitialized) {
		this.PATH = path;
		this.PLAY_WHEN_INITIALIZED = playWhenInitialized;
		this.state = State.INITIALIZING;
		setLoops(1);
	}

	@Override
	public void run() {
		SourceDataLine dataLine = null;
		try (		InputStream inputStream = new BufferedInputStream(Files.newInputStream(PATH));
				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);) {
			this.audioInputStream = audioInputStream;
			this.totalLength = audioInputStream.getFrameLength() * audioInputStream.getFormat().getFrameSize();

			dataLine = AudioSystem.getSourceDataLine(audioInputStream.getFormat());
			dataLine.open(audioInputStream.getFormat(), BUFFER_SIZE);
			dataLine.start();

			int numRead;
			int numWritten;
			byte[] buffer = new byte[Math.min((int)totalLength, BUFFER_SIZE)];
			audioInputStream.mark((int)totalLength);
			state = State.PAUSED;
			if (PLAY_WHEN_INITIALIZED) {
				play();
			}

			while (true) {
				if (!state.equals(State.PLAYING)) {
					Thread.currentThread().yield();
					continue;
				}

				numRead = audioInputStream.read(buffer);
				if (numRead == -1) {
					if (loops == Playable.LOOP_FOREVER || 1 < loops) {
						if (loops != Playable.LOOP_FOREVER) {
							loops--;
						}

						stop();
						play();
						continue;
					}

					break;
				}

				numWritten = dataLine.write(buffer, 0, numRead);
			}
		} catch (UnsupportedAudioFileException e) {
			// if the File does not point to valid audio file data recognized by the system
			e.printStackTrace();
		} catch (IOException e) {
			// if an I/O error occurs
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// matching source data line is not available due to resource restrictions
			e.printStackTrace();
		} finally {
			if (dataLine != null) {
				dataLine.close();
			}

			state = State.FINISHED;
		}
	}

	@Override
	public synchronized boolean play() {
		if (isInitializing() || isPlaying() || isFinished()) {
			return false;
		}

		state = State.PLAYING;
		return true;
	}

	/*@Override
	public synchronized void playWhenInitialized() {
		if (isPlaying() || isFinished()) {
			return;
		} else if (isInitializing()) {
			playWhenInitialized = true;
			return;
		}

		play();
	}*/

	@Override
	public synchronized boolean pause() {
		if (!isPlaying()) {
			return false;
		}

		state = State.PAUSED;
		return true;
	}

	@Override
	public synchronized void stop() {
		boolean paused = pause();
		if (!paused) {
			return;
		}

		try {
			reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isInitializing() {
		return state.equals(State.INITIALIZING);
	}

	@Override
	public boolean isPlaying() {
		return state.equals(State.PLAYING);
	}

	@Override
	public boolean isFinished() {
		return state.equals(State.FINISHED);
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public synchronized boolean play(int loops) {
		setLoops(loops);
		return play();
	}

	@Override
	public void setLoops(int loops) {
		if (loops < 1 && loops != Playable.LOOP_FOREVER) {
			throw new IllegalArgumentException(String.format("loops must be > 0 or %d", Playable.LOOP_FOREVER));
		}

		this.loops = loops;
	}

	@Override
	public int getLoops() {
		return loops;
	}

	@Override
	public String toString() {
		return PATH.toString();
	}

	/*private void initialize() throws IOException, UnsupportedAudioFileException {
		// TODO: create a multithreaded resource manager
	}*/

	private void reset() throws IOException {
		audioInputStream.reset();
	}
}
