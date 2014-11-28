package org.ilumbo.hakama.interpolation;

/**
 * Changes the fraction slowly at the start and fast at the end.
 *
 * Instances are stateless, hence can safely be re-used.
 *
 *                                                                 
 *                                                                 
 * ·······························································o
 *                                                             ooo 
 *                                                          ooo    
 *                                                       ooo       
 *                                                     oo          
 *                                                  ooo            
 *                                               ooo               
 *                                            ooo                  
 *                                        oooo                     
 *                                     ooo                         
 *                                 oooo                            
 *                             oooo                                
 *                         oooo                                    
 *                   oooooo                                        
 *            ooooooo                                              
 * ooooooooooo·····················································
 *                                                                 
 *                                                                 
 */
public final class AccelerateInterpolator implements ElapsedFactorInterpolator {
	public static final class Static {
		/**
		 * Calculates and returns the input for which {@link #interpolate(double)} would return the passed output.
		 */
		public static final double deinterpolate(double output) {
			return (2 * Math.acos(1 - output)) / Math.PI;
		}
		/**
		 * Calculates and returns the interpolated elapsed fraction from the passed elapsed fraction. The passed elapsed
		 * fraction must be between 0 and 1 (both including), where 0 represents the start of a glide and 1 represents the end.
		 * The returned interpolated elapsed fraction is within the same range.
		 */
		public static final double interpolate(double input) {
			return 1 - Math.cos((input * Math.PI) / 2);
		}
	}
	/**
	 * Calculates and returns the input for which {@link #interpolate(double)} would return the passed output.
	 */
	public static final double deinterpolate(double output) {
		return (2 * Math.acos(1 - output)) / Math.PI;
	}
	@Override
	public final double interpolate(double input) {
		return 1 - Math.cos((input * Math.PI) / 2);
	}
}