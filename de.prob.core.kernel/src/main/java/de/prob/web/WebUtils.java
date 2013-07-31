package de.prob.web;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;

public class WebUtils {

	private static final Gson GSON = new Gson();

	public static String render(String name, Object... scopes) {
		MustacheFactory mf = new DefaultMustacheFactory();

		Mustache mustache = mf.compile(name);
		StringWriter sw = new StringWriter();
		try {
			mustache.execute(sw, scopes).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}

	public static Map<String, String> wrap(String... content) {
		if (content.length % 2 != 0)
			throw new IllegalArgumentException(
					"wrap requires an even number of arguments");
		HashMap<String, String> m = new HashMap<String, String>();
		for (int i = 0; i < content.length; i = i + 2) {
			m.put(content[i], content[i + 1]);
		}
		return m;
	}

	public static String toJson(Object o) {
		return GSON.toJson(o);
	}

}
