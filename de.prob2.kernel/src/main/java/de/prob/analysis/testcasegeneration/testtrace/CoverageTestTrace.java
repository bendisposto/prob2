package de.prob.analysis.testcasegeneration.testtrace;

import de.prob.statespace.Transition;
import de.prob.analysis.testcasegeneration.TestCase;

import java.util.List;
import java.util.StringJoiner;

public class CoverageTestTrace extends TestTrace {

    public CoverageTestTrace(List<Transition> priorTransitions, String newTransition, boolean isComplete) {
        super(priorTransitions, newTransition, isComplete);
    }

    public CoverageTestTrace createNewTrace(List<Transition> transitions, TestCase t, boolean isComplete) {
        return new CoverageTestTrace(transitions, t.getOperation(), isComplete);
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ", "[", "]");
        allTransitionNames.forEach(stringJoiner::add);
        return stringJoiner.toString();
    }
}
