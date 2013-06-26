package de.prob.web.views;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.script.ScriptEngine;

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
		return Futures.immediateFuture((Object) "5");
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
