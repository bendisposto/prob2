package de.prob.analysis.testcasegeneration;

import de.be4.classicalb.core.parser.node.APredicateParseUnit;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.Start;
import de.be4.classicalb.core.parser.util.PrettyPrinter;
import de.prob.analysis.mcdc.ConcreteMCDCTestCase;
import de.prob.analysis.mcdc.MCDCIdentifier;
import de.prob.animator.command.*;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.Operation;
import de.prob.model.representation.Guard;
import de.prob.model.representation.Invariant;
import de.prob.statespace.StateSpace;
import de.prob.analysis.testcasegeneration.testtrace.CoverageTestTrace;
import de.prob.analysis.testcasegeneration.testtrace.MCDCTestTrace;
import de.prob.analysis.testcasegeneration.testtrace.TestTrace;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class CBTestCaseGenerator {

    private ClassicalBModel model;
    private StateSpace stateSpace;
    private String criterion;
    private int max_depth;
    private ArrayList<String> finalOperations;
    private ArrayList<String> infeasibleOperations;
    private ArrayList<TestCase> targets;
    private ArrayList<TestCase> uncoveredTargets = new ArrayList<>();

    public CBTestCaseGenerator(ClassicalBModel model, StateSpace stateSpace, String criterion,
                               int max_depth, ArrayList<String> finalOperations) {
        this.model = model;
        this.stateSpace = stateSpace;
        this.criterion = criterion;
        this.max_depth = max_depth;
        this.finalOperations = finalOperations;
    }

    /**
     * Filters paths that are of the right length and are not tagged as complete due to a final operation.
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

    private String prettyPrintGuardConjunct(Object guard) {
        PrettyPrinter prettyPrinter = new PrettyPrinter();
        Start ast = ((ClassicalB) ((Guard) guard).getPredicate()).getAst();
        ((APredicateParseUnit) ast.getPParseUnit()).getPredicate().apply(prettyPrinter);
        return prettyPrinter.getPrettyPrint();
    }

    private PPredicate getGuard(String operation) {
        StringJoiner stringJoiner = new StringJoiner(" & ");
        for (Object guard : model.getMainMachine().getOperation(operation).getChildren().get(Guard.class)) {
            stringJoiner.add("(" + prettyPrintGuardConjunct(guard) + ")");
        }
        Start ast = (new ClassicalB(stringJoiner.toString(), FormulaExpand.EXPAND)).getAst();
        return ((APredicateParseUnit) ast.getPParseUnit()).getPredicate();
    }

    private ArrayList<TestCase> filterTempTargets(ArrayList<String> operations, ArrayList<TestCase> tempTargets) {
        for (TestCase t : tempTargets) {
            operations.remove(t.getOperation());
        }
        ArrayList<TestCase> artificialTestCases = new ArrayList<>();
        for (String operation : operations) {
            artificialTestCases.add(new TestCase(operation, getGuard(operation)));
        }
        return artificialTestCases;
    }

    private ArrayList<String> getAllOperationNames() {
        ArrayList<String> events = new ArrayList<>();
        for (Operation operation : model.getMainMachine().getEvents()) {
            if (!infeasibleOperations.contains(operation.getName())) {
                events.add(operation.getName());
            }
        }
        return events;
    }

    private FindTestPathCommand findTestPath(TestTrace trace, TestCase testCase) {
        FindTestPathCommand cmd = new FindTestPathCommand(trace.getTransitionNames(), stateSpace, testCase.getGuard());
        stateSpace.execute(cmd);
        return cmd;
    }

    private ArrayList<TestCase> getMCDCTestCases(int maxLevel) {
        ArrayList<TestCase> targets = new ArrayList<>();
        Map<Operation, ArrayList<ConcreteMCDCTestCase>> testCases = new MCDCIdentifier(model, maxLevel).identifyMCDC();
        for (Operation operation : testCases.keySet()) {
            for (ConcreteMCDCTestCase concreteMCDCTestCase : testCases.get(operation)) {
                targets.add(new TestCase(operation.getName(), concreteMCDCTestCase));
            }
        }
        return targets;
    }

    private ArrayList<TestCase> getOperationCoverageTestCases() {
        ArrayList<TestCase> targets = new ArrayList<>();
        for (String operation : getAllOperationNames()) {
            targets.add(new TestCase(operation, getGuard(operation)));
        }
        return targets;
    }

    private ArrayList<IEvalElement> getInvariantPredicates() {
        ArrayList<IEvalElement> iEvalElements = new ArrayList<>();
        for (Invariant invariant : model.getMainMachine().getInvariants()) {
            iEvalElements.add(invariant.getPredicate());
        }
        return iEvalElements;
    }


    private ArrayList<IEvalElement> getGuardPredicates(String operation) {
        ArrayList<IEvalElement> iEvalElements = new ArrayList<>();
        for (Object guard : model.getMainMachine().getOperation(operation).getChildren().get(Guard.class)) {
            iEvalElements.add(((Guard) guard).getPredicate());
        }
        return iEvalElements;
    }

    private ClassicalB conjoin(IEvalElement... elements) {
        StringJoiner stringJoiner = new StringJoiner(" & ");
        for (IEvalElement element : elements) {
            stringJoiner.add("(" + element.getCode() + ")");
        }
        return new ClassicalB(stringJoiner.toString(), FormulaExpand.EXPAND);
    }

    private ArrayList<String> feasibilityAnalysis() {
        ArrayList<String> infeasibleOperations = new ArrayList<>();
        ArrayList<IEvalElement> invariantPredicates = getInvariantPredicates();
        for (Operation operation : model.getMainMachine().getEvents()) {
            ArrayList<IEvalElement> iEvalElements = new ArrayList<>(invariantPredicates);
            iEvalElements.addAll(getGuardPredicates(operation.getName()));

            ClassicalB predicate = conjoin(iEvalElements.toArray(new IEvalElement[0]));
            CbcSolveCommand cmd = new CbcSolveCommand(predicate);
            stateSpace.execute(cmd);
            if (!(((EvalResult) cmd.getValue()).getValue().equals("TRUE"))) {
                infeasibleOperations.add(operation.getName());
            }
        }
        return infeasibleOperations;
    }

    private void discardInfeasibleTargets() {
        for (TestCase target : new ArrayList<>(targets)) {
            if (infeasibleOperations.contains(target.getOperation())) {
                uncoveredTargets.add(target);
                targets.remove(target);
            }
        }
    }

    public TestCaseGeneratorResult generateTestCases() {
        ArrayList<TestTrace> traces = new ArrayList<>();

        if (criterion.startsWith("MCDC")) {
            targets = getMCDCTestCases(Integer.valueOf(criterion.split(":")[1]));
            traces.add(new MCDCTestTrace(new ArrayList<>(), null, new ArrayList<>(), false));
        } else if (criterion.startsWith("OPERATION")) {
            targets = getOperationCoverageTestCases();
            traces.add(new CoverageTestTrace(new ArrayList<>(), null, false));
        } else {
            return new TestCaseGeneratorResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        infeasibleOperations = feasibilityAnalysis();
        discardInfeasibleTargets();

        int depth = 0;
        ArrayList<TestCase> tempTargets;
        while (true) {
            tempTargets = new ArrayList<>(targets);
            ArrayList<TestTrace> tracesOfCurrentDepth = filterDepthAndFinal(traces, depth);
            for (TestTrace trace : tracesOfCurrentDepth) {
                for (TestCase t : new ArrayList<>(targets)) {
                    FindTestPathCommand cmd = findTestPath(trace, t);
                    if (cmd.isFeasible()) {
                        targets.remove(t);
                        traces.add(trace.createNewTrace(cmd.getTransitions(), t,
                                (finalOperations.contains(t.getOperation()) || t.isInfeasible())));
                    }
                }
            }
            if (targets.size() == 0 || depth == max_depth) {
                break;
            }
            for (TestTrace trace : tracesOfCurrentDepth) {
                for (TestCase t : filterTempTargets(getAllOperationNames(), tempTargets)) {
                    FindTestPathCommand cmd = findTestPath(trace, t);
                    if (cmd.isFeasible()) {
                        traces.add(trace.createNewTrace(cmd.getTransitions(), t,
                                (finalOperations.contains(t.getOperation()) || t.isInfeasible())));
                    }
                }
            }
            depth++;
        }
        uncoveredTargets.addAll(targets);
        return new TestCaseGeneratorResult(traces, uncoveredTargets, infeasibleOperations);
    }
}
