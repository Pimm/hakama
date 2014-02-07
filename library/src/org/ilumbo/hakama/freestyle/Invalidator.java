package org.ilumbo.hakama.freestyle;

import android.os.SystemClock;
import android.view.View;

/**
 * A threat that invalidates a view every so often.
 */
public final class Invalidator extends Thread {
	/**
	 * The time, in milliseconds, the invalidator will sleep between two job checks.
	 */
	private static final long CHECK_SLEEP_TIME = 1;
	/**
	 * An invalidator with this job will invalidate the target view after sleeping for a while.
	 */
	public static final byte JOB_INVALIDATE = 0;
	/**
	 * An invalidator with this job will wait for the job to change into something else.
	 */
	private static final byte JOB_WAIT = 1;
	/**
	 * An invalidator with this job will stop at some point in the future, potentially after invalidating the target view
	 * one more time.
	 */
	public static final byte JOB_STOP = 2;
	/**
	 * The time, in milliseconds, the invalidator will wait before invalidating the target view. This makes the view
	 * invalidate 71 times in a second in optimal situations, making the FPS 71.
	 */
	private static final long INVALIDATE_SLEEP_TIME = 14;
	/**
	 * The job the invalidator currently has.
	 */
	private byte job;
	/**
	 * Whether the invalidator has been started already.
	 */
	public boolean started;
	/**
	 * The view that is invalidated.
	 */
	private final View target;
	public Invalidator(View target) {
		// The thread name is "Invalidator", unless this thread is obfuscated, in which case the thread name is as well.
		super(Invalidator.class.getSimpleName());
		this.target = target;
		job = JOB_INVALIDATE;
		started = false;
	}
	/**
	 * Sets the job of the invalidator.
	 */
	public synchronized final void setJob(byte job) {
		this.job = job;
	}
	@Override
	public synchronized final void start() {
		started = true;
		super.start();
	}
	@Override
	public final void run() {
		while (true) {
			// Invalidate.
			SystemClock.sleep(INVALIDATE_SLEEP_TIME);
			target.postInvalidate();
			// Set the job to wait, if it was still set to invalidate. If the job has been set to stop, leave it at stop.
			synchronized (this) {
				if (JOB_INVALIDATE == job) {
					job = JOB_WAIT;
				}
			}
			// Wait until the job changes to something else.
			while (true) {
				SystemClock.sleep(CHECK_SLEEP_TIME);
				if (JOB_WAIT != job) {
					break;
				}
			}
			// If the job has been set to stop, at any point, break and stop this thread.
			if (JOB_STOP == job) {
				break;
			}
		}
	}
}