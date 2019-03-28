package de.prob.analysis.testcasegeneration;

import de.be4.classicalb.core.parser.node.PPredicate;
import de.prob.analysis.mcdc.ConcreteMCDCTestCase;

/**
 * A target that is to be fulfilled by the test case generation.
 *
 * The target consists of an {@link #operation}, a predicate {@link #guard} and a boolean {@link #feasible} indicating
 * whether the target is actually to be executed or just reached.
 *
 * Use cases:
 * For MC/DC coverage, the {@link #guard} may be different from the guard the operation has in the model. Additionally,
 * {@link #feasible} might be false.
 * For operation coverage, {@link #feasible} is always true and the {@link #guard} is always the same as in the model.
 */
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
