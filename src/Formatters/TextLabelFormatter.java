package Formatters;

public class TextLabelFormatter {

	/**
	 * Returns a string with constant length, containing text appended with
	 * spaces.
	 * 
	 * @param text
	 * @return
	 */
	public static String getFixedString(String text, int minLength) {
		StringBuilder sb = new StringBuilder();
		int numSpaces = minLength - text.length();
		if (numSpaces <= 0)
			return text;

		// Append spaces before the text
		for (int i = 0; i < numSpaces / 2; i++) {
			sb.append(" ");
		}

		sb.append(text);

		// Append spaces after the text
		while (sb.length() < minLength) {
			sb.append(" ");
		}

		return sb.toString();
	}

}
