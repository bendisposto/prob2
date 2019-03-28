package de.prob.analysis.mcdc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.be4.classicalb.core.parser.node.*;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.Extraction;
import de.prob.animator.domainobjects.Join;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.Operation;
import de.prob.model.representation.*;

/**
 * Determines the MCDC test cases for all guards of all operations of a given {@link #model} up to a
 * specified {@link #maxLevel} (levels start at 0).
 */
public class MCDCIdentifier {

    private ClassicalBModel model;
    private int maxLevel;

    public MCDCIdentifier(ClassicalBModel model, int maxLevel) {
        this.model = model;
        this.maxLevel = maxLevel;
    }

    public Map<Operation, List<ConcreteMCDCTestCase>> identifyMCDC() {
        Map<Operation, List<ConcreteMCDCTestCase>> testCases = new HashMap<>();
        ModelElementList<Operation> operations = model.getMainMachine().getEvents();
        for (Operation operation : operations) {
            Start ast = ((ClassicalB) Join
                    .conjunct(Extraction.getGuardPredicates(model, operation.getName())))
                    .getAst();
            PPredicate startNode = ((APredicateParseUnit) ast.getPParseUnit()).getPredicate();
            testCases.put(operation, getMCDCTestCases(startNode));
        }
        return testCases;
    }

    private List<ConcreteMCDCTestCase> getMCDCTestCases(PPredicate node) {
        return new MCDCASTVisitor(maxLevel).getMCDCTestCases(node);
    }
}
