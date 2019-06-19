package de.prob.analysis.testcasegeneration;

import de.be4.classicalb.core.parser.node.APredicateParseUnit;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.analysis.FeasibilityAnalysis;
import de.prob.analysis.mcdc.ConcreteMCDCTestCase;
import de.prob.analysis.mcdc.MCDCIdentifier;
import de.prob.analysis.testcasegeneration.testtrace.CoverageTestTrace;
import de.prob.analysis.testcasegeneration.testtrace.MCDCTestTrace;
import de.prob.analysis.testcasegeneration.testtrace.TestTrace;
import de.prob.animator.command.FindTestPathCommand;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.Join;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.Operation;
import de.prob.model.representation.Extraction;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Performs constraint-based test case generation.
 * <p>
 * The generator can be executed with different coverage objectives. Currently available coverage options:
 * - Operation Coverage
 * - MC/DC Coverage
 */
public class ConstraintBasedTestCaseGenerator {

    private ClassicalBModel model;
    private StateSpace stateSpace;
    private String criterion;
    private int maxDepth;
    private List<String> finalOperations;
    private List<String> infeasibleOperations;
    private List<Target> targets;
    private List<Target> uncoveredTargets = new ArrayList<>();

    public ConstraintBasedTestCaseGenerator(ClassicalBModel model, StateSpace stateSpace, String criterion,
                                            int maxDepth, List<String> finalOperations) {
        this.model = model;
        this.stateSpace = stateSpace;
        this.criterion = criterion;
        this.maxDepth = maxDepth;
        this.finalOperations = finalOperations;
    }

