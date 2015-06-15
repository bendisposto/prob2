package de.prob.web.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.animator.domainobjects.StateError;
import de.prob.annotations.OneToOne;
import de.prob.annotations.PublicSession;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventParameter;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.BEvent;
import de.prob.model.representation.Machine;
import de.prob.model.representation.ModelElementList;
import de.prob.scripting.ScriptEngineProvider;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.State;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;
import de.prob.web.AbstractAnimationBasedView;
import de.prob.web.WebUtils;

@PublicSession
@OneToOne
public class Events extends AbstractAnimationBasedView {

	Logger logger = LoggerFactory.getLogger(Events.class);
	Trace currentTrace;
	AbstractModel currentModel;
	List<String> opNames = new ArrayList<String>();
	Map<String, List<String>> opToParams = new HashMap<String, List<String>>();
	Comparator<Operation> sorter = new ModelOrder(new ArrayList<String>());
	List<Operation> events = new ArrayList<Operation>();
	private String filter = "";
	boolean hide = false;
	private final ScriptEngine groovy;

	@Inject
	public Events(final AnimationSelector selector,
			final ScriptEngineProvider sep) {
		super(selector);
		groovy = sep.get();
		incrementalUpdate = false;
		animationsRegistry.registerAnimationChangeListener(this);
	}

	// used in JS
	@SuppressWarnings("unused")
	private static class Operation {
		public final String name;
		public final List<String> params;
		public final String id;
		public final String enablement;

		public Operation(final String id, final String name,
				final List<String> params, final boolean isEnabled,
				final boolean hasTimeout) {
			this.id = id;
			this.name = name;
			this.params = params;
			enablement = isEnabled ? "enabled" : hasTimeout ? "timeout"
					: "notEnabled";
		}
	}

	private static class Error {
		@SuppressWarnings("unused")
		public final String shortMsg;
		@SuppressWarnings("unused")
		public final String longMsg;

		public Error(final String shortMsg, final String longMsg) {
			this.shortMsg = shortMsg;
			this.longMsg = longMsg;
		}
	}

