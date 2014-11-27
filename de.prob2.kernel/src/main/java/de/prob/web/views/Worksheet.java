package de.prob.web.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.script.ScriptEngine;
import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.annotations.Dangerous;
import de.prob.scripting.ScriptEngineProvider;
import de.prob.web.AbstractSession;
import de.prob.web.ISession;
import de.prob.web.ReflectionServlet;
import de.prob.web.WebUtils;
import de.prob.web.data.Message;
import de.prob.web.data.SessionResult;
import de.prob.web.worksheet.BindingsSnapshot;
import de.prob.web.worksheet.BoxFactory;
import de.prob.web.worksheet.EChangeEffect;
import de.prob.web.worksheet.IBox;
import de.prob.web.worksheet.VariableDetailTransformer;

@Dangerous
public class Worksheet extends AbstractSession {

	private int boxcount = 0;

	private final Map<String, IBox> boxes = Collections
			.synchronizedMap(new HashMap<String, IBox>());
	private final BindingsSnapshot initialSnapshot;
	private final Map<String, BindingsSnapshot> snapshots = Collections
			.synchronizedMap(new HashMap<String, BindingsSnapshot>());

	private final BoxFactory boxfactory;

	private String defaultboxtype = "Markdown";
	private volatile ScriptEngine groovy;
	private final Logger logger = LoggerFactory.getLogger(Worksheet.class);
	private final List<String> order = Collections
			.synchronizedList(new ArrayList<String>());

	private final ScriptEngineProvider sep;

	private final ReflectionServlet servlet;

	@Inject
	public Worksheet(final ScriptEngineProvider sep,
			final BoxFactory boxfactory, final ReflectionServlet servlet) {
		this.sep = sep;
		this.boxfactory = boxfactory;
		this.servlet = servlet;
		groovy = createGroovy();
		this.initialSnapshot = new BindingsSnapshot(groovy);
	}

	private ScriptEngine createGroovy() {
		synchronized (sep) {
			snapshots.clear();
			ScriptEngine g = sep.get();
			return g;
		}
	}

	private IBox appendFreshBox() {
		IBox box = makeBox(defaultboxtype);
		order.add(box.getId());
		return box;
	}

