package de.prob.testcasegeneration;

import java.util.ArrayList;
import java.util.Arrays;

public class AbstractMCDCTestCase {

    static final ArrayList<AbstractMCDCTestCase> CONJUNCT = new ArrayList<>(Arrays.asList(
            new AbstractMCDCTestCase(true, true, true),
            new AbstractMCDCTestCase(true, false, false),
            new AbstractMCDCTestCase(false, true, false)));
    static final ArrayList<AbstractMCDCTestCase> DISJUNCT = new ArrayList<>(Arrays.asList(
            new AbstractMCDCTestCase(true, false, true),
            new AbstractMCDCTestCase(false, true, true),
            new AbstractMCDCTestCase(false, false, false)));
    static final ArrayList<AbstractMCDCTestCase> IMPLICATION = new ArrayList<>(Arrays.asList(
            new AbstractMCDCTestCase(true, true, true),
            new AbstractMCDCTestCase(false, false, true),
            new AbstractMCDCTestCase(true, false, false)));
    static final ArrayList<AbstractMCDCTestCase> EQUIVALENCE = new ArrayList<>(Arrays.asList(
            new AbstractMCDCTestCase(true, true, true),
            new AbstractMCDCTestCase(false, false, true),
            new AbstractMCDCTestCase(false, true, false)));

    private final boolean left;
    private final boolean right;
    private final boolean truthValue;

    public AbstractMCDCTestCase(boolean left, boolean right, boolean truthValue){
        this.left = left;
        this.right = right;
        this.truthValue = truthValue;
    }

    public boolean getTruthValue() {
        return truthValue;
    }

    public boolean getLeft() {
        return left;
    }

    public boolean getRight() {
        return right;
    }

    public String toString() {
        return String.valueOf(left).substring(0, 1) + String.valueOf(right).substring(0, 1) + " -> " + String.valueOf(truthValue);
    }
}
