package de.prob.web.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;

import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;
import de.prob.web.data.Message;
import de.prob.web.worksheet.boxes.EChangeEffect;
import de.prob.worksheet.ScriptEngineProvider;

public class Worksheet extends AbstractSession {

	private int boxcount = 0;
	private final Map<String, IBox> boxes = Collections
			.synchronizedMap(new HashMap<String, IBox>());

	private final BoxFactory boxfactory;

	private String defaultboxtype = "Markdown";
	private volatile ScriptEngine groovy;
	private final Logger logger = LoggerFactory.getLogger(Worksheet.class);
	private final List<String> order = Collections
			.synchronizedList(new ArrayList<String>());

	private final ScriptEngineProvider sep;

	@Inject
	public Worksheet(ScriptEngineProvider sep, BoxFactory boxfactory) {
		this.sep = sep;
		this.boxfactory = boxfactory;
		groovy = sep.get();
	}

	private IBox appendFreshBox() {
		IBox box = makeBox(defaultboxtype);
		order.add(box.getId());
		return box;
	}

	public Object deleteBox(Map<String, String[]> params) {
		List<Object> messages = new ArrayList<Object>();
		String box = params.get("number")[0];
		logger.trace("Delete box {}", box);
		int index = order.indexOf(box);
		order.remove(index);
		IBox deleted = boxes.get(box);
		boxes.remove(box);
		Map<String, String> deleteCmd = WebUtils.wrap("cmd",
				"Worksheet.deleteBox", "id", box);
		messages.add(deleteCmd);
		if (order.size() == 0) {
			Map<String, String> renderCmd = appendFreshBox().createMessage();
			messages.add(renderCmd);
		} else if (index != order.size()) {
			messages.addAll(reEvaluate(deleted.changeEffect(), index));
		}
		return messages.toArray();
	}

	private String firstBox() {
		return order.isEmpty() ? null : order.get(0);
	}

	public ScriptEngine getGroovy() {
		return groovy;
	}

	private String getPredecessor(String boxId) {
		int index = order.indexOf(boxId) - 1;
		return order.get(index);
	}

	private String getSuccessor(String boxId) {
		int index = order.indexOf(boxId) + 1;
		return order.get(index);
	}

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

	private String lastBox() {
		int index = order.size() - 1;
		return index >= 0 ? order.get(index) : null;
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
		messages.addAll(render(box));
		messages.addAll(reEvaluate(boxId, order.indexOf(boxId)));
		return messages.toArray(new Object[messages.size()]);
	}

	private List<Object> render(IBox box) {

		Predicate<Entry<String, Object>> p = new Predicate<Entry<String, Object>>() {
			@Override
			public boolean apply(@Nullable Entry<String, Object> input) {
				return !input.getKey().startsWith("__");
			}
		};
		Comparator<Entry<String, Object>> comperator = new Comparator<Entry<String, Object>>() {
			@Override
			public int compare(Entry<String, Object> o1,
					Entry<String, Object> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		};

		List<Object> box_rendering = box.render();

		Collection<Entry<String, Object>> bindings_global = Collections2
				.filter(groovy.getBindings(ScriptContext.GLOBAL_SCOPE)
						.entrySet(), p);
		Collection<Entry<String, Object>> bindings_local = Collections2.filter(
				groovy.getBindings(ScriptContext.ENGINE_SCOPE).entrySet(), p);

		List<Entry<String, Object>> vars = new ArrayList<Entry<String, Object>>();
		vars.addAll(bindings_local);
		vars.addAll(bindings_global);
		Collections.sort(vars, comperator);

		Function<Entry<String, Object>, Map<String, String>> toJson = new Function<Map.Entry<String, Object>, Map<String, String>>() {

			@Override
			@Nullable
			public Map<String, String> apply(
					@Nullable Entry<String, Object> input) {
				HashMap<String, String> result = new HashMap<String, String>();
				result.put("name", input.getKey());
				result.put("value", input.getValue().toString());
				return result;
			}
		};
		Collection<Map<String, String>> vars2 = Collections2.transform(vars,
				toJson);

		box_rendering.add(WebUtils.wrap("cmd", "Worksheet.aside", "number",
				box.getId(), "aside", WebUtils.toJson(vars2)));

		return box_rendering;
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

	public IBox makeBox(String type) {
		IBox box = boxfactory.create(this, boxcount++, type);
		boxes.put(box.getId(), box);
		return box;
	}

	private List<Object> reEvaluateBoxes(int reorderposition) {
		ArrayList<Object> messages = new ArrayList<Object>();
		if (reorderposition == 0) {
			groovy = sep.get();
		}
		logger.trace("Re-Evaluating boxes, starting at box {}", reorderposition);
		for (int i = reorderposition; i < order.size(); i++) {
			String id = order.get(i);
			IBox box = boxes.get(id);
			if (box.requiresReEvaluation()) {
				messages.addAll(render(box));
			}
		}
		return messages;
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
				cp.addAll(render(b));
				cp.add(WebUtils.wrap("cmd", "Worksheet.unfocus", "number", id));
			}

			// cp.add(WebUtils.wrap("cmd", "reloaded"));

			Object[] everything = cp.toArray();
			Message m = new Message(lm.id, everything);
			String json = WebUtils.toJson(m);
			send(json, context);
		}
	}

	public Object reorder(Map<String, String[]> params) {
		String boxId = params.get("box")[0];
		int oldPos = order.indexOf(boxId);
		int newpos = Integer.parseInt(params.get("newpos")[0]);

		order.remove(boxId);
		order.add(newpos, boxId);

		int position = Math.min(oldPos, newpos);
		logger.trace("Reordered box {}. From {} to {}.", new Object[] { boxId,
				oldPos, newpos });
		return reEvaluate(boxId, position).toArray();
	}

	private List<Object> reEvaluate(String boxId, int position) {
		EChangeEffect effect = boxes.get(boxId).changeEffect();
		return reEvaluate(effect, position);
	}

	private List<Object> reEvaluate(EChangeEffect effect, int position) {
		switch (effect) {
		case DONT_CARE:
			return Collections.emptyList();
		case EVERYTHING_BELOW:
			return reEvaluateBoxes(position);
		default: // FULL_REEVALUATION
			return reEvaluateBoxes(0);
		}
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
}
