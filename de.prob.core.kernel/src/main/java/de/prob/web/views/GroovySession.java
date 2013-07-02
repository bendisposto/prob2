package de.prob.web.views;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

import de.prob.web.AbstractSession;
import de.prob.worksheet.ScriptEngineProvider;

public class GroovySession extends AbstractSession {

	private final ScriptEngine engine;

	@Inject
	public GroovySession(ScriptEngineProvider sep) {
		this.engine = sep.get();
	}

	@Override
	public ListenableFuture<Object> requestJson(
			Map<String, String[]> parameterMap) {

		String cmd = parameterMap.get("cmd")[0];
		String line = parameterMap.get("line")[0];

		if ("exec".equals(cmd)) {
			try {
				Object result = engine.eval(line);
				// {"cmd":"groovyResult", "content": result.toString()}
				return Futures.immediateFuture((Object) ImmutableMap.of("cmd",
						"groovyResult", "content", result.toString()));
			} catch (ScriptException e) {
				return Futures.immediateFuture((Object) ImmutableMap.of("cmd",
						"groovyError", "content", e.getMessage()));
			}
		}
		return Futures.immediateFuture((Object) ImmutableMap.of("cmd",
				"groovyError", "content", "Unknown Operation"));

	}

	@Override
	public String requestHtml(Map<String, String[]> parameterMap) {
		String uuid = getUuid().toString();
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile("ui/templates/console.html");
		StringWriter sw = new StringWriter();
		try {
			mustache.execute(sw, ImmutableMap.of("uuid", uuid)).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}
}
