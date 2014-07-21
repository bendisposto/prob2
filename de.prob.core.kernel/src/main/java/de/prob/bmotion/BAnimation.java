package de.prob.bmotion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;

import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.ui.api.ITool;
import de.prob.ui.api.IllegalFormulaException;
import de.prob.ui.api.ImpossibleStepException;
import de.prob.ui.api.ToolRegistry;

public class BAnimation implements ITool, IAnimationChangeListener, IObserver {

	private Trace trace;
	private final String modelPath;
	Map<String, IEvalElement> formulas = new HashMap<String, IEvalElement>();
	private final ToolRegistry toolRegistry;
	private final AnimationSelector animations;

	public BAnimation(final AbstractModel model,
			final AnimationSelector animations, final ToolRegistry toolRegistry) {
		this.animations = animations;
		this.toolRegistry = toolRegistry;
		this.modelPath = model.getModelFile().getAbsolutePath();
		trace = new Trace(model);
		animations.registerAnimationChangeListener(this);
		animations.addNewAnimation(trace);
		toolRegistry.register(getName(), this);
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
		return res != null ? res.toString() : null;
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
		return modelPath;
	}

	@Override
	public void traceChange(final Trace currentTrace,
			final boolean currentAnimationChanged) {
		if (currentTrace != null
				&& currentTrace.getModel().getModelFile().getAbsolutePath()
						.equals(modelPath) && !currentTrace.equals(trace)) {
			trace = currentTrace;
			toolRegistry.notifyToolChange(this);
		} else if (currentTrace == null) {
			trace = currentTrace;
		}
	}

	@Override
	public void animatorStatus(final boolean busy) {
	}

	@Override
	public IBMotionGroovyObserver getBMotionGroovyObserver(
			BMotionStudioSession bmsSession, JsonElement jsonObserver) {
		return new BAnimationObserver(bmsSession, jsonObserver);
	}

}
