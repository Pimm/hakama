package org.ilumbo.hakama.freestyle;

import org.ilumbo.hakama.GliderEngine;

import android.view.View;

/**
 * The engine that can be used on Android versions which do not have a choreographer.
 */
public final class FreestyleGliderEngine extends GliderEngine {
	/**
	 * The value that is returned if no gliding is happening. Otherwise, this value is bogus. Garbage.
	 */
	private double fixedValue;
	/**
	 * The invalidator used to invalidate the view whilst gliding. null otherwise.
	 */
	private Invalidator invalidator;
	/**
	 * The value determiner that is used to return the value whilst gliding. null otherwise.
	 */
	private ValueDeterminer valueDeterminer;
	public FreestyleGliderEngine(View invalidatee, double initialValue) {
		super(invalidatee);
		fixedValue = initialValue;
	}
	@Override
	public final double getEndValue() {
		if (null != valueDeterminer) {
			return valueDeterminer.endValue;
		} else /* if (null == valueDeterminer) */ {
			return fixedValue;
		}
	}
	@Override
	public synchronized final double getValue() {
		final double value;
		// If no glide is happening, use the fixed value.
		if (null == valueDeterminer) {
			value = fixedValue;
		// If a glide is happening, determine the value at this time using the value determiner.
		} else /* if (null != valueDeterminer) */ {
			final long time = System.nanoTime();
			value = valueDeterminer.determineValue(time);
			// Check whether the glide is now completed. Set the job of the invalidator to stop and clear out the value
			// determiner and invalidator while setting the end value as the fixed value. Note that this check only considers
			// the time. The glide could be completed before the time reaches the end time, because the time is close to the
			// end time and the value is somehow rounded. This check does not optimise for such a situation.
			if (time > valueDeterminer.endTime) {
				invalidator.setJob(Invalidator.JOB_STOP);
				valueDeterminer = null;
				invalidator = null;
				fixedValue = value;
			// If the glide is not completed yet, set the job of the invalidator to invalidate again so the view is again
			// invalidated at some point in the future. Start the invalidator if it hasn't been started before.
			} else /* if (time <= valueDeterminer.endTime) */ {
				if (false == invalidator.started) {
					invalidator.start();
					// (If the has not been started before, the job is already set to invalidate.)
				} else /* if (invalidator.started) */ {
					invalidator.setJob(Invalidator.JOB_INVALIDATE);
				}
			}
		}
		return value;
	}
	protected synchronized final void glide(ValueDeterminer newValueDeterminer) {
		// If an invalidator was already present, set the job to stop and clear it out. It might still invalidate the view
		// (once), which is OK: the getValue method will simply use the value determiner created below.
		if (null != invalidator) {
			invalidator.setJob(Invalidator.JOB_STOP);
		}
		// Save the value determiner. If an invalidator was present, this line will overwrite an existing value determiner (of
		// a less recently started glide).
		valueDeterminer = newValueDeterminer;
		// Create the invalidator. It will not be started now; instead it will be started the next time the view is drawn. See
		// the getValue method.
		invalidator = new Invalidator(invalidatee);
	}
	@Override
	public synchronized final void stop(double value) {
		// If an invalidator was present, set the job to stop and clear it out. It might still invalidate the view (once),
		// which is OK: the getValue method will simply return the fixed value set below.
		if (null != invalidator) {
			invalidator.setJob(Invalidator.JOB_STOP);
			valueDeterminer = null;
			invalidator = null;
		}
		// Save the passed value as the fixed value.
		fixedValue = value;
	}
}