package de.prob.util;

import java.util.HashMap;
import java.util.Map;

public class StringUtil {
	private static Map<String, String> stringCache = new HashMap<String, String>();

	public static String generateString(final String string) {
		if (!stringCache.containsKey(string)) {
			stringCache.put(string, string);
		}
		return stringCache.get(string);
	}
}