	@Override
	public void performTraceChange(final Trace trace) {
		if (trace == null) {
			currentTrace = null;
			currentModel = null;
			opNames = new ArrayList<String>();
			if (sorter instanceof ModelOrder) {
				sorter = new ModelOrder(opNames);
			}
			Map<String, String> wrap = WebUtils.wrap("cmd", "Events.newTrace",
					"ops", WebUtils.toJson(opNames), "canGoBack", false,
					"canGoForward", false, "errors", "[]");
			submit(wrap);
			return;
		}

		if (trace.getModel() != currentModel) {
			updateModel(trace);
		}
		currentTrace = trace;
		Set<Transition> ops = currentTrace.getNextTransitions(true);
		events = new ArrayList<Operation>(ops.size());
		Set<String> notEnabled = new HashSet<String>(opNames);
		Set<String> tWT = currentTrace.getCurrentState()
				.getTransitionsWithTimeout();
		for (Transition opInfo : ops) {
			String name = extractPrettyName(opInfo.getName());
			notEnabled.remove(name);
			Operation o = new Operation(opInfo.getId(), name,
					opInfo.getParams(), true, tWT.contains(name));
			events.add(o);
		}
		for (String s : notEnabled) {
			if (!s.equals("INITIALISATION")) {
				events.add(new Operation(s, s, opToParams.get(s), false, tWT
						.contains(s)));
			}
		}
		try {
			Collections.sort(events, sorter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Error> errors = extractErrors(currentTrace.getCurrentState());
		String json = WebUtils.toJson(applyFilter(filter));
		String stringErrors = WebUtils.toJson(errors);
		Map<String, String> wrap = WebUtils.wrap("cmd", "Events.newTrace",
				"ops", json, "canGoBack", currentTrace.canGoBack(),
				"canGoForward", currentTrace.canGoForward(), "errors",
				stringErrors);
		submit(wrap);
	}

	private List<Error> extractErrors(final State state) {
		List<Error> errors = new ArrayList<Error>();

		if (!state.isInvariantOk()) {
			errors.add(new Error("Invariant Violation",
					"One of the invariants was violated. "
							+ "See the State Inspector for more details."));
		}

		if (state.isTimeoutOccurred()) {
			errors.add(new Error("Timeout Occurred",
					"A time out occurred for the current state."));
		}

		if (state.isMaxTransitionsCalculated()) {
			errors.add(new Error(
					"Max Transitions Reached",
					"It is possible that not all possible transitions "
							+ "were calculated for the given state. If you would like to calculate "
							+ "more transitions, increase the MAX_OPERATIONS preference."));
		}

		for (StateError e : state.getStateErrors()) {
			errors.add(new Error(e.getShortDescription(), "For event "
					+ e.getEvent() + " the following error occurred: "
					+ e.getLongDescription()));
		}

		return errors;
	}

	private String extractPrettyName(final String name) {
		if ("$setup_constants".equals(name)) {
			return "SETUP_CONSTANTS";
		}
		if ("$initialise_machine".equals(name)) {
			return "INITIALISATION";
		}
		return name;
	}

	private void updateModel(final Trace trace) {
		currentModel = trace.getModel();
		AbstractElement mainComponent = trace.getStateSpace()
				.getMainComponent();
		opNames = new ArrayList<String>();
		opToParams = new HashMap<String, List<String>>();
		if (mainComponent instanceof Machine) {
			ModelElementList<BEvent> events = mainComponent
					.getChildrenOfType(BEvent.class);
			for (BEvent e : events) {
				opNames.add(e.getName());

				List<String> pList = new ArrayList<String>();
				if (e instanceof Event) {
					for (EventParameter eP : ((Event) e).getParameters()) {
						pList.add(eP.getName());
					}
				} else if (e instanceof de.prob.model.classicalb.Operation) {
					pList.addAll(((de.prob.model.classicalb.Operation) e)
							.getParameters());
				}
				opToParams.put(e.getName(), pList);
			}
		}
		if (sorter instanceof ModelOrder) {
			sorter = new ModelOrder(opNames);
		}
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/eventview/index.html");
	}

	public Object execute(final Map<String, String[]> params) {
		String id = params.get("id")[0];
		animationsRegistry.traceChange(currentTrace.add(id));
		return null;
	}

	public Object executeEvent(final Map<String, String[]> params) {
		String event = params.get("event")[0];
		String code = "t = animations.getCurrentTrace();"
				+ "t1 = execTrace(t) { " + event + "};"
				+ "animations.replaceTrace(t,t1)";
		try {
			groovy.eval(code);
		} catch (ScriptException e) {
			logger.error("Not able to execute event " + event
					+ " for current trace. " + e.getMessage());
		}
		return null;
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		sendInitMessage(context);
		Map<String, String> wrap = WebUtils.wrap(
				"cmd",
				"Events.setView",
				"ops",
				WebUtils.toJson(events),
				"canGoBack",
				currentTrace == null ? false : currentTrace.canGoBack(),
						"canGoForward",
						currentTrace == null ? false : currentTrace.canGoForward(),
								"sortMode",
								getSortMode(),
								"hide",
								hide,
								"errors",
								currentTrace == null ? "[]" : WebUtils
										.toJson(extractErrors(currentTrace.getCurrentState())));
		submit(wrap);
	}

	public Object random(final Map<String, String[]> params) {
		int num = Integer.parseInt(params.get("num")[0]);
		animationsRegistry.traceChange(currentTrace.randomAnimation(num));
		return null;
	}

	public Object back(final Map<String, String[]> params) {
		animationsRegistry.traceChange(currentTrace.back());
		return null;
	}

	public Object forward(final Map<String, String[]> params) {
		animationsRegistry.traceChange(currentTrace.forward());
		return null;
	}

	public Object sort(final Map<String, String[]> params) {
		String mode = params.get("sortMode")[0];
		if ("normal".equals(mode)) {
			sorter = new ModelOrder(opNames);
		} else if ("aToZ".equals(mode)) {
			sorter = new AtoZ();
		} else if ("zToA".equals(mode)) {
			sorter = new ZtoA();
		}
		Collections.sort(events, sorter);
		return WebUtils.wrap("cmd", "Events.setContent", "ops",
				WebUtils.toJson(applyFilter(filter)));
	}

	public String getSortMode() {
		if (sorter instanceof ModelOrder) {
			return "normal";
		}
		if (sorter instanceof AtoZ) {
			return "aToZ";
		}
		if (sorter instanceof ZtoA) {
			return "zToA";
		}
		return "other";
	}

	public Object filter(final Map<String, String[]> params) {
		filter = params.get("filter")[0];
		List<Operation> filteredEvents = applyFilter(filter);
		return WebUtils.wrap("cmd", "Events.setContent", "ops",
				WebUtils.toJson(filteredEvents));
	}

	public Object hide(final Map<String, String[]> params) {
		hide = Boolean.valueOf(params.get("hidden")[0]);
		return null;
	}

	private List<Operation> applyFilter(final String filter) {
		List<Operation> newOps = new ArrayList<Operation>();
		for (Operation op : events) {
			if (op.name.startsWith(filter)) {
				newOps.add(op);
			}
		}
		return newOps;
	}

	private class EventComparator {

		private String stripString(final String param) {
			return param.replaceAll("\\{", "").replaceAll("\\}", "");
		}

		public int compareParams(final List<String> params1,
				final List<String> params2) {
			for (int i = 0; i < params1.size(); i++) {
				String p1 = stripString(params1.get(i));
				String p2 = stripString(params2.get(i));
				if (p1.compareTo(p2) != 0) {
					return p1.compareTo(p2);
				}

			}
			return 0;
		}
	}

	private class ModelOrder extends EventComparator implements
	Comparator<Operation> {

		private final List<String> ops;

		public ModelOrder(final List<String> ops) {
			this.ops = ops;
		}

		@Override
		public int compare(final Operation o1, final Operation o2) {
			if (ops.contains(o1.name) && ops.contains(o2.name)
					&& ops.indexOf(o1.name) == ops.indexOf(o2.name)) {
				return compareParams(o1.params, o2.params);
			}
			return ops.indexOf(o1.name) - ops.indexOf(o2.name);
		}
	}

	private class AtoZ extends EventComparator implements Comparator<Operation> {

		@Override
		public int compare(final Operation o1, final Operation o2) {
			if (o1.name.compareTo(o2.name) == 0) {
				return compareParams(o1.params, o2.params);
			}
			return o1.name.compareTo(o2.name);
		}

	}

	private class ZtoA extends EventComparator implements Comparator<Operation> {

		@Override
		public int compare(final Operation o1, final Operation o2) {
			if (o1.name.compareTo(o2.name) == 0) {
				return compareParams(o1.params, o2.params);
			}
			return -1 * o1.name.compareTo(o2.name);
		}

	}

	@Override
	public void animatorStatus(final boolean busy) {
		if (busy) {
			submit(WebUtils.wrap("cmd", "Events.disable"));
		} else {
			submit(WebUtils.wrap("cmd", "Events.enable"));
		}
	}
}
