package de.prob.bmotion;

import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.ui.api.IllegalFormulaException;
import de.prob.ui.api.ImpossibleStepException;
import de.prob.ui.api.ToolRegistry;

import java.util.*;

public class BAnimation extends ProBAnimation {

    private final Map<String, IEvalElement> formulas = new HashMap<String, IEvalElement>();

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
        if (trace == null)
            return null;
        try {
            Trace new_trace = trace.execute(event, Arrays.asList(parameters));
            animations.traceChange(new_trace);
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

    /*@Override
    public BMotionObserver getBMotionObserver(JsonElement jsonObserver) {
        return new BAnimationObserver(jsonObserver);
    }*/

}
