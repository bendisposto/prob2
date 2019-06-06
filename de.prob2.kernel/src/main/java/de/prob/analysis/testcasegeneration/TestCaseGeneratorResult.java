package de.prob.analysis.testcasegeneration;

import de.prob.analysis.testcasegeneration.testtrace.TestTrace;
import de.prob.statespace.Trace;

import java.util.List;

/**
 * The result of the test case generation, wrapping the final test traces and the uncovered targets.
 */
public class TestCaseGeneratorResult {

    private final List<Trace> traces;
    private final List<TestTrace> testTraces;
    private final List<Target> uncoveredTargets;
    private final List<String> infeasibleOperations;

    public TestCaseGeneratorResult(final List<Trace> traces, final List<TestTrace> testTraces, final List<Target> uncoveredTargets,
                            final List<String> infeasibleOperations) {
        this.traces = traces;
        this.testTraces = testTraces;
        this.testTraces.removeIf(t -> t.getTransitionNames().isEmpty());
        this.uncoveredTargets = uncoveredTargets;
        this.infeasibleOperations = infeasibleOperations;
    }

    public List<Trace> getTraces() {
        return traces;
    }

    public List<TestTrace> getTestTraces() {
        return testTraces;
    }

    public List<Target> getUncoveredTargets() {
        return uncoveredTargets;
    }

    public List<String> getInfeasibleOperations() {
        return infeasibleOperations;
    }

}
