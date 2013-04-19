package de.prob.webconsole.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

import de.prob.animator.domainobjects.OpInfo;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.IStatesCalculatedListener;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.statespace.StateSpaceGraph;
import de.prob.visualization.AnimationNotLoadedException;

@SuppressWarnings("serial")
@Singleton
public class StateSpaceServlet extends HttpServlet implements
		IModelChangedListener, IStatesCalculatedListener {

	private static int sessionId = 0;

	public static int getSessionId() {
		return sessionId++;
	}

	private final List<StateSpace> spaces = new ArrayList<StateSpace>();
	private final List<StateSpaceData> dataObjects = new ArrayList<StateSpaceData>();
	private final Map<StateSpace, Set<Integer>> sessionMap = new HashMap<StateSpace, Set<Integer>>();
	private StateSpace currentStateSpace;

	@Inject
	public StateSpaceServlet(final AnimationSelector animations) {
		animations.registerModelChangedListener(this);
	}

	@Override
	public void doGet(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {
		if (req.getParameter("init") != null) {
			initializePage(req, res);
		} else if (req.getParameter("sessionId") != null) {
			normalResponse(req, res);
		} else {
			res.getWriter().close();
		}

	}

	private void initializePage(final HttpServletRequest req,
			final HttpServletResponse res) throws IOException {
		res.setContentType("text/html");

		int sId = Integer.parseInt(req.getParameter("init"));

		String html = "";
		if (sId >= 0 && sId < spaces.size() && spaces.get(sId) != null) {
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

		int sessionId = Integer.parseInt(req.getParameter("sessionId"));
		Boolean getFormula = Boolean.valueOf(req.getParameter("getSS"));
		Boolean getAllStates = Boolean.valueOf(req.getParameter("getAll"));

		if (sessionId >= 0 && sessionId < spaces.size()
				&& spaces.get(sessionId) != null) {
			if (getFormula) {
				if (getAllStates) {
					resp.put("data", dataObjects.get(sessionId).getData());
				} else {
					resp.put("data", dataObjects.get(sessionId).getChanges());
				}
			}
			resp.put("count", dataObjects.get(sessionId).count());
		} else {
			resp.put("count", 0);
			resp.put("data", "");
		}

		Gson g = new Gson();

		String json = g.toJson(resp);
		out.println(json);
		out.close();
	}

	@Override
	public void newTransitions(final StateSpaceGraph s,
			final List<OpInfo> newOps) {
		Set<Integer> sessIds = sessionMap.get(s);
		if (sessIds != null) {
			for (Integer integer : sessIds) {
				StateSpaceData d = dataObjects.get(integer);
				d.addNewLinks(s, newOps);
			}
		}

	}

	@Override
	public void modelChanged(final StateSpace s) {
		if (s != null && !sessionMap.containsKey(currentStateSpace)) {
			sessionMap.put(currentStateSpace, new HashSet<Integer>());
		}
		currentStateSpace = s;
		s.registerStateSpaceListener(this);
	}

	public String openSession() throws AnimationNotLoadedException {
		if (currentStateSpace == null) {
			throw new AnimationNotLoadedException(
					"Could not start state space visualization because no animation is loaded");
		}
		int sId = getSessionId();
		spaces.add(currentStateSpace);
		StateSpaceData d = new StateSpaceData();
		calculateData(currentStateSpace, d);
		dataObjects.add(d);
		currentStateSpace.registerStateSpaceListener(this);
		sessionMap.get(currentStateSpace).add(sId);

		return sId + "";
	}

	public void closeSession(final String id) {
		int iD = Integer.parseInt(id);
		StateSpace s = spaces.get(iD);
		sessionMap.get(s).remove(iD);

		spaces.set(iD, null);
		dataObjects.set(iD, null);
	}

	private void calculateData(final StateSpace s, final StateSpaceData d) {
		Collection<StateId> vertices = s.getGraph().getVertices();
		for (StateId stateId : vertices) {
			d.addNode(stateId);
		}

		Collection<OpInfo> edges = s.getGraph().getEdges();
		for (OpInfo opInfo : edges) {
			d.addLink(opInfo);
		}
	}
}
