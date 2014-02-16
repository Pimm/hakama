package org.ilumbo.hakama.interpolation;

/**
 * Changes the fraction slowly at the start and fast at the end.
 *
 * Instances are stateless, hence can safely be re-used.
 */
public final class AccelerateInterpolator implements ElapsedFactorInterpolator {
	@Override
	public final double interpolate(double input) {
		return 1 - Math.cos((input * Math.PI) / 2);
	}
}