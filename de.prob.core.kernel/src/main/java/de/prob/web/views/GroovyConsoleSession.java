package de.prob.web.views;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.UUID;

import javax.script.ScriptEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.web.AbstractSession;
import de.prob.web.SessionQueue;
import de.prob.web.WebUtils;
import de.prob.worksheet.ScriptEngineProvider;

public class GroovyConsoleSession extends AbstractSession {

	private final Logger logger = LoggerFactory
			.getLogger(GroovyConsoleSession.class);

	private final ScriptEngine engine;

	@Inject
	public GroovyConsoleSession(UUID id, ScriptEngineProvider sep,
			SessionQueue q) {
		super(id, q);
		engine = sep.get();
	}

	public Object exec(Map<String, String[]> params) {
		logger.trace("Exec");
		String line = get(params, "line");
		try {
			StringBuffer console = new StringBuffer();
			engine.put("__console", console);
			logger.trace("Eval " + engine.toString());
			Object eval = engine.eval(line);
			logger.trace("Evaled " + engine.toString());
			return WebUtils.wrap("cmd", "Console.groovyResult", "result",
					eval.toString(), "output", console.toString());
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			// def trace = e.getStackTrace().collect {it.toString() };
			return WebUtils.wrap("cmd", "Console.groovyError", "message",
					e.getMessage(), "trace", sw.toString());
		}
	}

	@Override
	public String html(Map<String, String[]> parameterMap) {
		String uuid = getUuid().toString();
		String template = "ui/console/index.html";
		Object scope = WebUtils.wrap("uuid", uuid);
		return WebUtils.render(template, scope);
	}

}
