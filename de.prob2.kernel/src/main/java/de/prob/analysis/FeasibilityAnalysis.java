package de.prob.analysis;

import de.prob.animator.command.CbcSolveCommand;
import de.prob.animator.domainobjects.*;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.Operation;
import de.prob.model.representation.Extraction;
import de.prob.statespace.StateSpace;

import java.util.ArrayList;
import java.util.List;

public class FeasibilityAnalysis {

    private ClassicalBModel model;
    private StateSpace stateSpace;

    public FeasibilityAnalysis(ClassicalBModel model, StateSpace stateSpace) {
        this.model = model;
        this.stateSpace = stateSpace;
    }

    public List<String> analyseFeasibility() {
        List<String> infeasibleOperations = new ArrayList<>();
        List<IEvalElement> invariantPredicates = Extraction.getInvariantPredicates(model);
        for (Operation operation : model.getMainMachine().getEvents()) {
            List<IEvalElement> iEvalElements = new ArrayList<>(invariantPredicates);
            iEvalElements.addAll(Extraction.getGuardPredicates(model, operation.getName()));

            ClassicalB predicate = (ClassicalB) Join.conjunct(iEvalElements);
            CbcSolveCommand cmd = new CbcSolveCommand(predicate);
            stateSpace.execute(cmd);
            if (!(((EvalResult) cmd.getValue()).getValue().equals("TRUE"))) {
                infeasibleOperations.add(operation.getName());
            }
        }
        return infeasibleOperations;
    }
}
