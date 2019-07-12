package de.prob.animator.domainobjects;

import java.util.List;
import java.util.StringJoiner;

/**
 * Contains methods for combining {@link IEvalElement}s.
 */
public final class Join {

    private Join() {}

    public static IEvalElement conjunct(List<IEvalElement> elements) {
        StringJoiner stringJoiner = new StringJoiner(" & ");
        for (IEvalElement element : elements) {
            stringJoiner.add("(" + element.getCode() + ")");
        }
        return new ClassicalB(stringJoiner.toString(), FormulaExpand.EXPAND);
    }

    public static IEvalElement disjunct(List<IEvalElement> elements) {
        StringJoiner stringJoiner = new StringJoiner(" or ");
        for (IEvalElement element : elements) {
            stringJoiner.add("(" + element.getCode() + ")");
        }
        return new ClassicalB(stringJoiner.toString(), FormulaExpand.EXPAND);
    }
}
