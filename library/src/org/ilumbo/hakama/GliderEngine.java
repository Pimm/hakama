package org.ilumbo.hakama;

import org.ilumbo.hakama.interpolation.ElapsedFactorInterpolator;

import android.view.View;

/**
 * The engine used by gliders to do all of the timing work and invalidations.
 */
public abstract class GliderEngine {
	/**
	 * Determines the value at a certain point in time during a glide.
	 */
	protected static abstract class ValueDeterminer {
		/**
		 * The amount that is to be added to the start value at the end time.
		 */
		protected final double delta;
		/**
		 * The duration of the glide, in nanoseconds.
		 */
		protected final double duration;
		/**
		 * The time at which the glide is completed, and at which the value equals the start value plus the delta.
		 */
		public final long endTime;
		/**
		 * The value at the end time. If this value is required, use it instead of adding {@link ValueDeterminer#startValue}
		 * and {@link ValueDeterminer#delta}. Adding those two values would in theory produce the same value, but doing so
		 * might introduce rounding errors.
		 */
		public final double endValue;
		/**
		 * The time at which the glide starts, and at which the value equals the start value.
		 */
		protected final long startTime;
		/**
		 * The value at the start time.
		 */
		public final double startValue;
		public ValueDeterminer(double startValue, double endValue, long startTime, long duration) {
			delta = (this.endValue = endValue) - (this.startValue = startValue);
			this.startTime = startTime;
			this.duration = duration;
			endTime = startTime + duration;
		}
		/**
		 * Returns the value for the passed time.
		 */
		public abstract double determineValue(long time);
	}
	/**
	 * Determines the value at a certain point in time during a non-linear glide.
	 */
	protected static final class LinearValueDeterminer extends ValueDeterminer {
		public LinearValueDeterminer(double startValue, double endValue, long startTime, long duration) {
			super(startValue, endValue, startTime, duration);
		}
		@Override
		public final double determineValue(long time) {
			if (time <= startTime) {
				return startValue;
			}
			final double elapsedFactor = (time - startTime) / duration;
			if (elapsedFactor >= 1) {
				return startValue + delta;
			}
			return startValue + delta * elapsedFactor;
		}
	}
	/**
	 * Determines the value at a certain point in time during an interpolated glide. The glide could be, and probably is,
	 * non-linear.
	 */
	protected static final class InterpolatedValueDeterminer extends ValueDeterminer {
		/**
		 * Used to interpolate the elapsed factor.
		 */
		private final ElapsedFactorInterpolator interpolator;
		public InterpolatedValueDeterminer(double startValue, double endValue, long startTime, long duration, ElapsedFactorInterpolator interpolator) {
			super(startValue, endValue, startTime, duration);
			this.interpolator = interpolator;
		}
		@Override
		public final double determineValue(long time) {
			if (time <= startTime) {
				return startValue;
			}
			final double elapsedFactor = (time - startTime) / duration;
			if (elapsedFactor >= 1) {
				return startValue + delta;
			}
			return startValue + delta * interpolator.interpolate(elapsedFactor);
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
	 * Returns the end value. In other words, returns the value {@link GliderEngine#getValue()} would return if an infinite
	 * amount of time would pass.
	 */
	public abstract double getEndValue();
	/**
	 * Returns the current value. The view passed to the constructor of the glider must use this method to obtain the current
	 * value in its onDraw implementation. This method should not be called from anywhere else, especially not from another
	 * thread.
	 *
	 * If the value is currently being glided, calling this method will invalidate said view at some point in the future.
	 */
	public abstract double getValue();
	/**
	 * Glides the value from the passed start value to the passed end value. The passed speed is the amount that is added to
	 * the start value or substracted from it every second to reach the end value.
	 *
	 * Calling this method ends any previously started glides.
	 *
	 * You should invalidate the view passed to the constructor after starting a new glide.
	 */
	public void glide(double startValue, double endValue, double speed) {
		glide(new LinearValueDeterminer(startValue, endValue, System.nanoTime(),
				determineDuration(startValue, endValue, speed)));
	}
	/**
	 * Glides the value from the passed start value to the passed end value, and does so in an interpolated fashion using the
	 * passed interpolator. The passed speed is the amount that would be added to the start value or substracted from it every
	 * second to reach the end value, if the glide were linear.
	 *
	 * Calling this method ends any previously started glides.
	 *
	 * You should invalidate the view passed to the constructor after starting a new glide.
	 *
	 * Because interpolators are most likely stateless, you should consider re-using the same one instead of creating a new one
	 * for every glide.
	 */
	public void glide(double startValue, double endValue, double averageSpeed, ElapsedFactorInterpolator interpolator) {
		glide(new InterpolatedValueDeterminer(startValue, endValue, System.nanoTime(),
				determineDuration(startValue, endValue, averageSpeed), interpolator));
	}
	/**
	 * Derivative classes should either implement this method, or leave this one blank implement the two public glide methods.
	 */
	protected abstract void glide(ValueDeterminer newValueDeterminer);
	/**
	 * Sets the value to the passed value, ending any previously started glides.
	 *
	 * You should invalidate the view passed to the constructor after ending a glide.
	 *
	 * No assumptions should be made about the number of times the view passed to the constructor is invalidated after this
	 * method is called: even an ended glide might still cause the view to be invalidated.
	 */
	public abstract void stop(double value);
}