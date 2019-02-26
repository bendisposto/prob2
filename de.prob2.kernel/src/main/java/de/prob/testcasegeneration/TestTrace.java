package de.prob.testcasegeneration;

import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.util.PrettyPrinter;
import de.prob.statespace.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class TestTrace {

    private final List<Transition> priorTransitions;
    private final List<String> allTransitionNames = new ArrayList<>();
    private final List<PPredicate> guards;
    private final boolean isComplete;

    public TestTrace(List<Transition> priorTransitions, String newTransition, List<PPredicate> guards, boolean isComplete) {
        this.priorTransitions = priorTransitions;
        if (!priorTransitions.isEmpty()) {
            priorTransitions.stream().skip(1).forEach(t -> allTransitionNames.add(t.getName()));
            allTransitionNames.add(newTransition);
        }
        this.guards = guards;
        this.isComplete = isComplete;
    }

    public List<PPredicate> getGuards() {
        return guards;
    }

    public List<String> getTransitionNames() {
        return allTransitionNames;
    }

    public String getLastTransition() {
        return allTransitionNames.get(allTransitionNames.size() - 1);
    }

    public int getDepth() {
        return priorTransitions.size();
    }

    public boolean isComplete() {
        return isComplete;
    }

    public String toString() {
        PrettyPrinter prettyPrinter;
        StringJoiner stringJoiner = new StringJoiner(", ", "[", "]");
        for (int i = 0; i < allTransitionNames.size(); i++) {
            prettyPrinter = new PrettyPrinter();
            guards.get(i).apply(prettyPrinter);
            stringJoiner.add(allTransitionNames.get(i) + " (" + prettyPrinter.getPrettyPrint() + ")");
        }
        return stringJoiner.toString();
    }
}