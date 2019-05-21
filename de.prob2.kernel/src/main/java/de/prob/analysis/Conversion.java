package de.prob.analysis;

import de.be4.classicalb.core.parser.node.APredicateParseUnit;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.Start;
import de.be4.classicalb.core.parser.util.PrettyPrinter;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;

public class Conversion {

    public static PPredicate predicateFromString(String predicateString) {
        Start ast = new ClassicalB(predicateString, FormulaExpand.EXPAND).getAst();
        return ((APredicateParseUnit) ast.getPParseUnit()).getPredicate();
    }

    public static PPredicate predicateFromClassicalB(ClassicalB classicalB) {
        Start ast = classicalB.getAst();
        return ((APredicateParseUnit) ast.getPParseUnit()).getPredicate();
    }

    public static IEvalElement classicalBFromPredicate(PPredicate predicate) {
        PrettyPrinter pp = new PrettyPrinter();
        predicate.apply(pp);
        return new ClassicalB(pp.getPrettyPrint(), FormulaExpand.EXPAND);
    }

    public static PPredicate predicateFromPredicate(PPredicate predicate) {
        PrettyPrinter pp = new PrettyPrinter();
        predicate.apply(pp);
        return predicateFromString(pp.getPrettyPrint());
    }

}
