package de.prob.analysis.mcdc;

import java.util.ArrayList;
import java.util.Arrays;

public class AbstractMCDCTestCase {

    private final boolean left;
    private final boolean right;
    private final boolean truthValue;

    private AbstractMCDCTestCase(boolean left, boolean right, boolean truthValue){
        this.left = left;
        this.right = right;
        this.truthValue = truthValue;
    }

    boolean getTruthValue() {
        return truthValue;
    }

    boolean getLeft() {
        return left;
    }

    boolean getRight() {
        return right;
    }

    public String toString() {
        return String.valueOf(left).substring(0, 1) + String.valueOf(right).substring(0, 1) + " -> " + truthValue;
    }

    public static ArrayList<AbstractMCDCTestCase> getAbstractMCDCTestCases(String operator) {
        switch (operator) {
            case "CONJUNCT":
                return new ArrayList<>(Arrays.asList(
                        new AbstractMCDCTestCase(true, true, true),
                        new AbstractMCDCTestCase(true, false, false),
                        new AbstractMCDCTestCase(false, true, false)));
            case "DISJUNCT":
                return new ArrayList<>(Arrays.asList(
                        new AbstractMCDCTestCase(true, false, true),
                        new AbstractMCDCTestCase(false, true, true),
                        new AbstractMCDCTestCase(false, false, false)));
            case "IMPLICATION":
                return new ArrayList<>(Arrays.asList(
                        new AbstractMCDCTestCase(true, true, true),
                        new AbstractMCDCTestCase(false, false, true),
                        new AbstractMCDCTestCase(true, false, false)));
            case "EQUIVALENCE":
                return new ArrayList<>(Arrays.asList(
                        new AbstractMCDCTestCase(true, true, true),
                        new AbstractMCDCTestCase(false, false, true),
                        new AbstractMCDCTestCase(false, true, false)));
            default: return new ArrayList<>();
        }
    }
}
