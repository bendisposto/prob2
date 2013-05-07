package de.prob.worksheet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.statespace.AnimationSelector;
import de.prob.webconsole.GroovyExecution;

@SuppressWarnings("serial")
@Singleton
public class WorksheetServlet extends HttpServlet {

	private static int sessioncount = 0;
	private ScriptEngineProvider sep;

	private Map<String, ScriptEngine> sessions = new HashMap<String, ScriptEngine>();

	@Inject
	public WorksheetServlet(ScriptEngineProvider sep) {
		this.sep = sep;
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		Map<String, Object> resp = new HashMap<String, Object>();

		String language = request.getParameter("lang");
		String command = request.getParameter("input");
		String session = request.getParameter("session");

		if (session == null || session.equals("null")) {
			int c = sessioncount++;
			resp.put("session", c);
			session = String.valueOf(c);
		}
		ScriptEngine executor = getExecutor(session);

		if ("groovy".equals(language)) {
			Object result = "";
			try {
				result = executor.eval(command);
				resp.put("result", result.toString());
			} catch (ScriptException e) {
				resp.put("error", true);
				resp.put("result", "Exception while processing '" + command
						+ "'. " + e.getMessage());
				e.printStackTrace();
			}
		}

		if ("b".equals(language)) {
			Object result = "";

			try {
				result = executor
						.eval("animations.getCurrentHistory().getCurrentState().eval("
								+ command + ")");
				resp.put("result", "<p>" + command + " is </p><p> " + result
						+ "</p>");
			} catch (ScriptException e) {
				resp.put("error", true);
				resp.put("result", "Exception while processing '" + command
						+ "'. " + e.getMessage());
				e.printStackTrace();
			}

		}

		System.out.println("used " + executor);

		if (resp.isEmpty()) {
			resp.put("result", "empty result");
		}

		Gson g = new Gson();

		String json = g.toJson(resp);
		out.println(json);
		out.close();
		;
	}

	private ScriptEngine getExecutor(String session) {
		ScriptEngine scriptEngine = sessions.get(session);
		if (scriptEngine == null) {
			scriptEngine = sep.get();
			sessions.put(session, scriptEngine);
		}
		return scriptEngine;
	}
}
