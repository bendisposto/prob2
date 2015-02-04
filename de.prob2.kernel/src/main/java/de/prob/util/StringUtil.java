package de.prob.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class StringUtil {

	private static Map<String, String> stringCache = new HashMap<String, String>();
	private static Gson g = new Gson();

	public static String generateString(final String string) {
		if (!stringCache.containsKey(string)) {
			stringCache.put(string, string);
		}
		return stringCache.get(string);
	}

	public static String generateJsonString(final Object obj) {
		return g.toJson(obj);
	}

}
