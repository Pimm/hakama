package org.ilumbo.hakama;

import org.ilumbo.hakama.choreographed.ChoreographedGliderEngine;
import org.ilumbo.hakama.freestyle.FreestyleGliderEngine;

import android.os.Build;
import android.view.View;

/**
 * Makes glides happen. Derivative classes should use the (protected) engine to initiate and stop glides, as well as to obtain
 * the current value whenever the view passed to the constructor is drawn.
 *
 * Under the hood, gliding works like this:
 * 1) A glide is initiated by calling a method of the glider, and the view that presents the glide is invalidated.
 * 2) The onDraw implementation of the view interacts with the glider, and draws the current state of the glide.
 * 3) During the interaction from step 2, the glider checks whether the glide is completed. If not, it invalidates the view
 *    that presents the glide. Because said view is invalidated, step 2 will happen once again.
 */
public abstract class Glider {
	/**
	 * On Android versions without a choreographer, use a freestyle engine. This behaviour is the default, and causes the
	 * glider to behave similarly across all devices.
	 */
	protected static final boolean LEGACY_BEHAVIOR_FREESTYLE = true;
	/**
	 * On Android versions without a choreographer, use a snapping engine. This is an "all or nothing" behaviour: if no engine
	 * is available that works neatly with the Android framework, snap.
	 */
	protected static final boolean LEGACY_BEHAVIOR_SNAP = false;
	/**
	 * The engine this glider uses, which does all of the hard work.
	 */
	protected final GliderEngine engine;
	protected Glider(View invalidatee, double initialValue) {
		this(invalidatee, initialValue, LEGACY_BEHAVIOR_FREESTYLE);
	}
	protected Glider(View invalidatee, double initialValue, boolean legacyBehavior) {
		// Check whether the invalidatee is null right now, because if it actually is null a NullPointerException will not be
		// thrown until the engine tries to invalidate. That might be somewhere completely different from where the glider was
		// constructed. If so, it is not obvious that passing the null to the constructor was the cause.
		if (null == invalidatee) {
			throw new IllegalArgumentException("The invalidatee must be non-null");
		}
		// Construct an engine. Use the choreographed one if the Android version of the device is 16 (jelly bean) or higher, as
		// that engine has neat integration with the Android framework.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			engine = new ChoreographedGliderEngine(invalidatee, initialValue);
		// If the choreographed engine is not available, use an engine based on the passed legacy behaviour.
		} else if (LEGACY_BEHAVIOR_FREESTYLE == legacyBehavior) {
			engine = new FreestyleGliderEngine(invalidatee, initialValue);
		} else /* if (LEGACY_BEHAVIOR_SNAP == legacyBehavior) */ {
			engine = new SnapGliderEngine(initialValue);
		}
	}
}