	public Object deleteBox(final Map<String, String[]> params) {
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
			IBox freshBox = appendFreshBox();
			initialSnapshot.restoreBindings(groovy);
			Map<String, String> renderCmd = freshBox.createMessage();
			messages.add(renderCmd);
			messages.addAll(freshBox.getMenuMessages());
		} else if (index != order.size()) {
			messages.addAll(reEvaluate(deleted.changeEffect(), index));
		}
		return messages.toArray();
	}

	private BindingsSnapshot restorePreviousBoxBindings(String boxId) {
		final BindingsSnapshot previous_snapshot = boxId.equals(order.get(0)) ? initialSnapshot
				: snapshots.get(getPredecessor(boxId));
		previous_snapshot.restoreBindings(groovy);
		return previous_snapshot;
	}

	private BindingsSnapshot getPreviousBoxBindings(String boxId) {
		final BindingsSnapshot previous_snapshot = boxId.equals(order.get(0)) ? initialSnapshot
				: snapshots.get(getPredecessor(boxId));
		previous_snapshot.restoreBindings(groovy);
		return previous_snapshot;
	}

	private String firstBox() {
		return order.isEmpty() ? null : order.get(0);
	}

	public ScriptEngine getGroovy() {
		return groovy;
	}

	private String getPredecessor(final String boxId) {
		int index = order.indexOf(boxId) - 1;
		return order.get(index);
	}

	private String getSuccessor(final String boxId) {
		int index = order.indexOf(boxId) + 1;
		return order.get(index);
	}

	public BindingsSnapshot getSnapshot(String boxId) {
		if (boxId.equals(order.get(0)))
			return null;
		return snapshots.get(boxId);
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
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

	public Object leaveEditor(final Map<String, String[]> params) {
		return leaveEditorMessages(params).toArray();
	}

	public List<Object> leaveEditorMessages(final Map<String, String[]> params) {
		String boxId = params.get("box")[0];
		String direction = params.get("direction")[0];
		String text = params.get("text")[0];

		logger.trace("Leaving {} direction {}. Content {}", new Object[] {
				boxId, direction, text });

		if ("up".equals(direction) && boxId.equals(firstBox())) {
			return null; // ignore
		}
		List<Object> messages = new ArrayList<Object>();
		if ("up".equals(direction) && !boxId.equals(firstBox())) {
			messages.add(WebUtils.wrap("cmd", "Worksheet.unfocus", "number",
					boxId));
			String focused = getPredecessor(boxId);
			messages.add(WebUtils.wrap("cmd", "Worksheet.focus", "number",
					focused, "direction", "up"));
		}
		// needs to be evaluated prior creation of new box in order to get the
		// correct snapshot while creation time
		IBox box = boxes.get(boxId);
		box.setContent(params);
		List<Object> renderBox = render(box);

		if ("down".equals(direction)) {
			messages.addAll(leaveEditorDown(boxId, text));
		}

		messages.addAll(renderBox);
		// FIXME Why render the same box twice in case of EVERYTHING_BELOW and
		// DONT_CARE?
		messages.addAll(reEvaluate(boxId, order.indexOf(boxId)));
		return messages;
	}

	private List<Object> leaveEditorDown(final String boxId, final String text) {
		ArrayList<Object> res = new ArrayList<Object>();
		res.add(WebUtils.wrap("cmd", "Worksheet.unfocus", "number", boxId));
		if (boxId.equals(lastBox())) {
			IBox freshBox = appendFreshBox();
			restorePreviousBoxBindings(freshBox.getId());
			res.add(freshBox.createMessage());
			res.addAll(freshBox.getMenuMessages());
		} else {
			String focused = getSuccessor(boxId);
			res.add(WebUtils.wrap("cmd", "Worksheet.focus", "number", focused,
					"direction", "down"));
		}
		return res;
	}

	public IBox makeBox(final String type) {
		IBox box = boxfactory.create(this, boxcount++, type);
		boxes.put(box.getId(), box);
		return box;
	}

	private List<Object> reEvaluate(final IBox box, final EChangeEffect effect,
			final int position) {
		switch (effect) {
		case DONT_CARE:
			return render(box);
		case EVERYTHING_BELOW:
			return reEvaluateBoxes(position);
		default: // FULL_REEVALUATION
			return reEvaluateBoxes(0);
		}
	}

	private List<Object> reEvaluate(final String boxId, final int position) {
		IBox box = boxes.get(boxId);
		EChangeEffect effect = box.changeEffect();
		return reEvaluate(box, effect, position);
	}

	private Collection<? extends Object> reEvaluate(
			final EChangeEffect changeEffect, final int index) {
		if (changeEffect == EChangeEffect.DONT_CARE) {
			return Collections.emptyList();
		}
		return reEvaluate(null, changeEffect, index);
	}

	private List<Object> reEvaluateBoxes(final int reorderposition) {
		if (reorderposition == 0) {
			groovy = createGroovy();
		}
		logger.trace("Re-Evaluating boxes, starting at box {}", reorderposition);

		final ISession delegate = this;

		Callable<SessionResult> invalidateCommand = new Callable<SessionResult>() {
			@Override
			public SessionResult call() throws Exception {
				ArrayList<Object> messages = new ArrayList<Object>();
				for (int i = reorderposition; i < order.size(); i++) {
					String id = order.get(i);
					IBox box = boxes.get(id);
					if (box.requiresReEvaluation()) {
						messages.add(WebUtils.wrap("cmd",
								"Worksheet.invalidate", "number", box.getId()));
					}
				}
				return new SessionResult(delegate, messages.toArray());
			}
		};
		Callable<SessionResult> reEvalCommand = new Callable<SessionResult>() {
			@Override
			public SessionResult call() throws Exception {
				ArrayList<Object> messages = new ArrayList<Object>();
				for (int i = reorderposition; i < order.size(); i++) {
					String id = order.get(i);
					IBox box = boxes.get(id);
					if (box.requiresReEvaluation()) {
						messages.addAll(render(box));
					}
				}
				return new SessionResult(delegate, messages.toArray());
			}
		};
		servlet.submit(invalidateCommand);
		servlet.submit(reEvalCommand);

		return Collections.emptyList();
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		VariableDetailTransformer.clear();
		if (responses.isEmpty()) {
			IBox box = appendFreshBox();
			this.initialSnapshot.restoreBindings(groovy);
			Map<String, String> renderCmd = box.createMessage();
			Map<String, String> focusCmd = WebUtils.wrap("cmd",
					"Worksheet.focus", "number", box.getId());
			submit(renderCmd, focusCmd);
			resend(client, 0, context);
		} else {
			Message lm;

			ArrayList<Object> cp = new ArrayList<Object>();
			for (String id : order) {
				IBox b = boxes.get(id);
				restorePreviousBoxBindings(b.getId());
				cp.add(b.createMessage());
				cp.addAll(render(b));
				cp.add(WebUtils.wrap("cmd", "Worksheet.unfocus", "number", id));
			}
			Object[] everything = cp.toArray();
			Message m = new Message(responses.size(), everything);
			String json = WebUtils.toJson(m);
			send(json, context);
		}
	}

	private List<Object> render(final IBox box) {
		BindingsSnapshot previous_snapshot = getPreviousBoxBindings(box.getId());
		List<Object> box_rendering = box.render(previous_snapshot);
		final BindingsSnapshot current_snapshot = new BindingsSnapshot(groovy);
		snapshots.put(box.getId(), current_snapshot);
		Object aside = box.getAside(previous_snapshot, current_snapshot);
		if (aside != null)
			box_rendering.add(aside);
		return box_rendering;
	}

	public Object refreshAll(final Map<String, String[]> params) {
		List<Object> messages = new ArrayList<Object>();
		if (params.get("text") != null) {
			List<Object> leaveEditorMessages = leaveEditorMessages(params);
			messages.addAll(leaveEditorMessages);
		}
		messages.addAll(reEvaluateBoxes(0));
		return messages.toArray();
	}

	public Object reorder(final Map<String, String[]> params) {
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

	public Object setDefaultType(final Map<String, String[]> params) {
		String type = params.get("type")[0];
		defaultboxtype = type;
		return WebUtils.wrap("cmd", "Worksheet.setDefaultType", "type", type);
	}

	public Object switchType(final Map<String, String[]> params) {
		String type = params.get("type")[0];
		String id = params.get("box")[0];
		logger.trace("Switch type of {} to {}", id, type);
		IBox box = boxfactory.create(this, id, type);
		box.setContent(params);
		boxes.put(id, box);
		BindingsSnapshot previous_snapshot = restorePreviousBoxBindings(box
				.getId());
		Collection<? extends Object> additionalMessages = box
				.getMenuMessages();
		int size = additionalMessages.size();
		Object aside = box.getAside(previous_snapshot, previous_snapshot);
		if (aside != null)
			size += 1;
		Object[] res = new Object[size + 1];
		res[0] = box.replaceMessage();
		if (aside != null)
			res[1] = aside;
		Iterator<? extends Object> it = additionalMessages.iterator();
		int pos = 1;
		while (it.hasNext()) {
			res[pos] = it.next();
			pos++;
		}
		return res;
	}
}
