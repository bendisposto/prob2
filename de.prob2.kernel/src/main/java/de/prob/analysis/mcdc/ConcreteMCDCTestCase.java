package de.prob.analysis.mcdc;

import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.util.PrettyPrinter;

/**
 * A concrete MCDC test case, consisting of a predicate and an over-all truth value.
 * The predicate is possibly a combination of child predicates according to an {@link AbstractMCDCTestCase}.
 */
public class ConcreteMCDCTestCase {

    private final PPredicate predicate;
    private final boolean truthValue;

    public ConcreteMCDCTestCase(PPredicate predicate, boolean truthValue) {
        this.predicate = (PPredicate) predicate.clone();
        this.truthValue = truthValue;
    }

    public PPredicate getPredicate() {
        return predicate;
    }

    public boolean getTruthValue() {
        return truthValue;
    }

    public String toString() {
        PrettyPrinter prettyPrinter = new PrettyPrinter();
        predicate.apply(prettyPrinter);
        return prettyPrinter.getPrettyPrint() + " -> " + truthValue;
    }
}