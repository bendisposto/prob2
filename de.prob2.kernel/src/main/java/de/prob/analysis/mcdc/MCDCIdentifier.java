package de.prob.analysis.mcdc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.be4.classicalb.core.parser.node.*;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.ExtractionLinkageProvider;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.Operation;
import de.prob.model.representation.*;

/**
 * Determines the MCDC test cases for all guards of all operations of a given {@link #model} up to a
 * specified {@link #maxLevel}.
 */
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
            Start ast = ((ClassicalB) ExtractionLinkageProvider
                    .conjoin(ExtractionLinkageProvider.getGuardPredicates(model, operation.getName())))
                    .getAst();
            PPredicate startNode = ((APredicateParseUnit) ast.getPParseUnit()).getPredicate();
            testCases.put(operation, getMCDCTestCases(startNode));
        }
        return testCases;
    }

    private ArrayList<ConcreteMCDCTestCase> getMCDCTestCases(PPredicate node) {
        return new MCDCASTVisitor(maxLevel).getMCDCTestCases(node);
    }
}
