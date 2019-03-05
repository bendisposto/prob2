package de.prob.analysis.mcdc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import de.be4.classicalb.core.parser.node.*;
import de.be4.classicalb.core.parser.util.PrettyPrinter;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.Operation;
import de.prob.model.representation.*;

public class MCDCIdentifier {

    private ClassicalBModel model;
    private int maxLevel;

    public MCDCIdentifier(ClassicalBModel model, int maxLevel) {
        this.model = model;
        this.maxLevel = maxLevel;
    }

    public Map<Operation, ArrayList<ConcreteMCDCTestCase>> identifyMCDC() {
        Map<Operation, ArrayList<ConcreteMCDCTestCase>> testCases = new HashMap<>();
        ModelElementList<Operation> operations = model.getMainMachine().getEvents();
        for (Operation operation : operations) {
            Start ast = getGuard(operation).getAst();
            PPredicate startNode = ((APredicateParseUnit) ast.getPParseUnit()).getPredicate();
            testCases.put(operation, getMCDCTestCases(startNode));
        }
        return testCases;
    }

    private ArrayList<ConcreteMCDCTestCase> getMCDCTestCases(PPredicate node) {
        return new MCDCASTVisitor(maxLevel).getMCDCTestCases(node);
    }

    private ClassicalB getGuard(Operation operation) {
        PrettyPrinter prettyPrinter;
        StringJoiner stringJoiner = new StringJoiner(" & ");
        for (Object guard: operation.getChildren().get(Guard.class)) {
            prettyPrinter = new PrettyPrinter();
            Start ast = ((ClassicalB) ((Guard) guard).getPredicate()).getAst();
            ((APredicateParseUnit) ast.getPParseUnit()).getPredicate().apply(prettyPrinter);
            stringJoiner.add("(" + prettyPrinter.getPrettyPrint() + ")");
        }
        return new ClassicalB(stringJoiner.toString(), FormulaExpand.EXPAND);
    }
}
