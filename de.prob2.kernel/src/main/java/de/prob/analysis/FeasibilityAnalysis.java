package de.prob.analysis;

import de.prob.animator.command.CbcSolveCommand;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.ExtractionLinkageProvider;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.Operation;
import de.prob.statespace.StateSpace;

import java.util.ArrayList;

public class FeasibilityAnalysis {

    private ClassicalBModel model;
    private StateSpace stateSpace;

    public FeasibilityAnalysis(ClassicalBModel model, StateSpace stateSpace) {
        this.model = model;
        this.stateSpace = stateSpace;
    }

    public ArrayList<String> analyseFeasibility() {
        ArrayList<String> infeasibleOperations = new ArrayList<>();
        ArrayList<IEvalElement> invariantPredicates = ExtractionLinkageProvider.getInvariantPredicates(model);
        for (Operation operation : model.getMainMachine().getEvents()) {
            ArrayList<IEvalElement> iEvalElements = new ArrayList<>(invariantPredicates);
            iEvalElements.addAll(ExtractionLinkageProvider.getGuardPredicates(model, operation.getName()));

            ClassicalB predicate = (ClassicalB) ExtractionLinkageProvider.conjoin(iEvalElements);
            CbcSolveCommand cmd = new CbcSolveCommand(predicate);
            stateSpace.execute(cmd);
            if (!(((EvalResult) cmd.getValue()).getValue().equals("TRUE"))) {
                infeasibleOperations.add(operation.getName());
            }
        }
        return infeasibleOperations;
    }
}
