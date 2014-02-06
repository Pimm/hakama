package org.ilumbo.hakama;

import android.graphics.Paint;
import android.view.View;

/**
 * Glides the alpha channel of a paint, and encapsulates that paint as well.
 */
public final class AlphaPaintGlider extends Glider {
	/**
	 * The paint whose alpha is changed.
	 */
	private final Paint target;
	public AlphaPaintGlider(Paint target, View invalidatee) {
		super(invalidatee, target.getAlpha());
		this.target = target;
	}
	/**
	 * Glides the alpha of the paint passed to the constructor from its current value to the passed value. The passed speed is
	 * the amount that would be added to or substracted from the initial alpha every second if the glide were linear. If a
	 * speed of 1000 is passed, the alpha could fade from 0 (fully transparent) to 255 (fully opaque) in 255 milliseconds.
	 */
	public synchronized final void glide(int endAlpha, double averageSpeed) {
		engine.glide(target.getAlpha(), endAlpha, averageSpeed, false);
	}
	/**
	 * Glides the alpha of the paint passed to the constructor from the one pased value to the other. The passed speed is the
	 * amount that would be added to or substracted from the initial alpha every second if the glide were linear. If a speed of
	 * 1000 is passed, the alpha could fade from 0 (fully transparent) to 255 (fully opaque) in 255 milliseconds.
	 */
	public synchronized final void glide(int startAlpha, int endAlpha, double averageSpeed) {
		engine.glide(startAlpha, endAlpha, averageSpeed, true);
	}
	/**
	 * Returns the paint whose alpha this glider changes. The alpha of the returned paint is updated.
	 */
	public synchronized final Paint getPaint() {
		target.setAlpha((int) Math.round(engine.getValue()));
		return target;
	}
}