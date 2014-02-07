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
 * Changes the fraction slowly at the start and at the end, and fast in between.
 *
 * Instances are stateless, hence can safely be re-used.
 */
public final class AccelerateDecelerateInterpolator implements ElapsedFactorInterpolator {
	@Override
	public final double interpolate(double input) {
		return Math.cos((input + 1) * Math.PI) / 2.0 + 0.5;
	}
}