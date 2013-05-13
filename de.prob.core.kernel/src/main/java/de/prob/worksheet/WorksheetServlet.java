package de.prob.worksheet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.script.ScriptEngine;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Joiner;
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
	private final List<Editor> editors = new ArrayList<Editor>();

	private final Gson g = new Gson();

	@Inject
	public WorksheetServlet(ScriptEngineProvider sep) {
		this.sep = sep;
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();

		Set<Entry<String, String[]>> entrySet = request.getParameterMap()
				.entrySet();
		for (Entry<String, String[]> entry : entrySet) {
			String k = entry.getKey();
			String v = Joiner.on(",").join(entry.getValue());
			System.out.print(k + "=>" + v);
			System.out.print(" ");
		}

		System.out.println();

		String session = request.getParameter("session");
		String cmds = request.getParameter("cmd");

		if (session == null || session.equals("null") || session.isEmpty()) {
			int c = session_count++;
			session = String.valueOf(c);
		}
		Queue<String> q = getQueue(session);

		ECmd cmd = ECmd.valueOf(cmds);
		switch (cmd) {
		case init:
			enqueue(q,
					makeJsonResponse(session, "cmd", "set_top", "lang",
							"groovy"));
			editors.add(0, new Editor("0", "groovy", ""));
			enqueue(q,
					makeJsonResponse(session, "cmd", "append_box", "id", "0",
							"lang", "groovy", "content", ""));
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

		case leave:
			String box = request.getParameter("box");
			String direction = request.getParameter("direction");
			int id = Integer.parseInt(box);

			break;
		default:
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

	public String makeJsonResponse(String session, String... args) {
		if (args.length % 2 != 0)
			throw new IllegalArgumentException(
					"Require an even number of key/values");

		Map<String, Object> response = makeEmptyResponse(session);
		for (int i = 0; i < args.length; i = i + 2) {
			response.put(args[i], args[i + 1]);
		}

		String json = g.toJson(response);
		return json;
	}

	private Map<String, Object> makeEmptyResponse(String session) {
		Map<String, Object> resp;
		resp = new HashMap<String, Object>();
		resp.put("id", message_count++);
		resp.put("session", session);
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
