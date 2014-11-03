package de.prob.bmotion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.ui.api.IllegalFormulaException;
import de.prob.ui.api.ImpossibleStepException;
import de.prob.ui.api.ToolRegistry;

public class CSPAnimation extends ProBAnimation {

	private final Map<String, IEvalResult> formulaCache = new HashMap<String, IEvalResult>();

	public CSPAnimation(final String toolId, final AbstractModel model,
			final AnimationSelector animations, final ToolRegistry toolRegistry) {
		super(toolId, model, animations, toolRegistry);
	}

	public CSPAnimation(final String toolId,
			final AnimationSelector animations, final ToolRegistry toolRegistry) {
		super(toolId, animations, toolRegistry);
	}

	@Override
	public String doStep(final String stateref, final String event,
			final String... parameters) throws ImpossibleStepException {
		try {
			Trace new_trace = trace.execute(event, Arrays.asList(parameters));
			animations.traceChange(new_trace);
			trace = new_trace;
			toolRegistry.notifyToolChange(BMotion.TRIGGER_ANIMATION_CHANGED,this);
		} catch (Exception e) {
			throw new ImpossibleStepException();
		}
		return trace.getCurrentState().getId();
	}

	@Override
	public Object evaluate(final String stateref, final String formula)
			throws IllegalFormulaException {
		if (trace == null) {
			return null;
		}
		if (!formulaCache.containsKey(formula)) {
			IEvalElement e = trace.getModel().parseFormula(formula);
			StateSpace space = trace.getStateSpace();
			StateId state = space.getState(stateref);
			if (state != null) {
				formulaCache.put(formula, state.eval(e));
			}
		}
		return formulaCache.get(formula);
	}

	@Override
	public List<String> getErrors(final String state, final String formula) {
		List<String> errors = new ArrayList<String>();
		if (trace != null) {
			try {
				IEvalElement e = trace.getModel().parseFormula(formula);
				StateSpace space = trace.getStateSpace();
				StateId state2 = space.getState(state);
				state2.eval(e);
			} catch (EvaluationException e) {
				errors.add("parse error : " + e.getMessage());
			} catch (Exception e) {
				errors.add("thrown " + e.getClass() + " because "
						+ e.getMessage());
			}
		}
		return errors;
	}

	@Override
	public void animatorStatus(final boolean busy) {
	}

	/*
	 * @Override public IBMotionTransformer getBMotionObserver(JsonElement
	 * jsonObserver) { return new CSPAnimationObserver(jsonObserver); }
	 */

}
