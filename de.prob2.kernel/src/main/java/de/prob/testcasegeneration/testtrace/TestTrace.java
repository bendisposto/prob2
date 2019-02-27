package de.prob.testcasegeneration.testtrace;

import de.prob.statespace.Transition;
import de.prob.testcasegeneration.TestCase;

import java.util.ArrayList;
import java.util.List;

public abstract class TestTrace {

    private final List<Transition> priorTransitions;
    final List<String> allTransitionNames = new ArrayList<>();
    private final boolean isComplete;

    TestTrace(List<Transition> priorTransitions, String newTransition, boolean isComplete) {
        this.priorTransitions = priorTransitions;
        if (!priorTransitions.isEmpty()) {
            priorTransitions.stream().skip(1).forEach(t -> allTransitionNames.add(t.getName()));
            allTransitionNames.add(newTransition);
        }
        this.isComplete = isComplete;
    }

    public List<String> getTransitionNames() {
        return allTransitionNames;
    }

    public int getDepth() {
        return priorTransitions.size();
    }

    public boolean isComplete() {
        return isComplete;
    }

    public abstract TestTrace createNewTrace(List<Transition> transitions, TestCase t, boolean isComplte);
}