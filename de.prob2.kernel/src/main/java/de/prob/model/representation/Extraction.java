package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.classicalb.ClassicalBInvariant;
import de.prob.model.classicalb.ClassicalBModel;

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

    public static List<IEvalElement> getGuardPredicates(ClassicalBModel model, String operation) {
        List<IEvalElement> iEvalElements = new ArrayList<>();
        Map<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> map = model.getMainMachine().getOperation(operation).getChildren();
        if(map == null || map.get(Guard.class) == null) {
        	return iEvalElements;
        }
        for (AbstractElement guard : map.get(Guard.class)) {
            iEvalElements.add(((Guard) guard).getPredicate());
        }
        return iEvalElements;
    }
}
