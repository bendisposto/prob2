package de.prob.worksheet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pegdown.PegDownProcessor;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.inject.Inject;

public class WorkSheet {

	private static final String DIR_FROM_ABOVE = "from_above";
	private static final String DIR_FROM_BELOW = "from_below";
	private static final String DIR_FROM_SOMEWHERE_ELSE = "from_somewhere_else";
	private String session;
	private final Queue<String> q = new LinkedBlockingQueue<String>();
	private final ArrayList<Editor> editors = new ArrayList<Editor>();
	private int active = -1;
	private int message_counter = 0;
	private final Gson g = new Gson();
	private ScriptEngine groovy;
	private String defaultlang = "groovy";
	private PegDownProcessor pegdown;

	@Inject
	public WorkSheet(ScriptEngine groovy, PegDownProcessor pegdown) {
		this.groovy = groovy;
		this.pegdown = pegdown;
	}

	public void doGet(String session, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.session = session;
		PrintWriter out = response.getWriter();

		printparams(request);

		String cmds = request.getParameter("cmd");

		ECmd cmd = ECmd.valueOf(cmds);
		String box = request.getParameter("box");
		switch (cmd) {
		case init:
			enqueue("cmd", "set_top", "lang", defaultLanguage());
			appendNewBox(session, q, editors);
			break;
		case delete:
			editors.remove(box);
			enqueue("cmd", "delete", "id", box);
			if (editors.isEmpty()) {
				appendNewBox(session, q, editors);
			}

		default:
			break;
		}

		String json = "";
		// Send Policy
		switch (cmd) {
		case session:
			json = g.toJson(makeEmptyResponse());
			break;
		case updates:
			json = createUpdateMessage();
			break;
		case renderer_dblclick:
			String c = request.getParameter("text");
			handleRendererDblClick(box, c);
			break;
		case default_lang:
			defaultlang = request.getParameter("lang");
			break;
		case switch_box_lang:
			String newlang = request.getParameter("lang");
			switchLanguage(box, newlang);
			break;

		case leave:
			String content = request.getParameter("text");
			String direction = request.getParameter("direction");
			leaveBox(session, box, content, direction);
			break;
		default:
			break;
		}

		if (!json.isEmpty())
			out.println(json);
		out.close();
	}

	private void switchLanguage(String box, String newlang) {
		Integer id = Integer.valueOf(box);
		String content = editors.get(id).getText();
		Editor editor = new Editor(box, newlang, content);
		editors.set(id, editor);
		enqueue(unfocus(box, content, editors));
		// active = Integer.valueOf(box);

	}

	private void leaveBox(String session, String box, String content,
			String direction) {
		int id = Integer.parseInt(box);
		if (active != id || active < 0)
			return;

		enqueue(unfocus(box, content, editors));

		if ("up".equals(direction)) {
			if (id > 0) {
				int nid = id - 1;
				activate(nid, DIR_FROM_BELOW);
				active = nid;
			}
		}
		if ("down".equals(direction)) {
			if (id < editors.size() - 1) {
				int nid = id + 1;
				activate(nid, DIR_FROM_ABOVE);
				active = nid;
			} else {
				appendNewBox(session, q, editors);
			}
		}
	}

	private void handleRendererDblClick(String box, String text) {
		if (active >= 0) {
			enqueue(unfocus(String.valueOf(active), text, editors));
		}
		activate(box, DIR_FROM_SOMEWHERE_ELSE);
		active = Integer.valueOf(box);
	}

	private String createUpdateMessage() {
		String r = "";
		if (!q.isEmpty()) {
			ArrayList<String> list = new ArrayList<String>();
			do {
				String s = dequeue();
				list.add(s);
			} while (!q.isEmpty());
			r = g.toJson(list);
		}
		return r;
	}

	private void activate(String box, String direction) {
		int id = Integer.parseInt(box);
		activate(id, direction);
	}

	@SuppressWarnings("unused")
	private void printparams(HttpServletRequest request) { // Debug method
		if ("updates".equals(request.getParameter("cmd")))
			return;
		Set<Entry<String, String[]>> entrySet = request.getParameterMap()
				.entrySet();
		for (Entry<String, String[]> entry : entrySet) {
			String k = entry.getKey();
			String v = Joiner.on(",").join(entry.getValue());
			System.out.print(k + "=>" + v);
			System.out.print(" ");
		}
		System.out.println();
	}

	private void activate(int id, String direction) {
		Editor e = editors.get(id);
		String sid = String.valueOf(id);

		enqueue("cmd", "activate", "id", sid, "lang", e.type.toString(),
				"text", e.getText());
		enqueue("cmd", "focus", "id", sid, "direction", direction);
	}

	private String dequeue() {
		if (q.isEmpty())
			return null;
		return q.poll();
	}

	private void enqueue(String json) {
		q.add(json);
	}

	private void enqueue(String... stuff) {
		q.add(toJson(stuff));
	}

	private String defaultLanguage() {
		return defaultlang;
	}

	private void appendNewBox(String session, Queue<String> q,
			ArrayList<Editor> editors) {
		String id = String.valueOf(editors.size());
		active = editors.size();
		editors.add(new Editor(id, defaultLanguage(), ""));
		enqueue("cmd", "append_box", "id", id, "lang", defaultLanguage(),
				"content", "");
		enqueue("cmd", "focus", "id", id);

	}

	public String toJson(String... args) {
		if (args.length % 2 != 0)
			throw new IllegalArgumentException(
					"Require an even number of key/values");

		Map<String, Object> response = makeEmptyResponse();
		for (int i = 0; i < args.length; i = i + 2) {
			response.put(args[i], args[i + 1]);
		}

		String json = g.toJson(response);
		return json;
	}

	private Map<String, Object> makeEmptyResponse() {
		Map<String, Object> resp;
		resp = new HashMap<String, Object>();
		resp.put("id", message_counter++);
		resp.put("session", session);
		return resp;
	}

	private String unfocus(final String box, final String content,
			ArrayList<Editor> editors) {
		active = -1;
		Editor editor = editors.get(Integer.parseInt(box));

		editor.setText(content);

		Map<String, String> lemap = new HashMap<String, String>();

		lemap.put("id", box);

		String evaluated = evaluate(editor);
		lemap.put("text", evaluated);

		String a = g.toJson(lemap);
		return toJson("cmd", "render", "id", box, "template",
				getTemplate(editor.type), "lang", editor.type.toString(),
				"args", a);
	}

	private String getTemplate(EBoxTypes type) {
		switch (type) {
		case markdown:
			return "none";
		default:
			return "worksheet_renderer.html";
		}
	}

	private String evaluate(Editor editor) {
		EBoxTypes type = editor.type;
		String text = editor.getText();
		switch (type) {
		case groovy:
			Object result;
			try {
				result = groovy.eval(text);
			} catch (ScriptException e) {
				return "Error";
			}
			return result == null ? "null" : result.toString();
		case b:
			return "GTFO! " + text + "\n Do I look like a calculator?";
		case markdown:
			return pegdown.markdownToHtml(text);
		default:
			return "";
		}
	}
}
