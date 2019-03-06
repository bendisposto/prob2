package de.prob.analysis.testcasegeneration;

import de.be4.classicalb.core.parser.node.PPredicate;
import de.prob.analysis.mcdc.ConcreteMCDCTestCase;

public class Target {

    private String operation;
    private PPredicate guard;
    private boolean feasible;

    Target(String operation, ConcreteMCDCTestCase concreteMCDCTestCase) {
        this.operation = operation;
        this.guard = concreteMCDCTestCase.getPredicate();
        this.feasible = concreteMCDCTestCase.getTruthValue();
    }

    Target(String operation, PPredicate guard) {
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

    public boolean getFeasible() {
        return feasible;
    }

    boolean isInfeasible() {
        return !feasible;
    }

    public String toString() {
        return operation + " (" + guard + "->" + feasible + ")";
    }
}
