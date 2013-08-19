package de.prob.web.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;
import de.prob.web.data.Message;
import de.prob.worksheet.ScriptEngineProvider;

public class Worksheet extends AbstractSession {

	private final BoxFactory boxfactory;

	@Inject
	public Worksheet(ScriptEngineProvider sep, BoxFactory boxfactory) {
		this.boxfactory = boxfactory;
		groovy = sep.get();
	}

	private final Logger logger = LoggerFactory.getLogger(Worksheet.class);
	private int boxcount = 0;
	private final Map<String, IBox> boxes = Collections
			.synchronizedMap(new HashMap<String, IBox>());
	private final List<String> order = Collections
			.synchronizedList(new ArrayList<String>());

	private String defaultboxtype = "Markdown";
	public final ScriptEngine groovy;

	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		ArrayList<Object> scopes = new ArrayList<Object>();
		scopes.add(WebUtils.wrap("clientid", clientid, "default-box-type",
				defaultboxtype));
		scopes.add(WebUtils.wrap("help-markdown", WebUtils.render(
				"ui/worksheet/help_markdown.html", new Object[] {})));
		String render = WebUtils.render("ui/worksheet/index.html",
				scopes.toArray());
		return render;
	}

	public Object reorder(Map<String, String[]> params) {
		String boxId = params.get("box")[0];
		int newpos = Integer.parseInt(params.get("newpos")[0]);

		order.remove(boxId);
		order.add(newpos, boxId);

		System.out.println("Reodered box " + boxId + ". New position: "
				+ newpos);
		return null;
	}

	public Object setDefaultType(Map<String, String[]> params) {
		String type = params.get("type")[0];
		defaultboxtype = type;
		return WebUtils.wrap("cmd", "Worksheet.setDefaultType", "type", type);
	}

	public Object switchType(Map<String, String[]> params) {
		String type = params.get("type")[0];
		String id = params.get("box")[0];
		logger.trace("Switch type of {} to {}", id, type);
		IBox box = boxfactory.create(this, id, type);
		box.setContent(params);
		boxes.put(id, box);
		return box.replaceMessage();
	}

	public Object deleteBox(Map<String, String[]> params) {
		String box = params.get("number")[0];
		logger.trace("Delete box {}", box);
		int index = order.indexOf(box);
		order.remove(index);
		boxes.remove(box);
		Map<String, String> deleteCmd = WebUtils.wrap("cmd",
				"Worksheet.deleteBox", "id", box);
		if (order.size() > 0)
			return deleteCmd;
		else {
			IBox freshbox = appendFreshBox();
			Map<String, String> renderCmd = freshbox.createMessage();
			return new Object[] { deleteCmd, renderCmd };
		}
	}

	public Object leaveEditor(Map<String, String[]> params) {
		String boxId = params.get("box")[0];
		String direction = params.get("direction")[0];
		String text = params.get("text")[0];

		logger.trace("Leaving {} direction {}. Content {}", new Object[] {
				boxId, direction, text });

		List<Object> messages = new ArrayList<Object>();

		if ("down".equals(direction)) {
			messages.addAll(leaveEditorDown(boxId, text));
		}

		if ("up".equals(direction) && boxId.equals(firstBox()))
			return null;// ignore

		if ("up".equals(direction) && !boxId.equals(firstBox())) {
			messages.add(WebUtils.wrap("cmd", "Worksheet.unfocus", "number",
					boxId));
			String focused = getPredecessor(boxId);
			messages.add(WebUtils.wrap("cmd", "Worksheet.focus", "number",
					focused, "direction", "up"));
		}
		IBox box = boxes.get(boxId);
		box.setContent(params);
		messages.addAll(box.render());
		return messages.toArray(new Object[messages.size()]);
	}

	private List<Object> leaveEditorDown(String boxId, String text) {
		ArrayList<Object> res = new ArrayList<Object>();
		res.add(WebUtils.wrap("cmd", "Worksheet.unfocus", "number", boxId));
		if (boxId.equals(lastBox())) {
			res.add(appendFreshBox().createMessage());
		} else {
			String focused = getSuccessor(boxId);
			res.add(WebUtils.wrap("cmd", "Worksheet.focus", "number", focused,
					"direction", "down"));
		}
		return res;

	}

	private String getSuccessor(String boxId) {
		int index = order.indexOf(boxId) + 1;
		return order.get(index);
	}

	private String getPredecessor(String boxId) {
		int index = order.indexOf(boxId) - 1;
		return order.get(index);
	}

	private String firstBox() {
		return order.isEmpty() ? null : order.get(0);
	}

	private String lastBox() {
		int index = order.size() - 1;
		return index >= 0 ? order.get(index) : null;
	}

	public IBox makeBox(String type) {
		IBox box = boxfactory.create(this, boxcount++, type);
		boxes.put(box.getId(), box);
		return box;
	}

	@Override
	public void reload(String client, int lastinfo, AsyncContext context) {
		if (responses.isEmpty()) {
			IBox box = appendFreshBox();
			Map<String, String> renderCmd = box.createMessage();
			Map<String, String> focusCmd = WebUtils.wrap("cmd",
					"Worksheet.focus", "number", box.getId());
			submit(renderCmd, focusCmd);
			resend(client, 0, context);
		} else {
			Message lm = responses.get(responses.size() - 1);
			ArrayList<Object> cp = new ArrayList<Object>();

			for (String id : order) {
				IBox b = boxes.get(id);
				cp.add(b.createMessage());
				cp.addAll(b.render());
				cp.add(WebUtils.wrap("cmd", "Worksheet.unfocus", "number", id));
			}

			// cp.add(WebUtils.wrap("cmd", "reloaded"));

			Object[] everything = cp.toArray();
			Message m = new Message(lm.id, everything);
			String json = WebUtils.toJson(m);
			send(json, context);
		}
	}

	private IBox appendFreshBox() {
		IBox box = makeBox(defaultboxtype);
		order.add(box.getId());
		return box;
	}
}
