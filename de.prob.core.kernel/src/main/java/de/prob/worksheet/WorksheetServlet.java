package de.prob.worksheet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.script.ScriptEngine;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class WorksheetServlet extends HttpServlet {

	private int session_count = 0;
	private long message_count = 0;
	private ScriptEngineProvider sep;

	private final Map<String, ScriptEngine> sessions = new HashMap<String, ScriptEngine>();
	private final Map<String, Queue<String>> queues = new HashMap<String, Queue<String>>();

	private final Gson g = new Gson();

	@Inject
	public WorksheetServlet(ScriptEngineProvider sep) {
		this.sep = sep;
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();

		String session = request.getParameter("session");
		String cmds = request.getParameter("cmd");

		if (session == null || session.equals("null") || session.isEmpty()) {
			int c = session_count++;
			session = String.valueOf(c);
		}
		Queue<String> q = getQueue(session);

		System.out.println(session + " " + cmds);

		Map<String, Object> resp = makeEmptyResponse(session);
		ECmd cmd = ECmd.valueOf(cmds);
		switch (cmd) {
		case init:
			resp.put("cmd", "set_top");
			resp.put("lang", "groovy");
			break;

		default:
			break;
		}

		String json = "";
		// Send Policy
		switch (cmd) {
		case session:
			json = g.toJson(makeEmptyResponse(session));
			break;
		case updates:
			if (!q.isEmpty())
				json = dequeue(q);
			break;

		default:
			enqueue(q, g.toJson(resp));
			break;
		}

		//
		// language = language == null ? "" : language; // I wish I could use
		// // groovy's elvis
		// // operator
		//
		// if ("update".equals(command)) {
		// editors.clear();
		// int count = Integer.valueOf(request.getParameter("count"));
		// for (int i = 0; i < count; i++) {
		// String id = request.getParameter("data[" + i + "][id]");
		// String lang = request.getParameter("data[" + i + "][lang]");
		// String text = request.getParameter("data[" + i + "][text]");
		// editors.put(id, new Editor(id, lang, text));
		// }
		// }
		// if ("eval".equals(command)) {
		// String text = request.getParameter("text");
		// text = text == null ? "" : text;
		//
		// ScriptEngine executor = getExecutor(session);
		//
		// if ("groovy".equals(language) && !text.isEmpty()) {
		// Object result = "";
		// try {
		// result = executor.eval(text);
		// resp.put("result", result.toString());
		// } catch (ScriptException e) {
		// resp.put("error", true);
		// resp.put("result", "Exception while processing '" + text
		// + "'. " + e.getMessage());
		// e.printStackTrace();
		// }
		// }
		//
		// if ("b".equals(language) && !text.isEmpty()) {
		// Object result = "";
		//
		// try {
		// result = executor
		// .eval("animations.getCurrentHistory().getCurrentState().eval(\""
		// + text + "\")");
		// resp.put("result", "<p>" + text + " is </p><p> " + result
		// + "</p>");
		// } catch (ScriptException e) {
		// resp.put("error", true);
		// resp.put("result", "Exception while processing '" + text
		// + "'. " + e.getMessage());
		// e.printStackTrace();
		// }
		// }
		//
		// System.out.println("used " + executor);
		//
		// if (!resp.containsKey("result")) {
		// resp.put("result", "empty result");
		// }
		// }

		if (!json.isEmpty())
			out.println(json);
		out.close();

	}

	private Map<String, Object> makeEmptyResponse(String session) {
		Map<String, Object> resp;
		resp = new HashMap<String, Object>();
		resp.put("session", session);
		resp.put("id", message_count++);
		return resp;
	}

	private Queue<String> getQueue(String session) {
		Queue<String> queue = queues.get(session);
		if (queue == null) {
			queue = new LinkedBlockingQueue<String>();
			queues.put(session, queue);
		}
		return queue;
	}

	private String dequeue(Queue<String> q) {
		if (q.isEmpty())
			return null;
		return q.poll();
	}

	private void enqueue(Queue<String> q, String json) {
		q.add(json);
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
