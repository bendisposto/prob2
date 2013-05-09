package de.prob.webconsole.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.animator.command.ApplySignatureMergeCommand;
import de.prob.animator.command.CalculateTransitionDiagramCommand;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.IStateSpace;
import de.prob.statespace.IStatesCalculatedListener;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.statespace.derived.AbstractDerivedStateSpace;
import de.prob.statespace.derived.AbstractDottyGraph;
import de.prob.statespace.derived.DottyTransitionDiagram;
import de.prob.statespace.derived.SignatureMergedStateSpace;
import de.prob.statespace.derived.TransitionDiagram;
import de.prob.visualization.AbstractData;
import de.prob.visualization.AnimationNotLoadedException;
import de.prob.visualization.DerivedStateSpaceData;
import de.prob.visualization.DottyData;
import de.prob.visualization.IVisualizationServlet;
import de.prob.visualization.StateSpaceData;
import de.prob.visualization.Transformer;
import de.prob.visualization.VisualizationSelector;

@SuppressWarnings("serial")
@Singleton
public class StateSpaceServlet extends HttpServlet implements
		IModelChangedListener, IStatesCalculatedListener, IVisualizationServlet {

	private static int sessionId = 0;

	public static String getSessionId() {
		return "space" + sessionId++;
	}

	private final Map<String, IStateSpace> spaces = new HashMap<String, IStateSpace>();
	private final Map<String, AbstractData> dataObjects = new HashMap<String, AbstractData>();
	private final Map<IStateSpace, Set<String>> sessionMap = new HashMap<IStateSpace, Set<String>>();
	private StateSpace currentStateSpace;
	private final VisualizationSelector visualizations;

	@Inject
	public StateSpaceServlet(final AnimationSelector animations,
			final VisualizationSelector visualizations) {
		this.visualizations = visualizations;
		visualizations.registerServlet(this, "State Space Visualizations");
		animations.registerModelChangedListener(this);
	}

	@Override
	public void doGet(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {
		if (req.getParameter("init") != null) {
			initializePage(req, res);
		} else if (req.getParameter("cmd") != null) {
			performCommand(req, res);
		} else if (req.getParameter("sessionId") != null) {
			normalResponse(req, res);
		} else {
			res.getWriter().close();
		}

	}

	private void performCommand(final HttpServletRequest req,
			final HttpServletResponse res) {
		String sId = req.getParameter("sessionId");
		String cmd = req.getParameter("cmd");
		String p = req.getParameter("param");

		if (cmd.equals("sig_merge") && dataObjects.containsKey(sId)) {
			createSigMergeGraph(sId);
		} else if (cmd.equals("org_ss") && dataObjects.containsKey(sId)) {
			IStateSpace ss = spaces.get(sId);
			if (ss instanceof AbstractDerivedStateSpace) {
				createStateSpaceGraph(sId,
						((AbstractDerivedStateSpace) ss).getStateSpace());
			}
		} else if (cmd.equals("trans_diag") && dataObjects.containsKey(sId)) {
			createTransitionDiagram(sId, p);
		} else if (cmd.equals("d_trans_diag") && dataObjects.containsKey(sId)) {
			createDottyTransitionDiagram(sId, p);
		}

	}

	private void initializePage(final HttpServletRequest req,
			final HttpServletResponse res) throws IOException {
		res.setContentType("text/html");

		String sId = req.getParameter("init");

		String html = "";
		if (dataObjects.containsKey(sId)) {
			html = HTMLResources.getSSVizHTML(sId + "");
		}

		PrintWriter out;

		out = res.getWriter();
		out.print(html);
		out.close();
	}

	private void normalResponse(final HttpServletRequest req,
			final HttpServletResponse res) throws IOException {
		PrintWriter out = res.getWriter();
		Map<String, Object> resp = new HashMap<String, Object>();

		String sessionId = req.getParameter("sessionId");
		Boolean getFormula = Boolean.valueOf(req.getParameter("getSS"));
		Boolean getAllStates = Boolean.valueOf(req.getParameter("getAll"));

		if (dataObjects.containsKey(sessionId)) {
			AbstractData data = dataObjects.get(sessionId);
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
		} else {
			resp.put("count", 0);
			resp.put("data", "");
			resp.put("varCount", 0);
			resp.put("reset", true);
			resp.put("mode", 0);
		}

		Gson g = new Gson();

		String json = g.toJson(resp);
		out.println(json);
		out.close();

		if (dataObjects.containsKey(sessionId)) {
			dataObjects.get(sessionId).setReset(false);
		}
	}

	@Override
	public void newTransitions(final IStateSpace s,
			final List<? extends OpInfo> newOps) {

		Set<String> sessIds = sessionMap.get(s);
		if (sessIds != null && !sessIds.isEmpty()) {
			if (s instanceof StateSpace) {
				((StateSpace) s).calculateVariables();
			}
			for (String id : sessIds) {
				final AbstractData d = dataObjects.get(id);
				d.addNewLinks(s.getSSGraph(), newOps);
			}
		}

	}

	@Override
	public void modelChanged(final StateSpace s) {
		if (s != null) {
			currentStateSpace = s;
			if (!sessionMap.containsKey(currentStateSpace)) {
				sessionMap.put(s, new HashSet<String>());
				s.registerStateSpaceListener(this);
			}
		}
	}

	public String openSession() throws AnimationNotLoadedException {
		if (currentStateSpace == null) {
			throw new AnimationNotLoadedException(
					"Could not start state space visualization because no animation is loaded");
		}
		String sId = getSessionId();
		createStateSpaceGraph(sId, currentStateSpace);
		visualizations.registerSession(sId + "", this);
		return sId + "";
	}

	public void closeSession(final String id) {
		IStateSpace s = spaces.get(id);
		sessionMap.get(s).remove(id);

		spaces.remove(s);
		dataObjects.remove(id);
	}

	private void calculateData(final IStateSpace s, final AbstractData d) {
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
	}

	public void createStateSpaceGraph(final String sessionId,
			final StateSpace space) {
		if (dataObjects.containsKey(sessionId)) {
			closeSession(sessionId);
		}

		spaces.put(sessionId, space);
		StateSpaceData d = new StateSpaceData(space);
		d.setReset(true);
		calculateData(space, d);
		dataObjects.put(sessionId, d);
		currentStateSpace.registerStateSpaceListener(this);
		sessionMap.get(space).add(sessionId);

	}

	public void createSigMergeGraph(final String sessionId) {
		IStateSpace iStateSpace = spaces.get(sessionId);
		ApplySignatureMergeCommand cmd = new ApplySignatureMergeCommand();
		iStateSpace.execute(cmd);
		SignatureMergedStateSpace space = new SignatureMergedStateSpace(
				iStateSpace, cmd);
		space.addStates(cmd.getStates());
		space.addTransitions(cmd.getOps());

		AbstractData d = changeStateSpaces(sessionId, iStateSpace, space);
		d.setMode(2);
	}

	public void createTransitionDiagram(final String sessionId,
			final String parameter) {
		IStateSpace iStateSpace = spaces.get(sessionId);
		CalculateTransitionDiagramCommand cmd = new CalculateTransitionDiagramCommand(
				parameter);
		iStateSpace.execute(cmd);
		TransitionDiagram space = new TransitionDiagram(iStateSpace, parameter,
				cmd);
		space.addStates(cmd.getStates());
		space.addTransitions(cmd.getOps());

		AbstractData d = changeStateSpaces(sessionId, iStateSpace, space);
		d.setMode(3);
	}

	public AbstractData changeStateSpaces(final String sessionId,
			final IStateSpace from, final IStateSpace to) {
		closeSession(sessionId);

		if (sessionMap.get(from).isEmpty()) {
			from.deregisterStateSpaceListener(this);
		}

		spaces.put(sessionId, to);
		AbstractData d = null;
		if (to instanceof AbstractDerivedStateSpace) {
			d = changeToDerivedSS(sessionId, from,
					(AbstractDerivedStateSpace) to);
		} else if (to instanceof AbstractDottyGraph) {
			d = changeToDottySS(sessionId, from, (AbstractDottyGraph) to);
		}
		dataObjects.put(sessionId, d);
		to.registerStateSpaceListener(this);
		if (!sessionMap.containsKey(to)) {
			sessionMap.put(to, new HashSet<String>());
		}
		sessionMap.get(to).add(sessionId);
		d.setReset(true);
		return d;
	}

	public AbstractData changeToDerivedSS(final String sessionId,
			final IStateSpace from, final AbstractDerivedStateSpace to) {
		AbstractData d = new DerivedStateSpaceData(to);
		calculateData(to, d);
		return d;
	}

	public AbstractData changeToDottySS(final String sessionId,
			final IStateSpace from, final AbstractDottyGraph to) {
		AbstractData d = new DottyData(to);
		return d;
	}

	@Override
	public void addUserDefinitions(final String id, final Transformer selection) {
		dataObjects.get(id).addStyling(selection);
	}

	public void createDottyTransitionDiagram(final String sessionId,
			final String expression) {
		IStateSpace iStateSpace = spaces.get(sessionId);
		DottyTransitionDiagram space = new DottyTransitionDiagram(iStateSpace,
				expression);

		AbstractData d = changeStateSpaces(sessionId, iStateSpace, space);
		d.setMode(4);
	}
}
