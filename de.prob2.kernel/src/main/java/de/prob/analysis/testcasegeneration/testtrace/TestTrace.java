package de.prob.analysis.testcasegeneration.testtrace;

import de.prob.analysis.testcasegeneration.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * A test trace (just a different denotation of test case to distinguish between target test cases and generated
 * test cases) created by the test case generator.
 *
 * It consists of a list of transitions and an identifier whether the trace is complete.
 * A complete trace cannot be extended.
 */
public abstract class TestTrace {

    final List<String> transitionNames = new ArrayList<>();
    private final boolean isComplete;
    private final boolean lastTransitionIsFeasible;

    TestTrace(List<String> priorTransitions, String newTransition, boolean isComplete, boolean lastTransitionIsFeasible) {
        transitionNames.addAll(priorTransitions);
        if (newTransition != null) {
            transitionNames.add(newTransition);
        }
        this.isComplete = isComplete;
        this.lastTransitionIsFeasible = lastTransitionIsFeasible;
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

    public boolean lastTransitionIsFeasible() {
        return lastTransitionIsFeasible;
    }

    public abstract TestTrace createNewTrace(List<String> transitions, Target t, boolean isComplete);
}