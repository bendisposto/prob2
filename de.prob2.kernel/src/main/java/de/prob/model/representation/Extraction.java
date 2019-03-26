package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.classicalb.ClassicalBModel;

import java.util.ArrayList;

/**
 * Contains methods for tidy access to different types of {@link IEvalElement}s.
 */
public class Extraction {

    public static ArrayList<IEvalElement> getInvariantPredicates(ClassicalBModel model) {
        ArrayList<IEvalElement> iEvalElements = new ArrayList<>();
        for (Invariant invariant : model.getMainMachine().getInvariants()) {
            iEvalElements.add(invariant.getPredicate());
        }
        return iEvalElements;
    }

    public static ArrayList<IEvalElement> getGuardPredicates(ClassicalBModel model, String operation) {
        ArrayList<IEvalElement> iEvalElements = new ArrayList<>();
        for (Object guard : model.getMainMachine().getOperation(operation).getChildren().get(Guard.class)) {
            iEvalElements.add(((Guard) guard).getPredicate());
        }
        return iEvalElements;
    }
}
