package de.prob.analysis;

import de.prob.animator.command.CbcSolveCommand;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.Operation;
import de.prob.model.representation.Guard;
import de.prob.model.representation.Invariant;
import de.prob.statespace.StateSpace;

import java.util.ArrayList;
import java.util.StringJoiner;

public class FeasibilityAnalysis {

    private ClassicalBModel model;
    private StateSpace stateSpace;

    public FeasibilityAnalysis(ClassicalBModel model, StateSpace stateSpace) {
        this.model = model;
        this.stateSpace = stateSpace;
    }

    private ArrayList<IEvalElement> getInvariantPredicates() {
        ArrayList<IEvalElement> iEvalElements = new ArrayList<>();
        for (Invariant invariant : model.getMainMachine().getInvariants()) {
            iEvalElements.add(invariant.getPredicate());
        }
        return iEvalElements;
    }

    private ArrayList<IEvalElement> getGuardPredicates(String operation) {
        ArrayList<IEvalElement> iEvalElements = new ArrayList<>();
        for (Object guard : model.getMainMachine().getOperation(operation).getChildren().get(Guard.class)) {
            iEvalElements.add(((Guard) guard).getPredicate());
        }
        return iEvalElements;
    }

    private ClassicalB conjoin(IEvalElement... elements) {
        StringJoiner stringJoiner = new StringJoiner(" & ");
        for (IEvalElement element : elements) {
            stringJoiner.add("(" + element.getCode() + ")");
        }
        return new ClassicalB(stringJoiner.toString(), FormulaExpand.EXPAND);
    }

    public ArrayList<String> analyseFeasibility() {
        ArrayList<String> infeasibleOperations = new ArrayList<>();
        ArrayList<IEvalElement> invariantPredicates = getInvariantPredicates();
        for (Operation operation : model.getMainMachine().getEvents()) {
            ArrayList<IEvalElement> iEvalElements = new ArrayList<>(invariantPredicates);
            iEvalElements.addAll(getGuardPredicates(operation.getName()));

            ClassicalB predicate = conjoin(iEvalElements.toArray(new IEvalElement[0]));
            CbcSolveCommand cmd = new CbcSolveCommand(predicate);
            stateSpace.execute(cmd);
            if (!(((EvalResult) cmd.getValue()).getValue().equals("TRUE"))) {
                infeasibleOperations.add(operation.getName());
            }
        }
        return infeasibleOperations;
    }
}
