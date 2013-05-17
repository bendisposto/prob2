package de.prob.worksheet;

import groovy.lang.MissingPropertyException;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
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

import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.pegdown.PegDownProcessor;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class WorkSheet {

	private static final String DIR_FROM_ABOVE = "from_above";
	private static final String DIR_FROM_BELOW = "from_below";
	private static final String DIR_FROM_SOMEWHERE_ELSE = "from_somewhere_else";
	private String session;
	private final Queue<String> q = new LinkedBlockingQueue<String>();
	private ArrayList<String> order = new ArrayList<String>();

	private final Map<String, Editor> editors = new HashMap<String, Editor>();

	private int active = -1;

	private static final String RENDERER_TEMPLATE_SIMPLE_TEXT = "worksheet_renderer.html";
	private static final String RENDERER_TEMPLATE_HTML = "none";

	private int message_counter = 0;
	private final Gson g = new Gson();
	private String defaultlang = "groovy";
	private PegDownProcessor pegdown;

	Type collectionType = new TypeToken<Collection<String>>() {
	}.getType();

	private Provider<ScriptEngine> groovyProvider;
	private ScriptEngine groovy;

	@Inject
	public WorkSheet(ScriptEngineProvider groovyProvider,
			PegDownProcessor pegdown) {
		this.groovyProvider = groovyProvider;
		this.pegdown = pegdown;
		groovy = groovyProvider.get();
	}

	public void doGet(String session, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.session = session;
		PrintWriter out = response.getWriter();

		printParams(request);

		String cmds = request.getParameter("cmd");

		ECmd cmd = ECmd.valueOf(cmds);
		String box = request.getParameter("box");

		switch (cmd) {
		case init:
			enqueue("cmd", "set_top", "lang", defaultLanguage());
			appendNewBox();
			break;
		case delete:
			editors.remove(box);
			enqueue("cmd", "delete", "id", box);
			if (editors.isEmpty()) {
				appendNewBox();
			}
			reEvalWorksheet(box);
			break;
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

		case reorder:
			String o = request.getParameter("order");
			Collection<String> fromJson = g.fromJson(o, collectionType);
			order = new ArrayList<String>(fromJson);
			reEvalWorksheet(box);

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
		String content = editors.get(box).getText();
		Editor editor = new Editor(box, newlang, content);
		editors.put(box, editor);
		unfocus(box, content);
		// active = Integer.valueOf(box);

	}

	private void leaveBox(String session, String box, String content,
			String direction) {

		int id = order.indexOf(box);
		if (active != id || active < 0)
			return;

		unfocus(box, content);

		if ("up".equals(direction)) {
			if (id > 0) {
				int nid = id - 1;
				activate(nid, DIR_FROM_BELOW);
				active = nid;
			}
		}
		if ("down".equals(direction)) {
			if (id < order.size() - 1) {
				int nid = id + 1;
				activate(nid, DIR_FROM_ABOVE);
				active = nid;
			} else {
				appendNewBox();
			}
		}
	}

	private void activate(int nid, String direction) {
		Editor e = editors.get(order.get(nid));
		enqueue("cmd", "activate", "id", e.id, "lang", e.type.toString(),
				"text", e.getText());
		enqueue("cmd", "focus", "id", e.id, "direction", direction);
	}

	private void handleRendererDblClick(String box, String text) {
		System.out.println(active + " " + box);
		if (active >= 0) {
			unfocus(order.get(active), text);
		}
		active = order.indexOf(box);
		activate(active, DIR_FROM_SOMEWHERE_ELSE);
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

	@SuppressWarnings("unused")
	private void printParams(HttpServletRequest request) { // Debug method
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

	@SuppressWarnings("unused")
	private void printEditors() { // Debug method

		for (int i = 0; i < order.size(); i++) {
			String id = order.get(i);
			Editor e = editors.get(id);
			System.out.println(i + " " + " " + e.type + " " + e.getText());
		}
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

	private void appendNewBox() {
		String id = String.valueOf(editors.size());
		active = editors.size();
		editors.put(id, new Editor(id, defaultLanguage(), ""));
		order.add(id);
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

	private void unfocus(final String box, final String content) {
		active = -1;
		Editor editor = editors.get(box);
		editor.setText(content);
		reEvalWorksheet(box);
	}

	private void reEvalWorksheet(String box) {
		groovy = groovyProvider.get();
		for (String b : order) {
			enqueueUpdate(b);
		}
	}

	private void enqueueUpdate(String id) {
		Editor editor = editors.get(id);
		String box = editor.id;
		Map<String, String> lemap = new HashMap<String, String>();

		lemap.put("id", box);

		RenderResult res = evaluate(editor);

		lemap.put("text", res.json);

		String a = g.toJson(lemap);
		enqueue(toJson("cmd", "render", "id", box, "template", res.template,
				"lang", editor.type.toString(), "args", a));
	}

	private RenderResult evaluate(Editor editor) {

		EBoxTypes type = editor.type;
		String text = editor.getText();
		switch (type) {
		case groovy:
			Object result = null;
			try {
				result = groovy.eval(text);
			} catch (ScriptException e) {
				return new RenderResult(RENDERER_TEMPLATE_HTML,
						pegdown.markdownToHtml("          "
								+ cleanGroovyException(e).replaceAll("\n",
										"\n        ")));
			}
			return new RenderResult(RENDERER_TEMPLATE_SIMPLE_TEXT,
					result == null ? "null" : result.toString());
		case b:
			return new RenderResult(RENDERER_TEMPLATE_SIMPLE_TEXT, "GTFO! "
					+ text + "\n Do I look like a calculator?");
		case markdown:
			return new RenderResult(RENDERER_TEMPLATE_HTML,
					pegdown.markdownToHtml(text));
		default:
			return new RenderResult(RENDERER_TEMPLATE_HTML, "DIE!");
		}
	}

	private String cleanGroovyException(ScriptException e) {

		String message = e.getMessage();
		if (e.getCause() instanceof MultipleCompilationErrorsException)
			return message.replaceAll("(.*\n.*Script.*?groovy): ", "");
		if (e.getCause().getCause() instanceof MissingPropertyException) {
			String r1 = message.replaceAll(".*property:", "No such property: ");
			String r2 = r1.replaceAll("for.*", "");
			return r2;
		}
		return message;
	}
}
