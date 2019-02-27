package de.prob.testcasegeneration;

import de.prob.testcasegeneration.testtrace.TestTrace;

import java.util.ArrayList;

public class TestCaseGeneratorResult {

    private final ArrayList<TestTrace> testTraces;
    private final ArrayList<TestCase> uncoveredTargets;

    public TestCaseGeneratorResult(ArrayList<TestTrace> testTraces, ArrayList<TestCase> uncoveredTargets) {
        this.testTraces = testTraces;
        this.uncoveredTargets = uncoveredTargets;
    }

    public ArrayList<TestTrace> getTestTraces() {
        return testTraces;
    }

    public ArrayList<TestCase> getUncoveredTargets() {
        return uncoveredTargets;
    }

}
