package de.prob.webconsole.servlets;

import java.awt.Dimension;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import de.prob.visualization.AnimationNotLoadedException;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;

@SuppressWarnings("serial")
@Singleton
public class StateSpaceServlet extends HttpServlet implements
		IModelChangedListener, IStatesCalculatedListener {

	private final List<StateSpace> spaces = new ArrayList<StateSpace>();
	private final List<StateSpaceData> dataObjects = new ArrayList<StateSpaceData>();
	private final AnimationSelector animations;
	private StateSpace currentStateSpace;

	@Inject
	public StateSpaceServlet(final AnimationSelector animations) {
		this.animations = animations;
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

		if (sessionId >= 0 && sessionId < spaces.size()
				&& spaces.get(sessionId) != null) {
			if (getFormula) {
				resp.put("data", dataObjects.get(sessionId).getData());
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
	public void newTransitions(final StateSpace s, final List<OpInfo> newOps) {
		StateSpaceData d = dataObjects.get(spaces.indexOf(s));
		d.addNewLinks(s, newOps);
	}

	@Override
	public void modelChanged(final StateSpace s) {
		currentStateSpace = s;
		if (!spaces.contains(s)) {
			spaces.add(s);
			StateSpaceData d = new StateSpaceData();
			calculateData(s, d);
			dataObjects.add(d);
			s.registerStateSpaceListener(this);
		}
	}

	public String openSession() throws AnimationNotLoadedException {
		if (currentStateSpace == null) {
			throw new AnimationNotLoadedException(
					"Could not start state space visualization because no animation is loaded");
		}
		return spaces.indexOf(currentStateSpace) + "";
	}

	public void closeSession(final String id) {
		int iD = Integer.parseInt(id);
		spaces.set(iD, null);
		dataObjects.set(iD, null);
	}

	private void calculateData(final StateSpace s, final StateSpaceData d) {
		SpringLayout<StateId, OpInfo> layout = new SpringLayout<StateId, OpInfo>(
				s.getGraph());

		Dimension preferredSize = new Dimension(1000, 1500);
		layout.setSize(preferredSize);

		layout.initialize();
		Collection<StateId> vertices = layout.getGraph().getVertices();
		for (StateId stateId : vertices) {
			d.addNode(0, 0, stateId);
			// Point2D transform = layout.transform(stateId);
			// d.addNode((int) (transform.getX()), (int) (transform.getY()),
			// stateId);
		}

		Collection<OpInfo> edges = layout.getGraph().getEdges();
		for (OpInfo opInfo : edges) {
			d.addLink(opInfo);
		}
	}
}
