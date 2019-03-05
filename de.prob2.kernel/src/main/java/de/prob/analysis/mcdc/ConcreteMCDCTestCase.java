package de.prob.analysis.mcdc;

import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.util.PrettyPrinter;

public class ConcreteMCDCTestCase {

    private final PPredicate predicate;
    private final boolean truthValue;

    ConcreteMCDCTestCase(PPredicate predicate, boolean truthValue) {
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