package com.gmail.collinsmith70.audio;

/**
 * A {@link Playable} object is an object which has a {@link State} that
 * represents the current state of a {@link Playable}.
 *
 * A common analogy for this relationship is with an audio file. An audio file,
 * once loaded, can be played or paused at will until it reaches its finished
 * state.
 *
 * @author Collin Smith <collinsmith@csupomona.edu>
 */
public interface Playable {
	/**
	 * Constant representing the value that symbolizes that the current
	 * {@link Playable} is looping indefinitely
	 */
	int LOOP_FOREVER = -1;

	/**
	 * A {@link State} object represents the current state of a
	 * {@link Playable} object. Each member of this class represents a
	 * different state, each with its own well-defined meaning.
	 */
	enum State {
		/**
		 * Represents that this {@link Playable} is still initializing
		 * (loading resources that it needs before the head can move)
		 */
		INITIALIZING,

		/**
		 * Represents that this {@link Playable} is in a paused state, with
		 * the head unmoving
		 */
		PAUSED,

		/**
		 * Represents that this {@link Playable} is in a playable state,
		 * with the head moving
		 */
		PLAYING,

		/**
		 * Represents that this {@link Playable} is in a finished state,
		 * no longer able to move its head
		 */
		FINISHED
	}

	/**
	 * Starts playing this {@link Playable} if and only if it is in the
	 * {@link State#PAUSED} state
	 *
	 * @return {@code true} if this {@link Playable} is now playing as a
	 *	direct result of this invocation, otherwise {@code false}
	 *
	 * @see #isPlaying()
	 */
	boolean play();

	/**
	 * Plays this {@link Playable} once it has finished initializing. This
	 * method will not block, and merely notifies the {@link Playable} to
	 * change its state once it has been fully initialized. If this
	 * {@link Playable} has already been initialized prior to the invocation
	 * of this method, then it will begin playing immediately. If this
	 * {@link Playable} is finished or playing, then the state of this
	 * {@link Playable} will not change
	 *
	 * @see #play();
	 *
	 * TODO: this method is not needed right now because it is redundant.
	 *	Currently, there is no resource manager on a separate thread, so
	 *	handling whether or not a Playable should start when initialized
	 *	is a parameter which belongs to the constructor of an instance
	 */
	//void playWhenInitialized();

	/**
	 * Stops moving the head of this {@link Playable} if and only if it is
	 * playing
	 *
	 * @return {@code true} if this {@link Playable} has been paused as a
	 *	direct result of this invocation, otherwise {@code false}
	 *
	 * @see #isPlaying()
	 * @see #stop()
	 */
	boolean pause();

	/**
	 * Resets and pauses and the head of this {@link Playable} if and only if
	 * it is playing. This {@link Playable} can then be restarted with the
	 * {@link #play()} method
	 *
	 * @see #pause()
	 */
	void stop();

	/**
	 * Returns whether or not this {@link Playable} is initializing. A
	 * {@link Playable} which is initializing is currently in a state where
	 * resources are still being loaded, and therefore this {@link Playable}
	 * cannot be played, paused or stopped.
	 *
	 * @return {@code true} if this {@link Playable} is initializing,
	 *	otherwise {@code false}
	 */
	boolean isInitializing();

	/**
	 * Returns whether or not this {@link Playable} is playing. A
	 * {@link Playable} which is not playing is currently in its paused
	 * state
	 *
	 * @return {@code true} if this {@link Playable} is playing, otherwise
	 *	{@code false}
	 */
	boolean isPlaying();

	/**
	 * Returns whether or not this {@link Playable} has finished. A
	 * {@link Playable} which is finished has reached its final state and
	 * the head can no longer advance. A {@link Playable} which has finished
	 * may be reset using the {@link #stop()} method
	 *
	 * @return {@code true} if this {@link Playable} has finished, otherwise
	 *	{@code false}
	 *
	 * @see #stop()
	 */
	boolean isFinished();

	/**
	 * Returns the current {@link State} of this {@link Playable}
	 *
	 * @return a member of {@link State} which represents the current state of
	 *	this playable
	 */
	State getState();

	/**
	 * Starts playing this {@link Playable} if and only if it is in the
	 * {@link State#PAUSED} state for a specified number of iterations
	 *
	 * @param loops number of times to loop this {@link Playable},
	 *	{@link #LOOP_FOREVER} to loop indefinitely
	 *
	 * @return {@code true} if this {@link Playable} is now playing as a
	 *	direct result of this invocation, otherwise {@code false}
	 *
	 * @see #play()
	 * @see #setLoops(int)
	 */
	boolean play(int loops);

	/**
	 * Sets the number of times this {@link Playable} should loop before
	 * entering its finished state
	 *
	 * @param loops number of times to loop this {@link Playable},
	 *	{@link #LOOP_FOREVER} to loop indefinitely
	 *
	 * @see #getLoops()
	 */
	void setLoops(int loops);

	/**
	 * Returns the current number of iterations remaining until this loop
	 * terminates. By default, a {@link Playable} will only perform a single
	 * iteration
	 *
	 * @return number of iterations remaining in this {@link Playable} before
	 *	it is allowed to enter its finished state
	 *
	 * @see #setLoops(int)
	 */
	int getLoops();

	/**
	 * Returns whether or not this {@link Playable} is currently in a looping
	 * state. A {@link Playable} is said to be in a looping state if it will
	 * repeat when it finishes an iteration (when the head reaches the end)
	 *
	 * @return {@code true} if this {@link Playable} is looping, otherwise
	 *	{@code false}
	 */
	default boolean isLooping() {
		int loopsRemaining = getLoops();
		if (loopsRemaining == LOOP_FOREVER) {
			return true;
		}

		return loopsRemaining != 0;
	}
}
