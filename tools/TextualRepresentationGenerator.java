package org.ilumbo.hakama.documentation;

import org.ilumbo.hakama.interpolation.ElapsedFactorInterpolator;

/**
 * Generates a textual representation of an elapsed factor interpolator.
 */
public final class TextualRepresentationGenerator {
	public static final String generateTextualRepresentation(ElapsedFactorInterpolator elapsedFactorInterpolator,
			int columnCount, int regularRowCount, int margin) {
		final int rowCount = regularRowCount + margin << 1;
		// Create the result builder.
		final StringBuilder resultBuilder = new StringBuilder((columnCount + 1) * rowCount);
		// Fill the result builder with "empty" characters and line breaks.
		final String spaces;
		final String dots;
		{
			final StringBuilder emptyCharacterStringBuilder = new StringBuilder(columnCount);
			while (emptyCharacterStringBuilder.length() != columnCount) {
				emptyCharacterStringBuilder.append(' ');
			}
			spaces = emptyCharacterStringBuilder.toString();
			emptyCharacterStringBuilder.setLength(0);
			while (emptyCharacterStringBuilder.length() != columnCount) {
				emptyCharacterStringBuilder.append('·');
			}
			dots = emptyCharacterStringBuilder.toString();
		}
		int pledgedSpacesRowCount = margin;
		while (0 != pledgedSpacesRowCount--) {
			resultBuilder.append(spaces)
					.append('\n');
		}
		resultBuilder.append(dots)
				.append('\n');
		// Note: infinite loop on absurdly low row counts.
		pledgedSpacesRowCount = regularRowCount - 2;
		while (0 != pledgedSpacesRowCount--) {
			resultBuilder.append(spaces)
					.append('\n');
		}
		resultBuilder.append(dots)
				.append('\n');
		pledgedSpacesRowCount = margin;
		while (0 != pledgedSpacesRowCount--) {
			resultBuilder.append(spaces);
			if (0 != pledgedSpacesRowCount) {
				resultBuilder.append('\n');
			}
		}
		// Use the interpolator, replacing "empty" characters.
		for (int x = 0; columnCount != x; x++) {
			final int y = margin + regularRowCount - 1 -
					(int) (Math.round(elapsedFactorInterpolator.interpolate((x + .5) / columnCount) * (regularRowCount - 1)));
			if (y >= 0 && y < rowCount) {
				resultBuilder.setCharAt(y * (columnCount + 1) + x, 'o');
			}
		}
		return resultBuilder.toString();
	}
}