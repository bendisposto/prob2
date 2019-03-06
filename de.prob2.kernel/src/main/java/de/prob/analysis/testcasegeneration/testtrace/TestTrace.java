package de.prob.analysis.testcasegeneration.testtrace;

import de.prob.analysis.testcasegeneration.TestCase;

import java.util.ArrayList;
import java.util.List;

public abstract class TestTrace {

    final List<String> transitionNames = new ArrayList<>();
    private final boolean isComplete;

    TestTrace(List<String> priorTransitions, String newTransition, boolean isComplete) {
        transitionNames.addAll(priorTransitions);
        if (newTransition != null) {
            transitionNames.add(newTransition);
        }
        this.isComplete = isComplete;
    }

    public List<String> getTransitionNames() {
        return transitionNames;
    }

    public int getDepth() {
        return transitionNames.size();
    }

    public boolean isComplete() {
        return isComplete;
    }

    public abstract TestTrace createNewTrace(List<String> transitions, TestCase t, boolean isComplte);
}