package de.prob.web.views;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;
import de.prob.worksheet.ScriptEngineProvider;

public class GroovySession extends AbstractSession {

	private final ScriptEngine engine;

	@Inject
	public GroovySession(ScriptEngineProvider sep) {
		this.engine = sep.get();
	}

	public Map<String, String> exec(Map<String, String[]> p) {
		String line = get(p, "line");
		try {
			Object eval = engine.eval(line);
			return ImmutableMap.of("cmd", "groovyResult", "result",
					eval.toString());
		} catch (ScriptException e) {
			return ImmutableMap.of("cmd", "groovyError", "message",
					e.getMessage());
		}
	}

	@Override
	public String requestHtml(Map<String, String[]> parameterMap) {
		String uuid = getUuid().toString();
		ImmutableMap<String, String> scope = ImmutableMap.of("uuid", uuid);
		String name = "ui/templates/console.html";
		return WebUtils.render(name, scope);
	}
}
