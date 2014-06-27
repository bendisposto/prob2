package de.prob.web;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class WebUtils {

	private static final Gson GSON = new Gson();

	public static Map<String, String> wrap(final Object... content) {
		if (content.length % 2 != 0) {
			throw new IllegalArgumentException(
					"wrap requires an even number of arguments");
		}
		HashMap<String, String> m = new HashMap<String, String>();
		for (int i = 0; i < content.length; i = i + 2) {
			m.put(content[i].toString(), content[i + 1].toString());
		}
		return m;
	}

	public static Map<String, String> wrap(final String... content) {
		if (content.length % 2 != 0) {
			throw new IllegalArgumentException(
					"wrap requires an even number of arguments");
		}
		HashMap<String, String> m = new HashMap<String, String>();
		for (int i = 0; i < content.length; i = i + 2) {
			m.put(content[i], content[i + 1]);
		}
		return m;
	}

	public static String toJson(final Object o) {
		return GSON.toJson(o);
	}

	public static String render(String string, Object... scope) {
		return "renderx";
	}

}
