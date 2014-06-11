package de.prob.bmotion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.ui.api.ITool;
import de.prob.ui.api.IllegalFormulaException;
import de.prob.ui.api.ImpossibleStepException;

public class BAnimation implements ITool {

	private Trace trace;
	Map<String, IEvalElement> formulas = new HashMap<String, IEvalElement>();

	public BAnimation(final Trace trace) {
		this.trace = trace;
	}

	@Override
	public String doStep(final String stateref, final String event,
			final String... parameters) throws ImpossibleStepException {
		trace = trace.execute(event, Arrays.asList(parameters));
		return trace.getCurrentState().getId();
	}

	@Override
	public String evaluate(final String stateref, final String formula)
			throws IllegalFormulaException {
		StateSpace space = trace.getStateSpace();
		if (!formulas.containsKey(formula)) {
			IEvalElement e = trace.getModel().parseFormula(formula);
			space.subscribe(this, e);
			formulas.put(formula, e);
		}
		StateId sId = space.getVertex(stateref);
		IEvalResult res = space.valuesAt(sId).get(formulas.get(formula));
		if (res instanceof EvalResult) {
			return ((EvalResult) res).getValue();
		}
		return res.toString();
	}

	@Override
	public List<String> getErrors(final String state, final String formula) {
		List<String> errors = new ArrayList<String>();
		try {
			IEvalElement e = trace.getModel().parseFormula(formula);
			StateSpace space = trace.getStateSpace();
			StateId sId = space.getVertex(state);
			if (!space.canBeEvaluated(sId)) {
				errors.add("State not initialized");
			}
			space.eval(sId, Arrays.asList(e));
		} catch (EvaluationException e) {
			errors.add("Could not parse: " + e.getMessage());
		} catch (Exception e) {
			errors.add(e.getClass() + " thrown: " + e.getMessage());
		}
		return errors;
	}

	@Override
	public String getCurrentState() {
		return trace.getCurrentState().getId();
	}

	@Override
	public boolean canBacktrack() {
		return true;
	}

	@Override
	public String getName() {
		return trace.getUUID().toString();
	}

}
