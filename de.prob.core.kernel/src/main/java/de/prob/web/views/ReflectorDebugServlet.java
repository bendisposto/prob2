package de.prob.web.views;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.UUID;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import de.prob.web.AbstractSession;

public class ReflectorDebugServlet extends AbstractSession {

	@Inject
	public ReflectorDebugServlet(UUID id) {
		super(id);
	}

	static class Result {

		public final String cmd;

		public Result(String string) {
			this.cmd = string;
		}

	}

	public Result ok(Map<String, String[]> p) {
		return new Result("Yiha!");
	}

	@Override
	public String html(Map<String, String[]> parameterMap) {
		String uuid = getUuid().toString();
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile("ui/templates/debug.html");
		StringWriter sw = new StringWriter();
		try {
			mustache.execute(sw, ImmutableMap.of("uuid", uuid)).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}

}
