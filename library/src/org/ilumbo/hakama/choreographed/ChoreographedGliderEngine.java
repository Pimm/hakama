package org.ilumbo.hakama.choreographed;

import org.ilumbo.hakama.GliderEngine;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.View;

/**
 * The default engine for Android versions with a choreographer.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public final class ChoreographedGliderEngine extends GliderEngine implements FrameCallback, Runnable {
	/**
	 * Whether the invalidatee should be invalidated when the new frame starts. true if getValue was called (thus onDraw of the
	 * invalidatee was called), but no new frame was started yet. false otherwise.
	 */
	private boolean invalidateOnFrame;
	/**
	 * The value that is returned.
	 */
	private double value;
	/**
	 * The value determiner that is used to return the value whilst gliding.
	 */
	private ValueDeterminer valueDeterminer;
	/**
	 * Hold this lock to access {@link #valueDeterminer}, {@link #value} or {@link #invalidateOnFrame}.
	 */
	private final Object stateLock;
	public ChoreographedGliderEngine(View invalidatee, double initialValue) {
		super(invalidatee);
		value = initialValue;
		stateLock = new Object();
	}
	/**
	 * @hide
	 */
	@Override
	public final void doFrame(long frameTime) {
		synchronized (stateLock) {
			// If a glide is happening (which is most likely is, otherwise this method would probably not be called), determine
			// the value at the frame time using the value determiner.
			if (null != valueDeterminer) {
				value = valueDeterminer.determineValue(frameTime);
				// Check whether the glide is now completed. null out the value determiner if so.
				if (frameTime > valueDeterminer.endTime) {
					valueDeterminer = null;
				}
				// Invalidate, if the flag is set.
				if (invalidateOnFrame) {
					invalidatee.invalidate();
					invalidateOnFrame = false;
				}
			}
		}
	}
	@Override
	public final double getEndValue() {
		synchronized (stateLock) {
			if (null != valueDeterminer) {
				return valueDeterminer.endValue;
			} else /* if (null == valueDeterminer) */ {
				return value;
			}
		}
	}
	@Override
	public final double getValue() {
		final double result;
		final boolean postFrameCallback;
		synchronized (stateLock) {
			result = value;
			// If a glide is happening, ensure this engine is notified when the next frame starts and invalidate the view
			// during that notification.
			if (postFrameCallback = (null != valueDeterminer)) {
				invalidateOnFrame = true;
			}
		}
		if (postFrameCallback) {
			Choreographer.getInstance().postFrameCallback(this);
		}
		return result;
	}
	protected final void glide(ValueDeterminer newValueDeterminer) {
		synchronized (stateLock) {
			// Set the value to the start value of the value determiner. The getValue method might me called before the doFrame
			// method is called. Setting the value ensures the expected result is returned.
			value = 
			// Save the value determiner. This might overwrite an existing value determiner (of a less recently started glide).
					(valueDeterminer = newValueDeterminer).startValue;
		}
		// Ensure this engine is notified when the next frame starts. When this happens, the value will be determined.
		if (null != android.os.Looper.myLooper()) {
			Choreographer.getInstance().postFrameCallback(this);
		} else /* if (null != android.os.Looper.myLooper()) */ {
			invalidatee.post(this);
		}
	}
	/**
	 * @hide
	 */
	@Override
	public final void run() {
		Choreographer.getInstance().postFrameCallback(this);
	}
	@Override
	public final void stop(double value) {
		synchronized (stateLock) {
			// null out any value determiner that might exist. The doFrame method might still be called (once), but that method
			// will soon enough find that the value determiner is gone.
			valueDeterminer = null;
			// Save the passed value.
			this.value = value;
		}
	}
}