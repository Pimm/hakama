package org.ilumbo.hakama;

import org.ilumbo.hakama.interpolation.ElapsedFactorInterpolator;

/**
 * A glider engine that snaps instead of glides. Sort of a null object implementation, if you will. It ignores the speeds
 * passed to the glide methods, so one could argue that the implementation is incorrect
 */
public final class SnapGliderEngine extends GliderEngine {
	/**
	 * The value that is returned.
	 */
	private double value;
	public SnapGliderEngine(double initialValue) {
		// There is no need to store the view (the invalidatee), as this implementation never invalidates.
		super(null);
		value = initialValue;
	}
	@Override
	public final double getEndValue() {
		return value;
	}
	@Override
	public final double getValue() {
		return value;
	}
	@Override
	public final void glide(double startValue, double endValue, double speed) {
		value = endValue;
	}
	@Override
	public final void glide(double startValue, double endValue, double averageSpeed, ElapsedFactorInterpolator interpolator) {
		value = endValue;
	}
	@Override
	protected final void glide(ValueDeterminer newValueDeterminer) {
		throw new UnsupportedOperationException();
	}
	@Override
	public final void stop(double value) {
		this.value = value;
	}
}