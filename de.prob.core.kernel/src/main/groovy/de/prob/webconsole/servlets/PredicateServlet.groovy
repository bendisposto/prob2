package de.prob.webconsole.servlets;

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import com.google.gson.Gson
import com.google.inject.Inject
import com.google.inject.Singleton

import de.prob.animator.command.ExpandFormulaCommand
import de.prob.animator.command.InsertFormulaForVisualizationCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.ExpandedFormula
import de.prob.animator.domainobjects.FormulaId
import de.prob.statespace.AnimationSelector
import de.prob.statespace.History

@SuppressWarnings("serial")
@Singleton
public class PredicateServlet extends HttpServlet {

	public static int idNr = 0;

	public static String getNewId() {
		return "pred"+idNr++;
	}

	private final AnimationSelector animations;
	private final Map<String, FormulaId> formulas = new HashMap<String, FormulaId>();

	@Inject
	public PredicateServlet(final AnimationSelector animations) {
		this.animations = animations;
	}

	@Override
	public void doGet(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();

		String formula = req.getParameter("formula");

		History currentHistory = animations.getCurrentHistory();
		if (!formulas.containsKey(formula)) {
			InsertFormulaForVisualizationCommand cmd = new InsertFormulaForVisualizationCommand(
					new ClassicalB(formula));
			currentHistory.getS().execute(cmd);
			formulas.put(formula, cmd.getFormulaId());
		}

		ExpandFormulaCommand cmd = new ExpandFormulaCommand(
				formulas.get(formula), currentHistory.getCurrentState().getId());
		currentHistory.getS().execute(cmd);

		ExpandedFormula expandedFormula = cmd.getResult();

		Gson g = new Gson();
		String json = g.toJson(expandedFormula);
		out.println(json);
		out.close();
	}
}
