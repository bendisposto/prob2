package de.prob.analysis.testcasegeneration;

import de.prob.analysis.testcasegeneration.testtrace.TestTrace;

import java.util.ArrayList;

public class TestCaseGeneratorResult {

    private final ArrayList<TestTrace> testTraces;
    private final ArrayList<Target> uncoveredTargets;
    private final ArrayList<String> infeasibleOperations;

    TestCaseGeneratorResult(ArrayList<TestTrace> testTraces, ArrayList<Target> uncoveredTargets,
                            ArrayList<String> infeasibleOperations) {
        this.testTraces = testTraces;
        this.uncoveredTargets = uncoveredTargets;
        this.infeasibleOperations = infeasibleOperations;
    }

    public ArrayList<TestTrace> getTestTraces() {
        return testTraces;
    }

    public ArrayList<Target> getUncoveredTargets() {
        return uncoveredTargets;
    }

    public ArrayList<String> getInfeasibleOperations() {
        return infeasibleOperations;
    }

}
