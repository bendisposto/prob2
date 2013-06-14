package de.prob.webconsole.servlets.visualizations;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import de.prob.animator.command.ExpandFormulaCommand;
import de.prob.animator.command.InsertFormulaForVisualizationCommand;
import de.prob.animator.domainobjects.ExpandedFormula;
import de.prob.animator.domainobjects.FormulaId;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.StateSpace;
import de.prob.visualization.Transformer;

public class PredicateSession implements ISessionServlet,
		IAnimationChangeListener, IVisualizationServlet {

	private final IEvalElement formula;
	private final StateSpace stateSpace;
	private Trace currentTrace;
	private final FormulaId formulaId;
	private ExpandedFormula expanded;
	private final List<Transformer> styling = new ArrayList<Transformer>();
	private int count = 0;

	public PredicateSession(final AnimationSelector animations,
			final IEvalElement formula) {
		currentTrace = animations.getCurrentTrace();
		stateSpace = currentTrace.getStateSpace();
		this.formula = formula;
		InsertFormulaForVisualizationCommand cmd = new InsertFormulaForVisualizationCommand(
				formula);
		stateSpace.execute(cmd);
		formulaId = cmd.getFormulaId();
		calculate();
		animations.registerAnimationChangeListener(this);
	}

	@Override
	public void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws IOException {
		PrintWriter out = resp.getWriter();
		Map<String, Object> response = new HashMap<String, Object>();

		boolean getFormula = Boolean.valueOf(req.getParameter("getFormula"));

		if (getFormula) {
			response.put("data", expanded);
		}
		response.put("count", count);
		response.put("styling", styling);

		Gson g = new Gson();

		String json = g.toJson(response);
		out.println(json);
		out.close();
	}

	@Override
	public void traceChange(final Trace trace) {
		if (currentTrace != trace) {
			currentTrace = trace;
			calculate();
		}
	}

	public void calculate() {
		if (currentTrace != null && currentTrace.getStateSpace() == stateSpace) {
			ExpandFormulaCommand cmd = new ExpandFormulaCommand(formulaId,
					currentTrace.getCurrentState().getId());
			stateSpace.execute(cmd);
			expanded = cmd.getResult();
			count++;
		}
	}

	@Override
	public void apply(final Transformer styling) {
		this.styling.add(styling);
		count++;
	}

	public IEvalElement getFormula() {
		return formula;
	}
}
