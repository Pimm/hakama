package org.ilumbo.hakama;

import org.ilumbo.hakama.choreographed.ChoreographedGliderEngine;
import org.ilumbo.hakama.freestyle.FreestyleGliderEngine;

import android.os.Build;
import android.view.View;

/**
 * Makes glides happen. Derivative classes should use the (protected) engine to initiate and stop glides, as well as to obtain
 * the current value whenever the view passed to the constructor in invalidated.
 */
public abstract class Glider {
	/**
	 * The engine this glider uses, which does all of the hard work.
	 */
	protected final GliderEngine engine;
	public Glider(View invalidatee, double initialValue) {
		// Construct an engine. Use the choreographed one if the Android version of the device is 16 (jelly bean) or higher, as
		// that engine has neater integration with the Android framework. Use the freestyle one if the choreographed one is not
		// available.
		engine = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ? new ChoreographedGliderEngine(invalidatee, initialValue) : new FreestyleGliderEngine(invalidatee, initialValue);
	}
}