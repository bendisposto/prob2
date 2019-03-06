package de.prob.analysis.testcasegeneration.testtrace;

import de.be4.classicalb.core.parser.util.PrettyPrinter;
import de.prob.analysis.mcdc.ConcreteMCDCTestCase;
import de.prob.analysis.testcasegeneration.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class MCDCTestTrace extends TestTrace {

    private List<ConcreteMCDCTestCase> MCDCTestCases;

    public MCDCTestTrace(List<String> priorTransitions, String newTransition, List<ConcreteMCDCTestCase> MCDCTestCases,
                         boolean isComplete) {
        super(priorTransitions, newTransition, isComplete);
        this.MCDCTestCases = MCDCTestCases;
    }

    private List<ConcreteMCDCTestCase> getMCDCTestCases() {
        return MCDCTestCases;
    }

    public MCDCTestTrace createNewTrace(List<String> priorTransitions, TestCase t, boolean isComplete) {
        List<ConcreteMCDCTestCase> newTestCaseList = new ArrayList<>(getMCDCTestCases());
        newTestCaseList.add(new ConcreteMCDCTestCase(t.getGuard(), t.getFeasible()));
        return new MCDCTestTrace(priorTransitions, t.getOperation(), newTestCaseList, isComplete);
    }

    public String toString() {
        PrettyPrinter prettyPrinter;
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        for (int i = 0; i < transitionNames.size(); i++) {
            prettyPrinter = new PrettyPrinter();
            MCDCTestCases.get(i).getPredicate().apply(prettyPrinter);
            stringJoiner.add(transitionNames.get(i) + " [" + prettyPrinter.getPrettyPrint() + " -> "
                    + MCDCTestCases.get(i).getTruthValue() + "]");
        }
        return stringJoiner.toString();
    }
}
