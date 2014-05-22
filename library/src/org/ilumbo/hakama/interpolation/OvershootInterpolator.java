package org.ilumbo.hakama.interpolation;



/**
 * Changes the fraction fast at the start, overshoots the 1 to finally restore back to 1 slowly.
 *
 * Instances hold no state other than the tension.
 *
 *                             ooooooooooooooooo                   
 *                        ooooo                 oooooooooo         
 * ····················ooo································ooooooooo
 *                  ooo                                            
 *                oo                                               
 *              oo                                                 
 *             o                                                   
 *           oo                                                    
 *          o                                                      
 *         o                                                       
 *       oo                                                        
 *      o                                                          
 *     o                                                           
 *    o                                                            
 *   o                                                             
 *  o                                                              
 * o                                                               
 * ································································
 *                                                                 
 *                                                                 
 */
public class OvershootInterpolator implements ElapsedFactorInterpolator {
	/**
	 * The default tension. Makes the interpolator exceed the 1 by ±.132, or 13%.
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
	/**
	 * Calculates and returns the tension for the overshoot interpolator that would cause it to exceed the 1 by the passed
	 * amount.
	 */
	public static final double calculateTension(double overshoot) {
		// In case 0 is passed, return 0, which is accurate. A tension of 0 gives the equation no overshoot:
		// (x - 1) * (x - 1) * (x - 1) + 1. In case a negative number greater than -1 is passed, the code below will actually
		// find a negative tension which causes the equation to get the passed "undershoot" for an extreme. That would be
		// somewhat of an accurate result, but not helpful considering the use case of this method. Return 0 for undershooting.
		if (overshoot <= 0) {
			return 0;
		}
		// Determine what the output should be at the top of the equation.
		final double output = 1 + overshoot;
		// For the top of the equation, the following is true:
		//		0 = Δoutput / Δinput
		//			↓
		//		0 = -3 - tension + 3 * input + 3 * tension * input
		//			↓
		//		input = (3 + tension) / (3 + 3 * tension)
		//			↓
		//		output = ((3 + tension) * (3 + tension) * (3 + 4 * tension)) / (27 * (1 + tension) * (1 + tension))
		//			↓
		//		tension = .75 * part - (-729 * output * output + 810 * output - 81) / (108 * part) + .25 * (9 * (output - 1));
		//		where part = Math.pow(27 * output * output * output - 45 * output * output + 8 *
		//			Math.sqrt(output * output * output - 2 * output * output + output) + 17 * output + 1, 1d / 3)
		final double outputSquared = output * output;
		final double outputCubed = outputSquared * output;
		final double part = Math.pow(27 * outputCubed - 45 * outputSquared + 8 *
				Math.sqrt(outputCubed - 2 * outputSquared + output) + 17 * output + 1, 1d / 3);
		return .75 * part - (-729 * outputSquared + 810 * output - 81) / (108 * part) + 2.25 * overshoot;
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