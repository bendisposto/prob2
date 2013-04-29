package de.prob.webconsole.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.IHistoryChangeListener;
import de.prob.statespace.StateSpace;
import de.prob.visualization.AnimationNotLoadedException;
import de.prob.visualization.IVisualizationServlet;
import de.prob.visualization.Transformer;
import de.prob.visualization.VisualizationSelector;

@SuppressWarnings("serial")
@Singleton
public class FormulaOverHistoryServlet extends HttpServlet implements
		IHistoryChangeListener, IVisualizationServlet {

	public static int idNr = 0;

	public static String getNewId() {
		return "value" + idNr++;
	}

	private final AnimationSelector animations;
	private final Map<String, Session> sessions = new HashMap<String, Session>();
	private final Map<String, List<Object>> dataSets = new HashMap<String, List<Object>>();
	private final Map<String, List<Transformer>> userOptions = new HashMap<String, List<Transformer>>();
	private History history;
	private final VisualizationSelector visualizations;

	@Inject
	public FormulaOverHistoryServlet(final AnimationSelector animations,
			final VisualizationSelector visualizations) {
		this.animations = animations;
		this.visualizations = visualizations;
		animations.registerHistoryChangeListener(this);
		visualizations.registerServlet(this, "Value over Time Visualizations");
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

		String sId = req.getParameter("init");

		String html = "";
		if (sessions.containsKey(sId)) {
			html = HTMLResources.getValueVsTimeHTML(sId);
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
		Boolean getFormula = Boolean.valueOf(req.getParameter("getFormula"));

		if (sessions.containsKey(sessionId)) {
			if (getFormula) {
				resp.put("data", dataSets.get(sessionId));
			}
			resp.put("count", sessions.get(sessionId).count);
			resp.put("attrs", userOptions.get(sessionId));
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
	public void historyChange(final History history) {
		this.history = history;

		for (Session s : sessions.values()) {
			calculateSession(s);
		}

	}

	public void calculateSession(final Session session) {
		if (history != null && history.getS() == session.stateSpace) {
			List<EvaluationResult> results = history.eval(session.formula);
			List<Object> result = new ArrayList<Object>();

			int c = 0;
			for (EvaluationResult it : results) {
				result.add(new Element(it.getStateId(), c, it.getValue()));
				result.add(new Element(it.getStateId(), c + 1, it.getValue()));
				c++;
			}

			dataSets.put(session.sessionId, result);
			session.inc();
		}
	}

	public String openSession(final IEvalElement formula)
			throws AnimationNotLoadedException {
		if (history == null) {
			throw new AnimationNotLoadedException("Could not visualize "
					+ formula.getCode() + " because no animation is loaded");
		}
		StateSpace s = history.getStatespace();
		String sessionId = getNewId();

		Session session = new Session(sessionId, s, formula);
		sessions.put(sessionId, session);
		calculateSession(session);
		visualizations.registerSession(sessionId, this);

		return sessionId;
	}

	public void closeSession(final String sessionId) {
		sessions.remove(sessionId);
		dataSets.remove(sessionId);
	}

	private class Element {
		public final String stateid;
		public final Integer value;
		public final Integer t;

		public Element(final String string, final int t, final Object value) {
			stateid = string;
			this.t = t;
			this.value = Integer.parseInt((String) value);
		}
	}

	private class Session {

		public final String sessionId;
		public final StateSpace stateSpace;
		public final IEvalElement formula;
		public int count = 0;

		public Session(final String sessionId, final StateSpace stateSpace,
				final IEvalElement formula) {
			this.sessionId = sessionId;
			this.stateSpace = stateSpace;
			this.formula = formula;
		}

		public void inc() {
			count++;
		}
	}

	@Override
	public void addUserDefinitions(final String id, final Transformer selection) {
		Session session = sessions.get(id);
		session.inc();
		if (!userOptions.containsKey(id)) {
			userOptions.put(id, new ArrayList<Transformer>());
		}
		userOptions.get(id).add(selection);

	}

}
