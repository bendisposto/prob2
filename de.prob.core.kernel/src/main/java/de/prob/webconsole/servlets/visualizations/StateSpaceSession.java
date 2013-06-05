package de.prob.webconsole.servlets.visualizations;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.prob.animator.command.ApplySignatureMergeCommand;
import de.prob.animator.command.CalculateTransitionDiagramCommand;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.BEvent;
import de.prob.model.representation.Machine;
import de.prob.statespace.IStateSpace;
import de.prob.statespace.IStatesCalculatedListener;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.statespace.derived.AbstractDerivedStateSpace;
import de.prob.statespace.derived.AbstractDottyGraph;
import de.prob.statespace.derived.DottySignatureMerge;
import de.prob.statespace.derived.DottyTransitionDiagram;
import de.prob.statespace.derived.SignatureMergedStateSpace;
import de.prob.statespace.derived.TransitionDiagram;
import de.prob.visualization.AbstractData;
import de.prob.visualization.AnimationProperties;
import de.prob.visualization.DerivedStateSpaceData;
import de.prob.visualization.DottyData;
import de.prob.visualization.DynamicTransformer;
import de.prob.visualization.StateSpaceData;
import de.prob.visualization.Transformer;

public class StateSpaceSession implements ISessionServlet,
		IStatesCalculatedListener, IVisualizationServlet {

	Logger logger = LoggerFactory.getLogger(StateSpaceSession.class);
	private final String filename;
	private IStateSpace space;
	private AbstractData data;
	private String expression;
	private final List<String> errors = new ArrayList<String>();
	private final Map<String, EnabledEvent> events = new HashMap<String, EnabledEvent>();
	private final List<String> disabledEvents = new ArrayList<String>();
	private final Set<IRefreshListener> refreshListeners = new HashSet<IRefreshListener>();
	private final AnimationProperties props;
	private final String sessionId;

	public StateSpaceSession(final String sessionId, final StateSpace space,
			final AnimationProperties props) {
		this.sessionId = sessionId;
		this.space = space;
		this.props = props;
		if (space != null) {
			data = createStateSpaceGraph();
		}
		AbstractElement mainComponent = space.getModel().getMainComponent();
		if (mainComponent instanceof Machine) {
			Set<BEvent> ops = mainComponent.getChildrenOfType(BEvent.class);
			for (BEvent bEvent : ops) {
				EnabledEvent e = new EnabledEvent(bEvent.getName(), true);
				events.put(bEvent.getName(), e);
			}
		}
		filename = props.getPropFileFromModelFile(space.getModel()
				.getModelFile().getAbsolutePath());
		props.setProperty(filename, sessionId, serialize());
	}

	public StateSpaceSession(final String sessionId, final StateSpace space,
			final int mode, final List<String> disabledEvents,
			final String expression, final List<Transformer> transformers,
			final AnimationProperties props) {
		this.sessionId = sessionId;
		this.space = space;
		this.props = props;
		if (space != null) {
			data = createStateSpaceGraph();
		}
		AbstractElement mainComponent = space.getModel().getMainComponent();
		if (mainComponent instanceof Machine) {
			Set<BEvent> ops = mainComponent.getChildrenOfType(BEvent.class);
			for (BEvent bEvent : ops) {
				EnabledEvent e = new EnabledEvent(bEvent.getName(), true);
				events.put(bEvent.getName(), e);
			}
		}
		filename = props.getPropFileFromModelFile(space.getModel()
				.getModelFile().getAbsolutePath());
		disabledEvents.addAll(disabledEvents);
		this.expression = expression;
		if (mode == 2) {
			data = createSigMergeGraph();
		} else if (mode == 3) {
			data = createTransitionDiagram(expression);
		} else if (mode == 4) {
			data = createDottySignatureMerge();
		} else if (mode == 5) {
			data = createDottyTransitionDiagram(expression);
		}

		for (Transformer transformer : transformers) {
			data.addStyling(transformer);
		}

		props.setProperty(filename, sessionId, serialize());
	}

	@Override
	public void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws IOException {
		if (req.getParameter("cmd") != null) {
			performCommand(req, resp);
		} else if (req.getParameter("sessionId") != null) {
			normalResponse(req, resp);
		}
	}

	private void performCommand(final HttpServletRequest req,
			final HttpServletResponse res) throws IOException {
		String cmd = req.getParameter("cmd");
		String p = req.getParameter("param");
		List<Object> result = new ArrayList<Object>();

		try {
			if (space != null) {
				if (cmd.equals("sig_merge")) {
					recalculateEvents(p);
					data = createSigMergeGraph();
				} else if (cmd.equals("org_ss")) {
					data = createStateSpaceGraph();
				} else if (cmd.equals("trans_diag")) {
					data = createTransitionDiagram(p);
					expression = p;
				} else if (cmd.equals("d_sig_merge")) {
					recalculateEvents(p);
					data = createDottySignatureMerge();
				} else if (cmd.equals("d_trans_diag")) {
					data = createDottyTransitionDiagram(p);
					expression = p;
				}
			}
			props.setProperty(filename, sessionId, serialize());
			result.add("success");
		} catch (Throwable e) {
			errors.add("creating visualization of type " + cmd
					+ " with parameter " + p + " resulted in this exception: "
					+ e.getClass().getSimpleName() + ": " + e.getMessage());
			logger.error(e.getClass().getSimpleName() + ": " + e.getMessage());
			result.add("failure");
		} finally {
			PrintWriter out = res.getWriter();
			Gson g = new Gson();
			out.println(g.toJson(result));
			out.close();
		}

	}

	private void recalculateEvents(final String p) {
		JsonElement parse = new JsonParser().parse(p);
		JsonArray array = parse.getAsJsonArray();
		List<String> disabled = new ArrayList<String>();
		for (JsonElement jsonElement : array) {
			JsonObject object = jsonElement.getAsJsonObject();
			String name = object.get("name").getAsString();
			boolean checked = object.get("checked").getAsBoolean();
			if (events.get(name).checked != checked) {
				events.put(name, new EnabledEvent(name, checked));

				// If the box checked for the event and it is in the disabled
				// list, remove it
				if (checked && disabledEvents.contains(name)) {
					disabledEvents.remove(name);
				} else {
					// If the box is not checked and it is not in the disabled
					// list, add it
					if (!disabled.contains(name)) {
						disabledEvents.add(name);
					}
				}
			}
		}
	}

	private void normalResponse(final HttpServletRequest req,
			final HttpServletResponse res) throws IOException {
		PrintWriter out = res.getWriter();
		Map<String, Object> resp = new HashMap<String, Object>();

		Boolean getFormula = Boolean.valueOf(req.getParameter("getSS"));
		Boolean getAllStates = Boolean.valueOf(req.getParameter("getAll"));

		if (getFormula) {
			if (getAllStates) {
				resp.put("data", data.getData());
			} else {
				resp.put("data", data.getChanges());
			}
			data.setReset(false);
		}
		resp.put("count", data.count());
		resp.put("varCount", data.varSize());
		resp.put("reset", data.getReset());
		resp.put("mode", data.getMode());
		resp.put("events", events.values());
		resp.put("errors", errors);
		errors.clear();

		Gson g = new Gson();

		String json = g.toJson(resp);
		out.println(json);
		out.close();
	}

	private AbstractData createStateSpaceGraph() {
		StateSpace s = extractStateSpace();
		return changeStateSpaceTo(s);
	}

	private StateSpace extractStateSpace() {
		if (space instanceof StateSpace) {
			return (StateSpace) space;
		} else if (space instanceof AbstractDerivedStateSpace) {
			return ((AbstractDerivedStateSpace) space).getStateSpace();
		} else if (space instanceof AbstractDottyGraph) {
			return ((AbstractDottyGraph) space).getStateSpace();
		}
		return null;
	}

	private AbstractData createSigMergeGraph() {
		ApplySignatureMergeCommand cmd = new ApplySignatureMergeCommand(
				disabledEvents);
		space.execute(cmd);
		SignatureMergedStateSpace s = new SignatureMergedStateSpace(space, cmd,
				disabledEvents);
		s.addStates(cmd.getStates());
		s.addTransitions(cmd.getOps());
		AbstractData d = changeStateSpaceTo(s);
		d.setMode(2);
		return d;
	}

	private AbstractData createTransitionDiagram(final String parameter) {
		CalculateTransitionDiagramCommand cmd = new CalculateTransitionDiagramCommand(
				parameter);
		space.execute(cmd);
		TransitionDiagram s = new TransitionDiagram(space, parameter, cmd);
		s.addStates(cmd.getStates());
		s.addTransitions(cmd.getOps());

		AbstractData d = changeStateSpaceTo(s);
		d.setMode(3);
		return d;
	}

	private AbstractData createDottySignatureMerge() {
		DottySignatureMerge s = new DottySignatureMerge(space, disabledEvents);

		notifyRefresh();
		AbstractData d = changeStateSpaceTo(s);
		d.setMode(4);
		return d;
	}

	private AbstractData createDottyTransitionDiagram(final String expression) {
		System.out.println(expression);
		DottyTransitionDiagram s = new DottyTransitionDiagram(space, expression);
		System.out.println(space);
		notifyRefresh();
		AbstractData d = changeStateSpaceTo(s);
		d.setMode(5);
		return d;
	}

	private AbstractData changeStateSpaceTo(final IStateSpace to) {
		space.deregisterStateSpaceListener(this);

		AbstractData data = calculateData(to);

		to.registerStateSpaceListener(this);
		space = to;
		data.setReset(true);
		return data;
	}

	private AbstractData calculateData(final IStateSpace s) {
		AbstractData d = createData(s);
		if (d instanceof DottyData) {
			return d;
		}

		if (s instanceof StateSpace) {
			((StateSpace) s).calculateVariables();
			((StateSpace) s).getEvaluatedOps();
		}

		Collection<StateId> vertices = s.getSSGraph().getVertices();
		for (StateId stateId : vertices) {
			d.addNode(stateId);
		}

		Collection<OpInfo> edges = s.getSSGraph().getEdges();
		for (OpInfo opInfo : edges) {
			d.addLink(opInfo);
		}

		d.updateTransformers();
		return d;
	}

	private AbstractData createData(final IStateSpace s) {
		if (s instanceof StateSpace) {
			return new StateSpaceData((StateSpace) s);
		} else if (s instanceof AbstractDerivedStateSpace) {
			return new DerivedStateSpaceData((AbstractDerivedStateSpace) s);
		} else if (s instanceof AbstractDottyGraph) {
			return new DottyData((AbstractDottyGraph) s);
		}
		return null;
	}

	@Override
	public void newTransitions(final List<? extends OpInfo> newOps) {
		try {
			if (space instanceof StateSpace) {
				((StateSpace) space).calculateVariables();
			}
			if (space instanceof AbstractDottyGraph) {
				notifyRefresh();
			}
			data.addNewLinks(space.getSSGraph(), newOps);
		} catch (Exception e) {
			errors.add("Exception thrown at new transition " + e.getMessage());
			logger.error(e.getClass().getSimpleName() + ": " + e.getMessage());
		}

	}

	@Override
	public void apply(final Transformer styling) {
		data.addStyling(styling);
		if (styling instanceof DynamicTransformer) {
			props.setProperty(filename, sessionId, serialize());
		}
	}

	class EnabledEvent {
		public String name;
		public Boolean checked;

		public EnabledEvent(final String name, final Boolean enabled) {
			this.name = name;
			checked = enabled;
		}
	}

	public void registerRefreshListener(final IRefreshListener l) {
		refreshListeners.add(l);
	}

	public void deregisterRefreshListener(final IRefreshListener l) {
		refreshListeners.remove(l);
	}

	public void notifyRefresh() {
		for (IRefreshListener l : refreshListeners) {
			l.refresh();
		}
	}

	public String serialize() {
		final List<String> serializedDyTrans = new ArrayList<String>();
		List<Transformer> styling = data.getStyling();
		for (Transformer transformer : styling) {
			if (transformer instanceof DynamicTransformer) {
				serializedDyTrans.add(((DynamicTransformer) transformer)
						.serialize());
			}
		}

		ToSerialize toSerialize = new ToSerialize(data.getMode(),
				disabledEvents, expression, serializedDyTrans);

		Gson g = new Gson();

		return g.toJson(toSerialize);
	}

	class ToSerialize {
		public int mode;
		public List<String> disabled;
		public String expr;
		public List<String> transformers;

		public ToSerialize(final int mode, final List<String> disabled,
				final String expr, final List<String> transformers) {
			this.mode = mode;
			this.disabled = disabled;
			this.expr = expr;
			this.transformers = transformers;
		}
	}
}
