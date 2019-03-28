package de.prob.analysis.mcdc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.be4.classicalb.core.parser.node.*;
import de.be4.classicalb.core.parser.util.PrettyPrinter;
import de.prob.animator.command.CbcSolveCommand;
import de.prob.animator.domainobjects.*;
import de.prob.model.representation.Extraction;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.Operation;
import de.prob.model.representation.*;
import de.prob.statespace.StateSpace;

/**
 * Determines the MCDC test cases for all guards of all operations of a given {@link #model} up to a
 * specified {@link #maxLevel} (levels start at 0).
 */
public class MCDCIdentifier {

    private final static Logger log = Logger.getLogger(MCDCIdentifier.class.getName());

    private ClassicalBModel model;
    private StateSpace stateSpace;
    private int maxLevel;

    public MCDCIdentifier(ClassicalBModel model, StateSpace stateSpace, int maxLevel) {
        this.model = model;
        this.stateSpace = stateSpace;
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
        List<ConcreteMCDCTestCase> testCases = new MCDCASTVisitor(maxLevel).getMCDCTestCases(node);
        return filterFeasible(testCases);
    }

    private List<ConcreteMCDCTestCase> filterFeasible(List<ConcreteMCDCTestCase> testCases) {
        List<ConcreteMCDCTestCase> feasibleTestCases = new ArrayList<>();
        for (ConcreteMCDCTestCase t : testCases) {
            CbcSolveCommand cmd = new CbcSolveCommand(classicalBFromPredicate(t.getPredicate()));
            stateSpace.execute(cmd);
            if (cmd.getValue() == EvalResult.FALSE) {
                log.info("Infeasible: " + t.toString());
            } else {
                feasibleTestCases.add(t);
            }
        }
        return feasibleTestCases;
    }

    private IEvalElement classicalBFromPredicate(PPredicate predicate) {
        PrettyPrinter pp = new PrettyPrinter();
        predicate.apply(pp);
        return new ClassicalB(pp.getPrettyPrint(), FormulaExpand.EXPAND);
    }
}
