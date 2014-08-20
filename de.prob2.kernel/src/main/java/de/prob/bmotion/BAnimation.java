package de.prob.bmotion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBVariable;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.ui.api.IllegalFormulaException;
import de.prob.ui.api.ImpossibleStepException;
import de.prob.ui.api.ToolRegistry;

public class BAnimation extends ProBAnimation {

	private final Map<String, IEvalElement> formulas = new HashMap<String, IEvalElement>();

	private final Gson gson = new Gson();

	protected StateSpace currentStatespace;

	public BAnimation(String toolId, AbstractModel model, AnimationSelector animations,
			ToolRegistry toolRegistry) {
		super(toolId, model, animations, toolRegistry);
	}
	
	public BAnimation(String toolId, AnimationSelector animations,
			ToolRegistry toolRegistry) {
		super(toolId, animations, toolRegistry);
	}

	@Override
	public String doStep(final String stateref, final String event,
			final String... parameters) throws ImpossibleStepException {
		if(trace == null)
			return null;
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
		if (trace == null)
			return null;
		StateSpace space = trace.getStateSpace();
		IEvalElement e = formulas.get(formula);
		if (e == null) {
			e = trace.getModel().parseFormula(formula);
			formulas.put(formula, e);
		}
		space.subscribe(this, e);
		StateId sId = space.getVertex(stateref);
		return space.valuesAt(sId).get(formulas.get(formula));
	}

	@Override
	public List<String> getErrors(final String state, final String formula) {
		List<String> errors = new ArrayList<String>();
		if (trace != null) {
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
		}
		return errors;
	}

	@Override
	public void animatorStatus(final boolean busy) {
	}

	@Override
	public IBMotionGroovyObserver getBMotionGroovyObserver(
			BMotionStudioSession bmsSession, JsonElement jsonObserver) {
		return new BAnimationObserver(bmsSession, jsonObserver);
	}

	@Override
	public String getModelData(String dataParameter, HttpServletRequest req) {

		List<XEditableListObj> l = new ArrayList<XEditableListObj>();
		AbstractModel model = getModel();
		if (model != null) {
			AbstractElement mainComponent = model.getMainComponent();

			if (mainComponent instanceof EventBMachine) {
				if ("operations".equals(dataParameter)) {
					for (Event event : ((EventBMachine) mainComponent)
							.getEvents()) {
						l.add(new XEditableListObj(event.getName(), event
								.getName()));
					}
				} else if ("variables".equals(dataParameter)) {
					for (EventBVariable var : ((EventBMachine) mainComponent)
							.getVariables()) {
						l.add(new XEditableListObj(var.getName(), var.getName()));
					}
				}
			}
		}
		return gson.toJson(l);

	}

	private class XEditableListObj {

		@SuppressWarnings("unused")
		private String text;
		@SuppressWarnings("unused")
		private String value;

		public XEditableListObj(String text, String value) {
			this.text = text;
			this.value = value;
		}

	}

}
