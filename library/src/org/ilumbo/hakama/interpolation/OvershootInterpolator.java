package org.ilumbo.hakama.interpolation;


/**
 * Changes the fraction fast at the start, overshoots the 1 to finally restore back to 1 slowly.
 *
 * Instances hold no state other than the tension.
 */
public class OvershootInterpolator implements ElapsedFactorInterpolator {
	/**
	 * The default tension.
	 */
	public static final double DEFAULT_TENSION = 2;
	/**
	 * The tension, most likely a positive number.
	 */
	private final double tension;
	public OvershootInterpolator() {
		tension = DEFAULT_TENSION;
	}
	public OvershootInterpolator(double tension) {
		this.tension = tension;
	}
	public static final class Static {
		/**
		 * Calculates and returns the interpolated elapsed fraction from the passed elapsed fraction. The passed elapsed
		 * fraction must be between 0 and 1 (both including), where 0 represents the start of a glide and 1 represents the end.
		 * Providing that the tension is non-negative, the returned interpolated elapsed fraction is greater than or equal to 0
		 * and in typical cases not much greater than 1.
		 */
		public static final double interpolate(double input, double tension) {
			return (input - 1) * (input - 1) * (tension * input + input - 1) + 1;
		}
	}
	@Override
	public final double interpolate(double input) {
		// This equation was taken from Android's overshoot interpolator. The source code of that source gives a hint of how
		// the equation was designed:
		//
		//		_o(input) = input * input * ((tension + 1) * input + tension)
        //		o(input) = _o(input - 1) + 1
		//			↓
		//		o(input) = (input - 1) * (input - 1) * ((tension + 1) * (input - 1) + tension) + 1
		//			↓
		return (input - 1) * (input - 1) * (tension * input + input - 1) + 1;
	}
}