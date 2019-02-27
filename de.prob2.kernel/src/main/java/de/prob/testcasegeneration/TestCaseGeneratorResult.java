package de.prob.testcasegeneration;

import de.prob.testcasegeneration.testtrace.TestTrace;

import java.util.ArrayList;

public class TestCaseGeneratorResult {

    private final ArrayList<TestTrace> testTraces;
    private final ArrayList<TestCase> uncoveredTargets;
    private final ArrayList<String> infeasibleOperations;

    TestCaseGeneratorResult(ArrayList<TestTrace> testTraces, ArrayList<TestCase> uncoveredTargets,
                                   ArrayList<String> infeasibleOperations) {
        this.testTraces = testTraces;
        this.uncoveredTargets = uncoveredTargets;
        this.infeasibleOperations = infeasibleOperations;
    }

    ArrayList<TestTrace> getTestTraces() {
        return testTraces;
    }

    ArrayList<TestCase> getUncoveredTargets() {
        return uncoveredTargets;
    }

    public ArrayList<String> getInfeasibleOperations() {
        return infeasibleOperations;
    }

}
