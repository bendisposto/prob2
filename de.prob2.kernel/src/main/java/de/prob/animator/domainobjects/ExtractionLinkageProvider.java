package de.prob.animator.domainobjects;

import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.representation.Guard;
import de.prob.model.representation.Invariant;

import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * Contains methods for tidy access to different types of {@link IEvalElement} and for combining the
 * elements.
 */
public class ExtractionLinkageProvider {

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

    public static IEvalElement conjoin(ArrayList<IEvalElement> elements) {
        StringJoiner stringJoiner = new StringJoiner(" & ");
        for (IEvalElement element : elements) {
            stringJoiner.add("(" + element.getCode() + ")");
        }
        return new ClassicalB(stringJoiner.toString(), FormulaExpand.EXPAND);
    }
}
