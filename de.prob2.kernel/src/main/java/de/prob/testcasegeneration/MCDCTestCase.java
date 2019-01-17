package de.prob.testcasegeneration;

import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.Start;
import de.be4.classicalb.core.parser.util.PrettyPrinter;
import de.be4.classicalb.core.parser.visualisation.ASTPrinter;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.FormulaExpand;

public class MCDCTestCase {

    private final PPredicate predicate;
    private final boolean truthValue;

    public MCDCTestCase(PPredicate predicate, boolean truthValue) {
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