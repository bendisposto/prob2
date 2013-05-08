package de.prob.worksheet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class WorksheetServlet extends HttpServlet {

	private static int sessioncount = 0;
	private ScriptEngineProvider sep;

	private static Map<String, Editor> editors = new HashMap<String, Editor>();

	private Map<String, ScriptEngine> sessions = new HashMap<String, ScriptEngine>();

	@Inject
	public WorksheetServlet(ScriptEngineProvider sep) {
		this.sep = sep;
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Gson g = new Gson();
		PrintWriter out = response.getWriter();
		Map<String, Object> resp = new HashMap<String, Object>();

		String language = request.getParameter("lang");
		String command = request.getParameter("command");
		String session = request.getParameter("session");

		if (session == null || session.equals("null")) {
			int c = sessioncount++;
			resp.put("session", c);
			session = String.valueOf(c);
		}

		language = language == null ? "" : language; // I wish I could use
														// groovy's elvis
														// operator

		if ("update".equals(command)) {
			editors.clear();
			int count = Integer.valueOf(request.getParameter("count"));
			for (int i = 0; i < count; i++) {
				String id = request.getParameter("data[" + i + "][id]");
				String lang = request.getParameter("data[" + i + "][lang]");
				String text = request.getParameter("data[" + i + "][text]");
				editors.put(id, new Editor(id, lang, text));
			}
		}
		if ("eval".equals(command)) {
			String text = request.getParameter("text");
			text = text == null ? "" : text;

			ScriptEngine executor = getExecutor(session);

			if ("groovy".equals(language) && !text.isEmpty()) {
				Object result = "";
				try {
					result = executor.eval(text);
					resp.put("result", result.toString());
				} catch (ScriptException e) {
					resp.put("error", true);
					resp.put("result", "Exception while processing '" + text
							+ "'. " + e.getMessage());
					e.printStackTrace();
				}
			}

			if ("b".equals(language) && !text.isEmpty()) {
				Object result = "";

				try {
					result = executor
							.eval("animations.getCurrentHistory().getCurrentState().eval(\""
									+ text + "\")");
					resp.put("result", "<p>" + text + " is </p><p> " + result
							+ "</p>");
				} catch (ScriptException e) {
					resp.put("error", true);
					resp.put("result", "Exception while processing '" + text
							+ "'. " + e.getMessage());
					e.printStackTrace();
				}
			}

			System.out.println("used " + executor);

			if (!resp.containsKey("result")) {
				resp.put("result", "empty result");
			}
		}

		String json = g.toJson(resp);
		out.println(json);
		out.close();

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
