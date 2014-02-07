/*
 * Copyright (C) 2007 The Android Open Source Project
 * Copyright 2014 Pimm Hogeling
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ilumbo.hakama.interpolation;

/**
 * Calculates the interpolated fraction of a passed elapsed fraction. This allows glides to have non-linear motion.
 */
public interface ElapsedFactorInterpolator {
	/**
	 * Calculates and returns the interpolated elapsed fraction from the passed elapsed fraction. The passed elapsed fraction
	 * must be between 0 and 1 (both including), where 0 represents the start of a glide and 1 represents the end. The returned
	 * interpolated elapsed fraction will most likely be within the same range, but might also be a bit more than 1 (overshoot)
	 * or less than 0 (anticipation).
	 */
	public double interpolate(double input);
}
