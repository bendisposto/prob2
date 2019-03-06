package de.prob.animator.command;

import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.util.PrettyPrinter;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.exception.ProBError;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Transition;

import java.util.List;
import java.util.stream.Collectors;

public class FindTestPathCommand extends AbstractCommand {

    public enum ResultType {
        STATE_FOUND, NO_STATE_FOUND, INTERRUPTED, ERROR, TIME_OUT, INFEASIBLE_PATH
    }

    private static final String PROLOG_COMMAND_NAME = "prob2_find_test_path";
    private static final String RESULT_VARIABLE = "R";
    private static final int TIME_OUT = 200;

    private ResultType result;
    private List<Transition> transitions;
    private final List<String> events;
    private final StateSpace stateSpace;
    private final IEvalElement endPredicate;

    public FindTestPathCommand(List<String> events, final StateSpace stateSpace, final PPredicate endPredicate) {
        this.events = events;
        this.stateSpace = stateSpace;
        PrettyPrinter prettyPrinter = new PrettyPrinter();
        endPredicate.apply(prettyPrinter);
        this.endPredicate = new ClassicalB(prettyPrinter.getPrettyPrint(), FormulaExpand.EXPAND);
    }

    public ResultType getResult() {
        return result;
    }

    public boolean isFeasible() {
        return !(result == ResultType.INFEASIBLE_PATH);
    }

    public List<Transition> getTransitions() {
        return this.transitions;
    }

    @Override
    public void writeCommand(IPrologTermOutput pto) {
        pto.openTerm(PROLOG_COMMAND_NAME);

        pto.openList();
        for (String event : events) {
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
            transitions = BindingGenerator.getList(resultTerm).stream()
                    .map(pt -> Transition.createTransitionFromCompoundPrologTerm(
                            stateSpace, BindingGenerator.getCompoundTerm(pt, 4)))
                    .collect(Collectors.toList());
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
}
