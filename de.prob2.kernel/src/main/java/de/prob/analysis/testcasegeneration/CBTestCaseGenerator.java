package de.prob.analysis.testcasegeneration;

import de.be4.classicalb.core.parser.node.APredicateParseUnit;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.analysis.FeasibilityAnalysis;
import de.prob.analysis.mcdc.ConcreteMCDCTestCase;
import de.prob.analysis.mcdc.MCDCIdentifier;
import de.prob.animator.command.*;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.ExtractionLinkageProvider;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.Operation;
import de.prob.statespace.StateSpace;
import de.prob.analysis.testcasegeneration.testtrace.CoverageTestTrace;
import de.prob.analysis.testcasegeneration.testtrace.MCDCTestTrace;
import de.prob.analysis.testcasegeneration.testtrace.TestTrace;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Performs constraint-based test case generation.
 *
 * The generator can be executed with different coverage objectives. Currently available coverage options:
 * - Operation Coverage
 * - MC/DC Coverage
 */
public class CBTestCaseGenerator {

    private ClassicalBModel model;
    private StateSpace stateSpace;
    private String criterion;
    private int max_depth;
    private ArrayList<String> finalOperations;
    private ArrayList<String> infeasibleOperations;
    private ArrayList<Target> targets;
    private ArrayList<Target> uncoveredTargets = new ArrayList<>();

    public CBTestCaseGenerator(ClassicalBModel model, StateSpace stateSpace, String criterion,
                               int max_depth, ArrayList<String> finalOperations) {
        this.model = model;
        this.stateSpace = stateSpace;
        this.criterion = criterion;
        this.max_depth = max_depth;
        this.finalOperations = finalOperations;
    }

