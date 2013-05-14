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

	private int counter = 0;

	private ScriptEngineProvider sep;

	private final Map<String, ScriptEngine> sessions = new HashMap<String, ScriptEngine>();
	private final Map<String, Queue<String>> queues = new HashMap<String, Queue<String>>();
	private final Map<String, Editor> editors = new HashMap<String, Editor>();

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
			int c = nextId();
			session = String.valueOf(c);
		}
		Queue<String> q = getQueue(session);

		ECmd cmd = ECmd.valueOf(cmds);
		String box = request.getParameter("box");
		switch (cmd) {
		case init:
			enqueue(q,
					toJson(session, "cmd", "set_top", "lang", defaultLanguage()));
			appendNewBox(session, q);
			break;
		case delete:
			editors.remove(box);
			enqueue(q, toJson(session, "cmd", "delete", "id", box));
			if (editors.isEmpty()) {
				appendNewBox(session, q);
			}

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
			if (!q.isEmpty()) {
				ArrayList<String> list = new ArrayList<String>();
				do {
					String s = dequeue(q);
					list.add(s);
				} while (!q.isEmpty());
				json = g.toJson(list);
			}
			break;

		case leave:
			String content = request.getParameter("text");
			String direction = request.getParameter("direction");
			int id = Integer.parseInt(box);
			unfocus(box, content);
			enqueue(q, toJson(session, "cmd", "unfocus", "id", box));

			if ("up".equals(direction)) {
				if (id > 0) {
					String newId = String.valueOf(id - 1);
					enqueue(q, toJson(session, "cmd", "activate", "id", newId));
				}
			}
			if ("down".equals(direction)) {
				if (id < editors.size() - 1) {
					String newId = String.valueOf(id + 1);
					enqueue(q, toJson(session, "cmd", "activate", "id", newId));
				} else {
					appendNewBox(session, q);
				}
			}

			break;
		default:
			break;
		}

		if (!json.isEmpty())
			out.println(json);
		out.close();
	}

	private void unfocus(String box, String content) {
		Editor editor = editors.get(box);
		editor.setText(content);

		System.out.println("Unfocused editor");
		System.out.println("Content is: " + editor.getText());
		System.out.println("Language is: " + editor.lang);
	}

	private int nextId() {
		return counter++;
	}

	private void appendNewBox(String session, Queue<String> q) {
		String id = String.valueOf(nextId());
		editors.put(id, new Editor("0", defaultLanguage(), ""));
		enqueue(q,
				toJson(session, "cmd", "append_box", "id", id, "lang",
						defaultLanguage(), "content", ""));
	}

	private String defaultLanguage() {
		return "groovy";
	}

	public String toJson(String session, String... args) {
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
		resp.put("id", nextId());
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
