package de.prob.animator.domainobjects;

import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * Contains methods for combining {@link IEvalElement}s.
 */
public class Join {

    public static IEvalElement conjunct(ArrayList<IEvalElement> elements) {
        StringJoiner stringJoiner = new StringJoiner(" & ");
        for (IEvalElement element : elements) {
            stringJoiner.add("(" + element.getCode() + ")");
        }
        return new ClassicalB(stringJoiner.toString(), FormulaExpand.EXPAND);
    }

    public static IEvalElement disjunct(ArrayList<IEvalElement> elements) {
        StringJoiner stringJoiner = new StringJoiner(" or ");
        for (IEvalElement element : elements) {
            stringJoiner.add("(" + element.getCode() + ")");
        }
        return new ClassicalB(stringJoiner.toString(), FormulaExpand.EXPAND);
    }
}