    /**
     * Performs the test case generation.
     *
     * @return A {@link TestCaseGeneratorResult} containing the final test cases and the targets left uncovered.
     */
    public TestCaseGeneratorResult generateTestCases() {
        ArrayList<TestTrace> traces = new ArrayList<>();

        if (criterion.startsWith("MCDC")) {
            targets = getMCDCTargets(Integer.valueOf(criterion.split(":")[1]));
            traces.add(new MCDCTestTrace(new ArrayList<>(), null, new ArrayList<>(), false));
        } else if (criterion.startsWith("OPERATION")) {
            targets = getOperationCoverageTargets();
            traces.add(new CoverageTestTrace(new ArrayList<>(), null, false));
        } else {
            return new TestCaseGeneratorResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        infeasibleOperations = new FeasibilityAnalysis(model, stateSpace).analyseFeasibility();
        discardInfeasibleTargets();

        int depth = 0;
        ArrayList<Target> tempTargets;
        while (true) {
            tempTargets = new ArrayList<>(targets);
            ArrayList<TestTrace> tracesOfCurrentDepth = filterDepthAndFinal(traces, depth);
            for (TestTrace trace : tracesOfCurrentDepth) {
                for (Target t : new ArrayList<>(targets)) {
                    FindTestPathCommand cmd = findTestPath(trace, t);
                    if (cmd.isFeasible()) {
                        targets.remove(t);
                        traces.add(trace.createNewTrace(trace.getTransitionNames(), t,
                                (finalOperations.contains(t.getOperation()) || t.isInfeasible())));
                    }
                }
            }
            if (targets.size() == 0 || depth == max_depth) {
                break;
            }
            for (TestTrace trace : tracesOfCurrentDepth) {
                for (Target t : filterTempTargets(getAllOperationNames(), tempTargets)) {
                    FindTestPathCommand cmd = findTestPath(trace, t);
                    if (cmd.isFeasible()) {
                        traces.add(trace.createNewTrace(trace.getTransitionNames(), t,
                                (finalOperations.contains(t.getOperation()) || t.isInfeasible())));
                    }
                }
            }
            depth++;
        }
        uncoveredTargets.addAll(targets);
        return new TestCaseGeneratorResult(traces, uncoveredTargets, infeasibleOperations);
    }

    /**
     * Determines the targets for the test case generation with MC/DC coverage.
     *
     * @param maxLevel The maximum level for MC/DC
     * @return The targets.
     */
    private ArrayList<Target> getMCDCTargets(int maxLevel) {
        ArrayList<Target> targets = new ArrayList<>();
        Map<Operation, ArrayList<ConcreteMCDCTestCase>> testCases = new MCDCIdentifier(model, maxLevel).identifyMCDC();
        for (Operation operation : testCases.keySet()) {
            for (ConcreteMCDCTestCase concreteMCDCTestCase : testCases.get(operation)) {
                targets.add(new Target(operation.getName(), concreteMCDCTestCase));
            }
        }
        return targets;
    }


    /**
     * Determines the targets for the test case generation with operation coverage, i.e. all feasible operations.
     *
     * @return The targets.
     */
    private ArrayList<Target> getOperationCoverageTargets() {
        ArrayList<Target> targets = new ArrayList<>();
        for (String operation : getAllOperationNames()) {
            targets.add(new Target(operation, getGuardAsPredicate(operation)));
        }
        return targets;
    }

    /**
     * Removes the targets which can never be reached due to an infeasible operation.
     */
    private void discardInfeasibleTargets() {
        for (Target target : new ArrayList<>(targets)) {
            if (infeasibleOperations.contains(target.getOperation())) {
                uncoveredTargets.add(target);
                targets.remove(target);
            }
        }
    }

    /**
     * Returns test traces that are of the specified depth and are not tagged as complete due to a final operation.
     *
     * @param traces All built traces
     * @param depth  The current trace length
     * @return List of paths that can be extended
     */
    private ArrayList<TestTrace> filterDepthAndFinal(ArrayList<TestTrace> traces, int depth) {
        return traces.stream()
                .filter(x -> x.getDepth() == depth && !x.isComplete())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns targets that have not yet been examined in the current iteration.
     *
     * @param operations All feasible operations of the machine
     * @param tempTargets The examined targets
     * @return The artificial targets to be examined next, one for each valid operation
     */
    private ArrayList<Target> filterTempTargets(ArrayList<String> operations, ArrayList<Target> tempTargets) {
        for (Target t : tempTargets) {
            operations.remove(t.getOperation());
        }
        ArrayList<Target> artificialTargets = new ArrayList<>();
        for (String operation : operations) {
            artificialTargets.add(new Target(operation, getGuardAsPredicate(operation)));
        }
        return artificialTargets;
    }

    private PPredicate getGuardAsPredicate(String operation) {
        Start ast = ((ClassicalB) ExtractionLinkageProvider
                .conjoin(ExtractionLinkageProvider.getGuardPredicates(model, operation)))
                .getAst();
        return ((APredicateParseUnit) ast.getPParseUnit()).getPredicate();
    }

    /**
     * Returns the names of all feasible operations.
     *
     * @return Names of feasible operations.
     */
    private ArrayList<String> getAllOperationNames() {
        ArrayList<String> operations = new ArrayList<>();
        for (Operation operation : model.getMainMachine().getEvents()) {
            if (!infeasibleOperations.contains(operation.getName())) {
                operations.add(operation.getName());
            }
        }
        return operations;
    }

    /**
     * Executes the {@link FindTestPathCommand}.
     *
     * The command calls the ProB core to find a feasible path, composed of the transitions of a trace, that ends in a
     * state that satisfies the guard of the regarded target.
     *
     * @param trace The prior trace
     * @param target The regarded target
     * @return The command that contains the result of the ProB call
     */
    private FindTestPathCommand findTestPath(TestTrace trace, Target target) {
        FindTestPathCommand cmd = new FindTestPathCommand(trace.getTransitionNames(), stateSpace, target.getGuard());
        stateSpace.execute(cmd);
        return cmd;
    }
}
