package de.prob.testcasegeneration;

import de.prob.testcasegeneration.mcdc.ConcreteMCDCTestCase;

public class TestCase {

    private String operation;
    private ConcreteMCDCTestCase concreteMCDCTestCase;

    public TestCase(String operation, ConcreteMCDCTestCase concreteMCDCTestCase) {
        this.operation = operation;
        this.concreteMCDCTestCase = concreteMCDCTestCase;
    }

    public ConcreteMCDCTestCase getConcreteMCDCTestCase() {
        return concreteMCDCTestCase;
    }

    public String getOperation() {
        return operation;
    }

    public String toString() {
        return operation + " (" + concreteMCDCTestCase + ")";
    }
}
