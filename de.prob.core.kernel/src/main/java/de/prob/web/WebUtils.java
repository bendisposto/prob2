package de.prob.web;

import java.io.IOException;
import java.io.StringWriter;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class WebUtils {

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

}
