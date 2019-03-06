package de.prob.analysis.testcasegeneration.testtrace;

import de.be4.classicalb.core.parser.util.PrettyPrinter;
import de.prob.analysis.mcdc.ConcreteMCDCTestCase;
import de.prob.analysis.testcasegeneration.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * A test trace created by the test case generator with the aim to fulfill MC/DC coverage.
 *
 * In addition to the {@link TestTrace}, an MCDCTestTrace contains the MC/DC targets that were fulfilled along the trace
 * to allow for a more detailed output once the test case generation is finished.
 */
public class MCDCTestTrace extends TestTrace {

    private List<ConcreteMCDCTestCase> MCDCTargets;

    public MCDCTestTrace(List<String> priorTransitions, String newTransition, List<ConcreteMCDCTestCase> MCDCTargets,
                         boolean isComplete) {
        super(priorTransitions, newTransition, isComplete);
        this.MCDCTargets = MCDCTargets;
    }

    private List<ConcreteMCDCTestCase> getMCDCTargets() {
        return MCDCTargets;
    }

    public MCDCTestTrace createNewTrace(List<String> priorTransitions, Target t, boolean isComplete) {
        List<ConcreteMCDCTestCase> newTestCaseList = new ArrayList<>(getMCDCTargets());
        newTestCaseList.add(new ConcreteMCDCTestCase(t.getGuard(), t.getFeasible()));
        return new MCDCTestTrace(priorTransitions, t.getOperation(), newTestCaseList, isComplete);
    }

    public String toString() {
        PrettyPrinter prettyPrinter;
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        for (int i = 0; i < transitionNames.size(); i++) {
            prettyPrinter = new PrettyPrinter();
            MCDCTargets.get(i).getPredicate().apply(prettyPrinter);
            stringJoiner.add(transitionNames.get(i) + " [" + prettyPrinter.getPrettyPrint() + " -> "
                    + MCDCTargets.get(i).getTruthValue() + "]");
        }
        return stringJoiner.toString();
    }
}
