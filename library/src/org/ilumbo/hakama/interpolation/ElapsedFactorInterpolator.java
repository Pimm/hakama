package org.ilumbo.hakama.interpolation;

/**
 * Calculates the interpolated fraction of a passed elapsed fraction. This allows glides to have non-linear motion.
 */
public interface ElapsedFactorInterpolator {
	/**
	 * Calculates and returns the interpolated elapsed fraction from the passed elapsed fraction. The passed elapsed fraction
	 * must be between 0 and 1 (both including), where 0 represents the start of a glide and 1 represents the end. The returned
	 * interpolated elapsed fraction will most likely be within the same range, but might also be a bit more than 1 (overshoot)
	 * or less than 0 (anticipation).
	 */
	public double interpolate(double input);
}