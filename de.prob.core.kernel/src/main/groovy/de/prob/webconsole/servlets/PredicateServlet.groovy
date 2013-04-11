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

@Singleton
class PredicateServlet extends HttpServlet implements IHistoryChangeListener {

	def static int idNr = 0

	def static String getNewId() {
		return "pred"+idNr++
	}

	def final AnimationSelector animations
	def Map<String,Session> sessions = new HashMap<String, Session>()
	def Map<String, ExpandedFormula> formulas = new HashMap<String, ExpandedFormula>()
	def History history

	@Inject
	def PredicateServlet(final AnimationSelector animations) {
		this.animations = animations
		animations.registerHistoryChangeListener(this)
	}

	@Override
	def void doGet(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {

		def init = req.getParameter("init")
		if(init != null) {
			initializePage(req, res)
		} else {
			normalResponse(req, res)
		}
	}

	def initializePage(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html")

		def sId = req.getParameter("init")

		PrintWriter out = res.getWriter()
		String html = HTMLResources.pred_head+"\'"+sId+"\'"+HTMLResources.pred_tail

		out.print(html)
		out.close();
	}

	def normalResponse(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter()
		def resp = [:]

		String sessionId = req.getParameter("sessionId")
		def getFormula = req.getParameter("getFormula")

		if(getFormula) {
			resp["data"] = formulas.get(sessionId)
		}

		resp["count"] = sessions.get(sessionId).count

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
		if(history.getS() == s.stateSpace) {
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

		def sessionId = "pred1"
		//def sessionId = getNewId()

		def sess = new Session(sessionId,s,formulaId)
		sessions[sessionId] = sess
		calculateSession(sess)

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
		changed.remove(sessionId);
	}
}
