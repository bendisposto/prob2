package de.prob.webconsole.servlets.visualizations;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import de.prob.visualization.DerivedStateSpaceData;
import de.prob.visualization.DottyData;
import de.prob.visualization.StateSpaceData;
import de.prob.visualization.Transformer;

public class StateSpaceSession implements ISessionServlet,
		IStatesCalculatedListener, IVisualizationServlet {
	private IStateSpace space;
	private AbstractData data;
	private final Map<String, EnabledEvent> includedEvents = new HashMap<String, EnabledEvent>();

	public StateSpaceSession(final StateSpace space) {
		this.space = space;
		if (space != null) {
			data = createStateSpaceGraph();
		}
		AbstractElement mainComponent = space.getModel().getMainComponent();
		if (mainComponent instanceof Machine) {
			Set<BEvent> events = mainComponent.getChildrenOfType(BEvent.class);
			for (BEvent bEvent : events) {
				EnabledEvent e = new EnabledEvent(bEvent.getName(), true);
				includedEvents.put(bEvent.getName(), e);
			}
		}
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
			final HttpServletResponse res) {
		String cmd = req.getParameter("cmd");
		String p = req.getParameter("param");

		if (space != null) {
			if (cmd.equals("sig_merge")) {
				recalculateEvents(p);
				data = createSigMergeGraph();
			} else if (cmd.equals("org_ss")) {
				data = createStateSpaceGraph();
			} else if (cmd.equals("trans_diag")) {
				data = createTransitionDiagram(p);
			} else if (cmd.equals("d_sig_merge")) {
				data = createDottySignatureMerge();
			} else if (cmd.equals("d_trans_diag")) {
				data = createDottyTransitionDiagram(p);
			}
		}

	}

	private void recalculateEvents(final String p) {
		JsonElement parse = new JsonParser().parse(p);
		JsonArray array = parse.getAsJsonArray();
		boolean changed = false;
		for (JsonElement jsonElement : array) {
			JsonObject object = jsonElement.getAsJsonObject();
			String name = object.get("name").getAsString();
			boolean checked = object.get("checked").getAsBoolean();
			if (includedEvents.get(name).checked != checked) {
				includedEvents.put(name, new EnabledEvent(name, checked));
			}
		}
		updateSigMerge();
	}

	private void updateSigMerge() {
		// TODO Auto-generated method stub

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
		}
		resp.put("count", data.count());
		resp.put("varCount", data.varSize());
		resp.put("reset", data.getReset());
		resp.put("mode", data.getMode());
		resp.put("events", includedEvents.values());

		Gson g = new Gson();

		String json = g.toJson(resp);
		out.println(json);
		out.close();

		data.setReset(false);
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
		ApplySignatureMergeCommand cmd = new ApplySignatureMergeCommand();
		space.execute(cmd);
		SignatureMergedStateSpace s = new SignatureMergedStateSpace(space, cmd);
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
		DottySignatureMerge s = new DottySignatureMerge(space);

		AbstractData d = changeStateSpaceTo(s);
		d.setMode(4);
		return d;
	}

	private AbstractData createDottyTransitionDiagram(final String expression) {
		DottyTransitionDiagram s = new DottyTransitionDiagram(space, expression);

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
		if (space instanceof StateSpace) {
			((StateSpace) space).calculateVariables();
		}
		data.addNewLinks(space.getSSGraph(), newOps);
	}

	@Override
	public void apply(final Transformer styling) {
		data.addStyling(styling);
	}

	class EnabledEvent {
		public String name;
		public Boolean checked;

		public EnabledEvent(final String name, final Boolean enabled) {
			this.name = name;
			checked = enabled;
		}
	}
}
