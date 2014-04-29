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
	 * The value determiner that is used to return the value whilst gliding. null otherwise.
	 */
	private ValueDeterminer valueDeterminer;
	public FreestyleGliderEngine(View invalidatee, double initialValue) {
		super(invalidatee);
		fixedValue = initialValue;
	}
	@Override
	public synchronized final double getEndValue() {
		if (null != valueDeterminer) {
			return valueDeterminer.endValue;
		} else /* if (null == valueDeterminer) */ {
			return fixedValue;
		}
	}
	@Override
	public final double getValue() {
		final double result;
		synchronized (this) {
			// If no glide is happening, use the fixed value.
			if (null == valueDeterminer) {
				return fixedValue;
			// If a glide is happening, determine the value at this time using the value determiner.
			} else /* if (null != valueDeterminer) */ {
				final long time = System.nanoTime();
				result = valueDeterminer.determineValue(time);
				// Check whether the glide is now completed. Clear out the value determiner if so, while setting the end value as
				// the fixed value. Note that this check only considers the time. The glide could be completed before the time
				// reaches the end time, because the time is close to the end time and the value is somehow rounded. This check
				// does not optimise for such a situation.
				if (time > valueDeterminer.endTime) {
					valueDeterminer = null;
					return fixedValue = result;
				}
			}
		}
		// If the glide is not completed yet, invalidate so the view is drawn again at some point in the future. (This
		// commented if block should have been placed inside the synchronized block. It is nice to have the invalidate call
		// outside of that block, though.)
		/* if (null != valueDeterminer) { */
			invalidatee.invalidate();
		/* } */
		return result;
	}
	protected synchronized final void glide(ValueDeterminer newValueDeterminer) {
		// Save the value determiner. This line might overwrite an existing value determiner (of a less recently started
		// glide).
		valueDeterminer = newValueDeterminer;
		// The fixed value could be set to NaN. It will not be used as long as valueDeterminer is non-null.
		/* fixedValue = Double.NaN; */
	}
	@Override
	public synchronized final void stop(double value) {
		// null out the value determiner. The view might still be drawn again (once) because of the now stopped glide, which is
		// OK: the getValue method will simply return the fixed value set below.
		valueDeterminer = null;
		// Save the passed value as the fixed value.
		fixedValue = value;
	}
}