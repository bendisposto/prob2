package de.prob.testcasegeneration;

import java.util.ArrayList;

import de.be4.classicalb.core.parser.node.*;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.FormulaExpand;

public class MCDCIdentifier {

    private int maxLevel;
    private String[] formulas;

    public MCDCIdentifier(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public MCDCIdentifier(int maxLevel, String[] formulas) {
        this.maxLevel = maxLevel;
        this.formulas = formulas;
    }

    public ArrayList<ArrayList<MCDCTestCase>> identifyMCDC() {
        ArrayList<ArrayList<MCDCTestCase>> testCases = new ArrayList<>();
        for (String formula : formulas) {
            ClassicalB predicate = new ClassicalB(formula, FormulaExpand.EXPAND);
            Start ast = predicate.getAst();
            PPredicate startNode = ((APredicateParseUnit) ast.getPParseUnit()).getPredicate();
            ArrayList<MCDCTestCase> testCasesForFormula = getMCDCTestCases(startNode);
            testCases.add(testCasesForFormula);
        }
        return testCases;
    }

    private ArrayList<MCDCTestCase> getMCDCTestCases(PPredicate node) {
        return new MCDCASTVisitor(maxLevel).getMCDCTestCases(node);
    }

}
