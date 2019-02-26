package de.prob.testcasegeneration;

import de.be4.classicalb.core.parser.node.APredicateParseUnit;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.Start;
import de.be4.classicalb.core.parser.util.PrettyPrinter;
import de.prob.animator.command.*;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.Operation;
import de.prob.model.representation.Guard;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Transition;
import de.prob.testcasegeneration.mcdc.ConcreteMCDCTestCase;
import de.prob.testcasegeneration.mcdc.MCDCIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class CBTestCaseGenerator {

    private ClassicalBModel model;
    private StateSpace stateSpace;
    private String criterion;
    private int max_depth;
    private ArrayList<String> finalOperations;

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
        tempTargets.forEach(t -> operations.remove(t.getOperation()));
        ArrayList<TestCase> artificialTestCases = new ArrayList<>();
        for (String operation : operations) {
            PPredicate guard = getGuard(operation);
            artificialTestCases.add(new TestCase(operation, new ConcreteMCDCTestCase(guard, true)));
        }
        return artificialTestCases;
    }

    private ArrayList<String> getAllEventNames() {
        ArrayList<String> events = new ArrayList<>();
        model.getMainMachine().getEvents().forEach(op -> events.add(op.getName()));
        return events;
    }

    private FindTestPathCommand solveConstraints(TestTrace trace, TestCase testCase) {
        FindTestPathCommand cmd = new FindTestPathCommand(
                trace.getTransitionNames(), stateSpace,
                testCase.getConcreteMCDCTestCase().getPredicate());
        stateSpace.execute(cmd);
        return cmd;
    }

    private TestTrace createNewTrace(List<Transition> transitions, TestCase t, TestTrace oldTrace) {
        List<PPredicate> newGuardList = new ArrayList<>(oldTrace.getGuards());
        newGuardList.add(t.getConcreteMCDCTestCase().getPredicate());
        return new TestTrace(transitions, t.getOperation(), newGuardList,
                (finalOperations.contains(t.getOperation()) || !t.getConcreteMCDCTestCase().getTruthValue()));
    }

    private ArrayList<TestCase> getMCDCTestCases(int maxLevel) {
        ArrayList<TestCase> targets = new ArrayList<>();
        MCDCIdentifier mcdcIdentifier = new MCDCIdentifier(model, maxLevel);
        Map<Operation, ArrayList<ConcreteMCDCTestCase>> mcdcTestCases = mcdcIdentifier.identifyMCDC();
        for (Operation operation : mcdcTestCases.keySet()) {
            for (ConcreteMCDCTestCase concreteMCDCTestCase : mcdcTestCases.get(operation)) {
                targets.add(new TestCase(operation.getName(), concreteMCDCTestCase));
            }
        }
        return targets;
    }

    public TestCaseGeneratorResult generateTestCases() {
        // TODO feasibility analysis

        ArrayList<TestCase> targets = new ArrayList<>();
        if (criterion.startsWith("MCDC")) {
            targets = getMCDCTestCases(Integer.valueOf(criterion.split(":")[1]));
        }

        int depth = 0;
        ArrayList<TestTrace> traces = new ArrayList<>();
        ArrayList<TestCase> tempTargets;

        traces.add(new TestTrace(new ArrayList<>(), null, new ArrayList<>(), false));

        while (true) {
            tempTargets = new ArrayList<>(targets); // target' = target
            ArrayList<TestTrace> tracesOfCurrentDepth = filterDepthAndFinal(traces, depth);
            for (TestTrace trace : tracesOfCurrentDepth) {
                for (TestCase t : new ArrayList<>(targets)) {
                    FindTestPathCommand cmd = solveConstraints(trace, t);
                    if (cmd.isFeasible()) {
                        targets.remove(t);
                        traces.add(createNewTrace(cmd.getTransitions(), t, trace));
                    }
                }
            }
            if (targets.size() == 0 || depth == max_depth) {
                break;
            }
            for (TestTrace trace : tracesOfCurrentDepth) {
                for (TestCase t : filterTempTargets(getAllEventNames(), tempTargets)) {
                    FindTestPathCommand cmd = solveConstraints(trace, t);
                    if (cmd.isFeasible()) {
                        traces.add(createNewTrace(cmd.getTransitions(), t, trace));
                    }
                }
            }
            depth++;
        }
        return new TestCaseGeneratorResult(traces, targets);
    }
}
