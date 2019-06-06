package de.prob.animator.command;

import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.util.PrettyPrinter;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.exception.ProBError;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

import java.util.ArrayList;
import java.util.List;

/**
 * Calls the ProB core to find a feasible path of a list of transitions {@link #givenTransitions} that ends in a
 * state that satisfies a given predicate {@link #endPredicate}.
 */
public class FindTestPathCommand extends AbstractCommand implements IStateSpaceModifier {

    public enum ResultType {
        STATE_FOUND, NO_STATE_FOUND, INTERRUPTED, ERROR, TIME_OUT, INFEASIBLE_PATH
    }

    private static final String PROLOG_COMMAND_NAME = "prob2_find_test_path";
    private static final String RESULT_VARIABLE = "R";
    private static final int TIME_OUT = 200;

    private ResultType result;
    private List<Transition> transitions;
    private final List<String> givenTransitions;
    private final StateSpace stateSpace;
    private final IEvalElement endPredicate;

    public FindTestPathCommand(List<String> givenTransitions, final StateSpace stateSpace, final PPredicate endPredicate) {
        this.givenTransitions = givenTransitions;
        this.stateSpace = stateSpace;
        PrettyPrinter prettyPrinter = new PrettyPrinter();
        endPredicate.apply(prettyPrinter);
        this.endPredicate = new ClassicalB(prettyPrinter.getPrettyPrint(), FormulaExpand.EXPAND);
    }

    public ResultType getResult() {
        return result;
    }

    public boolean isFeasible() {
        return (result != ResultType.INFEASIBLE_PATH);
    }

    public List<Transition> getTransitions() {
        return this.transitions;
    }

    @Override
    public void writeCommand(IPrologTermOutput pto) {
        pto.openTerm(PROLOG_COMMAND_NAME);

        pto.openList();
        for (String event : givenTransitions) {
            pto.printAtom(event);
        }
        pto.closeList();

        endPredicate.printProlog(pto);

        pto.printNumber(TIME_OUT);
        pto.printVariable(RESULT_VARIABLE);
        pto.closeTerm();
    }

    @Override
    public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
        final PrologTerm resultTerm = bindings.get(RESULT_VARIABLE);
        if (resultTerm instanceof ListPrologTerm) {
        	ListPrologTerm list = (ListPrologTerm) resultTerm;
        	this.result = ResultType.STATE_FOUND;
        	List<Transition> transitions = new ArrayList<>();
        	for(PrologTerm prologTerm : list) {
        		transitions.add(Transition.createTransitionFromCompoundPrologTerm(stateSpace, (CompoundPrologTerm) prologTerm));
        	}
        	this.transitions = transitions;
        } else if (resultTerm.hasFunctor("errors", 1)) {
            this.result = ResultType.ERROR;
        } else if (resultTerm.hasFunctor("interrupted", 0)) {
            this.result = ResultType.INTERRUPTED;
        } else if (resultTerm.hasFunctor("timeout", 0)) {
            this.result = ResultType.TIME_OUT;
        } else if (resultTerm.hasFunctor("infeasible_path", 0)) {
            this.result = ResultType.INFEASIBLE_PATH;
        } else {
            throw new ProBError("unexpected result when trying to find a valid trace: " + resultTerm);
        }
    }


	@Override
	public List<Transition> getNewTransitions() {
		return transitions;
	}

    public Trace getTrace() {
        if(transitions.isEmpty()) {
            return null;
        }
        return stateSpace.getTrace(stateSpace.getRoot().getId()).addTransitions(transitions);
    }

}
