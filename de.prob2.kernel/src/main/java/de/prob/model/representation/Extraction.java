package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.classicalb.ClassicalBInvariant;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains methods for tidy access to different types of {@link IEvalElement}s.
 */
public final class Extraction {

    private Extraction() {
    }

    public static List<IEvalElement> getInvariantPredicates(ClassicalBModel model) {
        List<IEvalElement> iEvalElements = new ArrayList<>();
        ModelElementList<ClassicalBInvariant> invariants = model.getMainMachine().getInvariants();
        if(invariants == null) {
        	return iEvalElements;
        }
        for (Invariant invariant : model.getMainMachine().getInvariants()) {
            iEvalElements.add(invariant.getPredicate());
        }
        return iEvalElements;
    }

    public static List<IEvalElement> getGuardPredicates(ClassicalBModel model, String operationName) {
        List<IEvalElement> iEvalElements = new ArrayList<>();
        Operation operation = model.getMainMachine().getOperation(operationName);
        if(operation == null || operation.getChildren() == null || operation.getChildren().get(Guard.class) == null) {
        	return iEvalElements;
        }
        for (AbstractElement guard : operation.getChildren().get(Guard.class)) {
            iEvalElements.add(((Guard) guard).getPredicate());
        }
        return iEvalElements;
    }
}
