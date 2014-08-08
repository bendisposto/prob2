package de.prob.bmotion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;

import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.ui.api.IllegalFormulaException;
import de.prob.ui.api.ImpossibleStepException;
import de.prob.ui.api.ToolRegistry;

public class CSPAnimation extends ProBAnimation {

	private final Map<String, IEvalResult> formulaCache = new HashMap<String, IEvalResult>();

	public CSPAnimation(String toolId, AbstractModel model, AnimationSelector animations,
			ToolRegistry toolRegistry) {
		super(toolId, model, animations, toolRegistry);
	}
	
	public CSPAnimation(String toolId, AnimationSelector animations,
			ToolRegistry toolRegistry) {
		super(toolId, animations, toolRegistry);
	}

	@Override
	public String doStep(final String stateref, final String event,
			final String... parameters) throws ImpossibleStepException {
		try {
			Trace new_trace = trace.execute(event, Arrays.asList(parameters));
			animations.replaceTrace(trace, new_trace);
			trace = new_trace;
			toolRegistry.notifyToolChange(this);
		} catch (Exception e) {
			throw new ImpossibleStepException();
		}
		return trace.getCurrentState().getId();
	}

	@Override
	public Object evaluate(final String stateref, final String formula)
			throws IllegalFormulaException {
		if(trace == null)
			return null;
		if (!formulaCache.containsKey(formula)) {
			IEvalElement e = trace.getModel().parseFormula(formula);
			StateSpace space = trace.getStateSpace();
			List<IEvalResult> results = space.eval(space.getVertex(stateref),
					Arrays.asList(e));
			formulaCache.put(formula, results.get(0));
		}
		IEvalResult res = formulaCache.get(formula);
		if (res instanceof EvalResult) {
			return ((EvalResult) res).getValue();
		}
		return res.toString();
	}

	@Override
	public List<String> getErrors(final String state, final String formula) {
		List<String> errors = new ArrayList<String>();
		if (trace != null) {
			try {
				IEvalElement e = trace.getModel().parseFormula(formula);
				StateSpace space = trace.getStateSpace();
				space.eval(space.getVertex(state), Arrays.asList(e));
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

	@Override
	public IBMotionGroovyObserver getBMotionGroovyObserver(
			BMotionStudioSession bmsSession, JsonElement jsonObserver) {
		return new CSPAnimationObserver(bmsSession, jsonObserver);
	}

	@Override
	public String getModelData(String dataParameter, HttpServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

}
