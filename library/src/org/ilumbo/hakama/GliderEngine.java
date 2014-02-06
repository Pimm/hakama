package org.ilumbo.hakama;

import android.view.View;

/**
 * The engine used by gliders to do all of the timing work and invalidations.
 */
public abstract class GliderEngine {
	/**
	 * Determines the value at a certain point in time during a glide.
	 */
	protected static final class ValueDeterminer {
		/**
		 * The amount that is to be added to the start value at the end time.
		 */
		private final double delta;
		/**
		 * The duration of the glide, in nanoseconds.
		 */
		private final double duration;
		/**
		 * The time at which the glide is completed, and at which the value equals the start value plus the delta.
		 */
		public final long endTime;
		/**
		 * The value at the start time.
		 */
		private final double startValue;
		/**
		 * The time at which the glide starts, and at which the value equals the start value.
		 */
		private final long startTime;
		public ValueDeterminer(double startValue, double endValue, long startTime, long duration) {
			delta = endValue - (this.startValue = startValue);
			this.startTime = startTime;
			this.duration = duration;
			endTime = startTime + duration;
		}
		/**
		 * Returns the value for the passed time.
		 */
		public final double determineValue(long time) {
			final double factor = (time - startTime) / duration;
			if (factor >= 1) {
				return startValue + delta;
			}
			return startValue + delta * factor;
		}
	}
	/**
	 * The view that is invalidated while gliding and potentially while stopping.
	 */
	protected final View invalidatee;
	protected GliderEngine(View invalidatee) {
		this.invalidatee = invalidatee;
	}
	/**
	 * Determines the duration of a glide in nanoseconds, based on the start and end values, and the average speed.
	 */
	protected final long determineDuration(double startValue, double endValue, double averageSpeed) {
		// Determine the distance first, which is an absolute (non-negative) number.
		double distance = startValue - endValue;
		if (distance < 0) {
			distance = -distance;
		}
		// The duration is the distance divided by the average speed (in a per-nanosecond base).
		return Math.round((distance / averageSpeed) * 1e9);
	}
	/**
	 * Returns the current value. The view passed to the constructor of the glider should use this method to obtain the current
	 * value.
	 *
	 * If the value is currently being glided, calling this method will invalidate said view at some point in the future.
	 */
	public abstract double getValue();
	/**
	 * Glides the value from the passed start value to the passed end value. The passed speed is the amount that would be added
	 * to the start value or substracted from it every second to reach the end value, if the glide were linear.
	 *
	 * Calling this method ends any previously started glides.
	 *
	 * If the appropriate flag is set, the view passed to the constructor of the glider will be invalidated immediately. This
	 * is useful if the start value does not equal the current one, and you want the start value to be shown without delay.
	 * Regardless of whether that flag is set, calling this method causes the view to be invalidated at some point in the
	 * future. The view must then obtain the current value from this glider and use it for drawing.
	 */
	public abstract void glide(double startValue, double endValue, double averageSpeed, boolean invalidateImmediately);
	/**
	 * Sets the value to the passed value, ending any previously started glides.
	 */
	public abstract void stop(double value, boolean invalidateImmediately);
}