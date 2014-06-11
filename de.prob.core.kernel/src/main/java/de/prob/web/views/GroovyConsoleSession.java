package de.prob.web.views;

import java.util.Map;
import java.util.UUID;

import javax.script.ScriptEngine;
import javax.servlet.AsyncContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.annotations.Dangerous;
import de.prob.scripting.ScriptEngineProvider;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

/* Access to this view is restricted to localhost */
@Dangerous
public class GroovyConsoleSession extends AbstractSession {

	private final Logger logger = LoggerFactory
			.getLogger(GroovyConsoleSession.class);

	private final ScriptEngine engine;

	@Inject
	public GroovyConsoleSession(final UUID id, final ScriptEngineProvider sep) {
		super(id);
		engine = sep.get();
	}

	public Object exec(final Map<String, String[]> params) {

		logger.trace("Exec");
		String line = get(params, "line");
		try {
			StringBuffer console = new StringBuffer();
			engine.put("__console", console);
			logger.trace("Eval {} on {}", line, engine.toString());
			Object eval = engine.eval(line);
			String resultString = eval.toString();
			logger.trace("Evaled {} to {}", line, resultString);
			return WebUtils.wrap("cmd", "Console.groovyResult", "result",
					StringEscapeUtils.escapeHtml(resultString), "output",
					console.toString());
		} catch (Exception e) {
			return WebUtils.wrap("cmd", "Console.groovyError", "message",
					e.getMessage(), "trace", extractTrace(e.getStackTrace()));
		}

	}

	private String extractTrace(final StackTraceElement[] stackTrace) {
		if (stackTrace.length == 0) {
			return "";
		}
		if (stackTrace.length == 1) {
			return "at " + stackTrace[0].toString();
		}
		return "at " + stackTrace[0].toString()
				+ System.getProperty("line.separator") + "...";
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/console/index.html");
	}

	@Override
	public void reload(String client, int lastinfo, AsyncContext context) {
		sendInitMessage(context);
	}

}
