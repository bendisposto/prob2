package de.prob.analysis.testcasegeneration;

import de.be4.classicalb.core.parser.node.PPredicate;
import de.prob.analysis.mcdc.ConcreteMCDCTestCase;

public class TestCase {

    private String operation;
    private PPredicate guard;
    private boolean feasible;

    public TestCase(String operation, ConcreteMCDCTestCase concreteMCDCTestCase) {
        this.operation = operation;
        this.guard = concreteMCDCTestCase.getPredicate();
        this.feasible = concreteMCDCTestCase.getTruthValue();
    }

    public TestCase(String operation, PPredicate guard) {
        this.operation = operation;
        this.guard = guard;
        this.feasible = true;
    }

    public String getOperation() {
        return operation;
    }

    public PPredicate getGuard() {
        return guard;
    }

    public boolean isFeasible() {
        return feasible;
    }

    public String toString() {
        return operation + " (" + guard + "->" + feasible + ")";
    }
}
