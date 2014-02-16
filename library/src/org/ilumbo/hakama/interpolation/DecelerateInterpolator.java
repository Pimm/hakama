package org.ilumbo.hakama.interpolation;

/**
 * Changes the fraction fast at the start and slowly at the end.
 *
 * Instances are stateless, hence can safely be re-used.
 */
public final class DecelerateInterpolator implements ElapsedFactorInterpolator {
	@Override
	public final double interpolate(double input) {
		return Math.sin(input * Math.PI / 2);
	}
}