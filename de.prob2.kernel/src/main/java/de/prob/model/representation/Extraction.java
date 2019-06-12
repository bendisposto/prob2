package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.classicalb.ClassicalBModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains methods for tidy access to different types of {@link IEvalElement}s.
 */
public final class Extraction {

    private Extraction() {
    }

    public static List<IEvalElement> getInvariantPredicates(ClassicalBModel model) {
        List<IEvalElement> iEvalElements = new ArrayList<>();
        for (Invariant invariant : model.getMainMachine().getInvariants()) {
            iEvalElements.add(invariant.getPredicate());
        }
        return iEvalElements;
    }

    public static List<IEvalElement> getGuardPredicates(ClassicalBModel model, String operation) {
        List<IEvalElement> iEvalElements = new ArrayList<>();
        //TODO: Fix possible NPE
        for (AbstractElement guard : model.getMainMachine().getOperation(operation).getChildren().get(Guard.class)) {
            iEvalElements.add(((Guard) guard).getPredicate());
        }
        return iEvalElements;
    }
}