    /**
     * Performs the test case generation.
     *
     * @return A {@link TestCaseGeneratorResult} containing the final test cases and the targets left uncovered.
     */
    public TestCaseGeneratorResult generateTestCases() {
        boolean interrupted = false;
        List<Trace> traces = new ArrayList<>();
        List<TestTrace> testTraces = new ArrayList<>();

        if (criterion.startsWith("MCDC")) {
            targets = getMCDCTargets(Integer.valueOf(criterion.split(":")[1]));
            testTraces.add(new MCDCTestTrace(new ArrayList<>(), null, new ArrayList<>(), false,
                    true));
        } else if (criterion.startsWith("OPERATION")) {
            List<String> selectedOperations = Arrays.asList(criterion.split(":")[1].split(","));
            targets = getOperationCoverageTargets(selectedOperations);
            testTraces.add(new CoverageTestTrace(new ArrayList<>(), null, false));
        } else {
            return new TestCaseGeneratorResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), interrupted);
        }

        infeasibleOperations = new FeasibilityAnalysis(model, stateSpace).analyseFeasibility();
        discardInfeasibleTargets();

        int depth = 0;
        List<Target> tempTargets;
        while (true) {
            tempTargets = new ArrayList<>(targets);
            List<TestTrace> tracesOfCurrentDepth = filterDepthAndFinal(testTraces, depth);
            for (TestTrace trace : tracesOfCurrentDepth) {
                for (Target t : new ArrayList<>(targets)) {
                    FindTestPathCommand cmd = findTestPath(trace, t);
                    if (cmd.isFeasible()) {
                        targets.remove(t);
                        testTraces.add(trace.createNewTrace(trace.getTransitionNames(), t,
                                (finalOperations.contains(t.getOperation()) || t.isInfeasible())));
                        cmd = findTestPathWithTarget(trace, t);
                        if(cmd.getTrace() != null) {
                            traces.add(cmd.getTrace());
                        }
                    }
                }
            }
            if (targets.isEmpty() || depth == maxDepth) {
                break;
            }
            for (TestTrace trace : tracesOfCurrentDepth) {
                for (Target t : filterTempTargets(getAllOperationNames(), tempTargets)) {
                    FindTestPathCommand cmd = findTestPath(trace, t);
                    if (cmd.isFeasible()) {
                        testTraces.add(trace.createNewTrace(trace.getTransitionNames(), t,
                                (finalOperations.contains(t.getOperation()) || t.isInfeasible())));
                        cmd = findTestPathWithTarget(trace, t);
                        if(cmd.getTrace() != null) {
                            traces.add(cmd.getTrace());
                        }
                    }
                }
            }
            depth++;
            if(Thread.currentThread().isInterrupted()) {
                interrupted = true;
                break;
            }
        }
        uncoveredTargets.addAll(targets);
        return new TestCaseGeneratorResult(traces, testTraces, uncoveredTargets, infeasibleOperations, interrupted);
    }

    /**
     * Determines the targets for the test case generation with MC/DC coverage.
     *
     * @param maxLevel The maximum level for MC/DC
     * @return The targets.
     */
    private List<Target> getMCDCTargets(int maxLevel) {
        List<Target> mcdcTargets = new ArrayList<>();
        Map<Operation, List<ConcreteMCDCTestCase>> testCases = new MCDCIdentifier(model, stateSpace, maxLevel).identifyMCDC();
        for (Entry<Operation, List<ConcreteMCDCTestCase>> entry : testCases.entrySet()) {
            for (ConcreteMCDCTestCase concreteMCDCTestCase : entry.getValue()) {
                mcdcTargets.add(new Target(entry.getKey().getName(), concreteMCDCTestCase));
            }
        }
        return mcdcTargets;
    }

    /**
     * Determines the {@link Target}s for the test case generation with operation coverage based on the selected operations.
     *
     * @param selectedOperations The list of selected operations
     * @return The {@link Target}s
     */
    private List<Target> getOperationCoverageTargets(List<String> selectedOperations) {
        return createTargetsForOperations(selectedOperations);
    }

    /**
     * Creates {@link Target}s for a list of operations.
     * <p>
     * Each target consists of the operation's name and guard.
     *
     * @param operations The list of operations
     * @return The {@link Target}s
     */
    private List<Target> createTargetsForOperations(List<String> operations) {
        List<Target> operationTargets = new ArrayList<>();
        for (String operation : operations) {
            operationTargets.add(new Target(operation, getGuardAsPredicate(operation)));
        }
        return operationTargets;
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
    private List<TestTrace> filterDepthAndFinal(List<TestTrace> traces, int depth) {
        return traces.stream()
                .filter(x -> x.getDepth() == depth && !x.isComplete())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns targets that have not yet been examined in the current iteration.
     *
     * @param operations  All feasible operations of the machine
     * @param tempTargets The examined targets
     * @return The artificial targets to be examined next, one for each valid operation
     */
    private List<Target> filterTempTargets(List<String> operations, List<Target> tempTargets) {
        for (Target t : tempTargets) {
            operations.remove(t.getOperation());
        }
        return createTargetsForOperations(operations);
    }

    private PPredicate getGuardAsPredicate(String operation) {
    	List<IEvalElement> guards = Extraction.getGuardPredicates(model, operation);
        ClassicalB predicate = null;
        if(guards.isEmpty()) {
        	predicate = new ClassicalB("1=1", FormulaExpand.EXPAND);
        } else {
        	predicate = (ClassicalB) Join.conjunct(guards);
        }
        Start ast = predicate.getAst();
        return ((APredicateParseUnit) ast.getPParseUnit()).getPredicate();
    }

    /**
     * Returns the names of all feasible operations.
     *
     * @return Names of feasible operations.
     */
    private List<String> getAllOperationNames() {
        List<String> operations = new ArrayList<>();
        for (Operation operation : model.getMainMachine().getEvents()) {
            if (!infeasibleOperations.contains(operation.getName())) {
                operations.add(operation.getName());
            }
        }
        return operations;
    }

    /**
     * Executes the {@link FindTestPathCommand}.
     * <p>
     * The command calls the ProB core to find a feasible path, composed of the transitions of a trace, that ends in a
     * state that satisfies the guard of the regarded target.
     *
     * @param trace  The prior trace
     * @param target The regarded target
     * @return The command that contains the result of the ProB call
     */
    private FindTestPathCommand findTestPath(TestTrace trace, Target target) {
        FindTestPathCommand cmd = new FindTestPathCommand(trace.getTransitionNames(), stateSpace, target.getGuard());
        stateSpace.execute(cmd);
        return cmd;
    }

    /**
     * Executes the {@link FindTestPathCommand}.
     * <p>
     * The command calls the ProB core to find a feasible path containing the target as final operation.
     * This function is used after checking the feasibility of the prior trace and the final operation.
     *
     * @param trace  The prior trace
     * @param target The regarded target
     * @return The command that contains the result of the ProB call
     */
    private FindTestPathCommand findTestPathWithTarget(TestTrace trace, Target target) {
        List<String> transitions = new ArrayList<>(trace.getTransitionNames());
        transitions.add(target.getOperation());
        FindTestPathCommand cmd = new FindTestPathCommand(transitions, stateSpace);
        stateSpace.execute(cmd);
        return cmd;
    }
}
