package de.prob.webconsole.servlets

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import com.google.gson.Gson
import com.google.inject.Inject
import com.google.inject.Singleton

import de.prob.animator.command.ExpandFormulaCommand
import de.prob.animator.command.InsertFormulaForVisualizationCommand
import de.prob.animator.domainobjects.ExpandedFormula
import de.prob.animator.domainobjects.IEvalElement
import de.prob.statespace.AnimationSelector
import de.prob.statespace.History
import de.prob.statespace.IHistoryChangeListener
import de.prob.statespace.StateSpace
import de.prob.visualization.AnimationNotLoadedException
import de.prob.visualization.IVisualizationServlet
import de.prob.visualization.Transformer
import de.prob.visualization.VisualizationSelector

@Singleton
class PredicateServlet extends HttpServlet implements IHistoryChangeListener, IVisualizationServlet {

	def static int idNr = 0

	def static String getNewId() {
		return "pred"+idNr++
	}

	def final AnimationSelector animations
	def Map<String,Session> sessions = new HashMap<String, Session>()
	def Map<String, ExpandedFormula> formulas = new HashMap<String, ExpandedFormula>()
	def Map<String, List<Transformer>> userOptions = new HashMap<String, List<Transformer>>()
	def History history
	private final VisualizationSelector visualizations;

	@Inject
	def PredicateServlet(final AnimationSelector animations, final VisualizationSelector visualizations) {
		this.visualizations = visualizations;
		this.animations = animations
		animations.registerHistoryChangeListener(this)
		visualizations.registerServlet(this, "Predicate Visualizations");
	}

	@Override
	def void doGet(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {

		def init = req.getParameter("init")
		if(init != null) {
			initializePage(req, res)
		} else if(req.getParameter("sessionId") != null) {
			normalResponse(req, res)
		} else {
			res.getWriter().close();
		}
	}

	def initializePage(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html")

		def sId = req.getParameter("init")

		String html = ""
		if(sessions.containsKey(sId)) {
			html = HTMLResources.getPredicateHTML(sId);
		}

		PrintWriter out = res.getWriter()
		out.print(html)
		out.close();
	}

	def normalResponse(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter()
		def resp = [:]

		String sessionId = req.getParameter("sessionId")
		def getFormula = req.getParameter("getFormula")

		if(sessions.containsKey(sessionId)) {
			if(getFormula) {
				resp["data"] = formulas.get(sessionId)
			}
			resp["count"] = sessions.get(sessionId).count
			resp["attrs"] = userOptions.get(sessionId)
		} else {
			resp["count"] = 0
			resp["data"] = ""
		}

		Gson g = new Gson()

		String json = g.toJson(resp)
		out.println(json)
		out.close()
	}

	@Override
	def void historyChange(History history) {
		this.history = history

		sessions.each {
			calculateSession(it.getValue())
		}
	}

	def calculateSession(Session s) {
		if(history != null && history.getS() == s.stateSpace) {
			ExpandFormulaCommand cmd = new ExpandFormulaCommand(s.formulaId, history.getCurrentState().getId())
			s.stateSpace.execute(cmd)
			formulas[s.sessionId] = cmd.getResult()
			s.inc()
		}
	}


	def String openSession(IEvalElement formula) throws AnimationNotLoadedException {
		if(history == null) {
			throw new AnimationNotLoadedException("Could not visualize ${formula.getCode()} because no animation is loaded")
		}
		StateSpace s = history.getStatespace()
		def cmd = new InsertFormulaForVisualizationCommand(formula)
		s.execute(cmd)
		def formulaId = cmd.getFormulaId()

		def sessionId = getNewId()

		def sess = new Session(sessionId,s,formulaId)
		sessions[sessionId] = sess
		userOptions[sessionId] = []
		calculateSession(sess)
		visualizations.registerSession(sessionId, this)

		return sessionId
	}

	class Session {

		def final sessionId
		def final stateSpace
		def final formulaId
		def count = 0

		def Session(def sessionId, def stateSpace, def formulaId) {
			this.formulaId = formulaId
			this.stateSpace = stateSpace
			this.sessionId = sessionId
		}

		def inc() {
			count++;
		}
	}

	def closeSession(String sessionId) {
		sessions.remove(sessionId);
		formulas.remove(sessionId);
	}

	@Override
	public void addUserDefinitions(String id, Transformer selection) {
		Session session = sessions.get(id);
		session.inc();
		if (!userOptions.containsKey(id)) {
			userOptions.put(id, new ArrayList<Transformer>());
		}
		userOptions.get(id).add(selection);
	}
}
