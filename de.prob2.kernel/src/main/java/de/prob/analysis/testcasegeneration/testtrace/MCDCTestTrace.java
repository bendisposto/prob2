package de.prob.analysis.testcasegeneration.testtrace;

import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.util.PrettyPrinter;
import de.prob.analysis.testcasegeneration.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class MCDCTestTrace extends TestTrace {

    private final List<PPredicate> guards;

    public MCDCTestTrace(List<String> priorTransitions, String newTransition, List<PPredicate> guards, boolean isComplete) {
        super(priorTransitions, newTransition, isComplete);
        this.guards = guards;
    }

    private List<PPredicate> getGuards() {
        return guards;
    }

    public MCDCTestTrace createNewTrace(List<String> priorTransitions, TestCase t, boolean isComplete) {
        List<PPredicate> newGuardList = new ArrayList<>(getGuards());
        newGuardList.add(t.getGuard());
        return new MCDCTestTrace(priorTransitions, t.getOperation(), newGuardList, isComplete);
    }

    public String toString() {
        PrettyPrinter prettyPrinter;
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        for (int i = 0; i < transitionNames.size(); i++) {
            prettyPrinter = new PrettyPrinter();
            guards.get(i).apply(prettyPrinter);
            stringJoiner.add(transitionNames.get(i) + " [" + prettyPrinter.getPrettyPrint() + "]");
        }
        return stringJoiner.toString();
    }
}
