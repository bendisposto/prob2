package de.prob.analysis.mcdc;

import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.node.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MCDCASTVisitor extends DepthFirstAdapter {

    private ArrayList<ConcreteMCDCTestCase> tempTestCases = new ArrayList<>();
    private int maxLevel;
    private int currentLevel;

    public MCDCASTVisitor(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public ArrayList<ConcreteMCDCTestCase> getMCDCTestCases(PPredicate node) {
        currentLevel = -1;
        node.apply(this);
        return tempTestCases;
    }

    @Override
    public void defaultIn (Node node)
    {
        currentLevel++;
    }

    @Override
    public void defaultOut (Node node)
    {
        currentLevel--;
    }

    private ArrayList<ConcreteMCDCTestCase> processOperatorPredicate(PPredicate node) {
        tempTestCases.clear();
        node.apply(this);
        return new ArrayList<>(tempTestCases);
    }

    private ArrayList<ConcreteMCDCTestCase> filterRequiredTests(ArrayList<ConcreteMCDCTestCase> testCases,
                                                                boolean requiredTest) {
        return testCases.stream()
                .filter(x -> x.getTruthValue() == requiredTest)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void maxLevelOrLeafReached(PPredicate node) {
        tempTestCases.clear();
        tempTestCases.add(new ConcreteMCDCTestCase(node, true));
        tempTestCases.add(new ConcreteMCDCTestCase(new ANegationPredicate(node), false));
    }

    private void addTestCases(ArrayList<ConcreteMCDCTestCase> leftChildTests,
                              ArrayList<ConcreteMCDCTestCase> rightChildTests,
                              ArrayList<AbstractMCDCTestCase> requiredTests) {
        tempTestCases.clear();
        for (AbstractMCDCTestCase required : requiredTests) {
            for (ConcreteMCDCTestCase lct : filterRequiredTests(leftChildTests, required.getLeft())) {
                for (ConcreteMCDCTestCase rct : filterRequiredTests(rightChildTests, required.getRight())) {
                    tempTestCases.add(new ConcreteMCDCTestCase(new AConjunctPredicate
                            (lct.getPredicate(), rct.getPredicate()), required.getTruthValue()));
                }
            }
        }
    }

    @Override
    public void caseAConjunctPredicate(AConjunctPredicate node) {
        inAConjunctPredicate(node);
        if (currentLevel == maxLevel) {
            maxLevelOrLeafReached(node);
        } else {
            ArrayList<ConcreteMCDCTestCase> leftChildTests = processOperatorPredicate(node.getLeft());
            ArrayList<ConcreteMCDCTestCase> rightChildTests = processOperatorPredicate(node.getRight());
            addTestCases(leftChildTests, rightChildTests, AbstractMCDCTestCase.CONJUNCT);
        }
        outAConjunctPredicate(node);
    }

    @Override
    public void caseADisjunctPredicate(final ADisjunctPredicate node) {
        inADisjunctPredicate(node);
        if (currentLevel == maxLevel) {
            maxLevelOrLeafReached(node);
        } else {
            ArrayList<ConcreteMCDCTestCase> leftChildTests = processOperatorPredicate(node.getLeft());
            ArrayList<ConcreteMCDCTestCase> rightChildTests = processOperatorPredicate(node.getRight());
            addTestCases(leftChildTests, rightChildTests, AbstractMCDCTestCase.DISJUNCT);
        }
        outADisjunctPredicate(node);
    }


    @Override
    public void caseAImplicationPredicate(final AImplicationPredicate node) {
        inAImplicationPredicate(node);
        if (currentLevel == maxLevel) {
            maxLevelOrLeafReached(node);
        } else {
            ArrayList<ConcreteMCDCTestCase> leftChildTests = processOperatorPredicate(node.getLeft());
            ArrayList<ConcreteMCDCTestCase> rightChildTests = processOperatorPredicate(node.getRight());
            addTestCases(leftChildTests, rightChildTests, AbstractMCDCTestCase.IMPLICATION);
        }
        outAImplicationPredicate(node);
    }


    @Override
    public void caseAEquivalencePredicate(final AEquivalencePredicate node) {
        inAEquivalencePredicate(node);
        if (currentLevel == maxLevel) {
            maxLevelOrLeafReached(node);
        } else {
            ArrayList<ConcreteMCDCTestCase> leftChildTests = processOperatorPredicate(node.getLeft());
            ArrayList<ConcreteMCDCTestCase> rightChildTests = processOperatorPredicate(node.getRight());
            addTestCases(leftChildTests, rightChildTests, AbstractMCDCTestCase.EQUIVALENCE);
        }
        outAEquivalencePredicate(node);
    }

    @Override
    public void caseANegationPredicate(final ANegationPredicate node) {
        inANegationPredicate(node);
        if (currentLevel == maxLevel) {
            maxLevelOrLeafReached(node);
        } else {
            tempTestCases.clear();
            node.getPredicate().apply(this);
            ArrayList<ConcreteMCDCTestCase> childTests = new ArrayList<>(tempTestCases);
            tempTestCases.clear();
            childTests.forEach(ct -> tempTestCases.add(new ConcreteMCDCTestCase(ct.getPredicate(), !ct.getTruthValue())));
        }
        outANegationPredicate(node);
    }


    @Override
    public void caseAEqualPredicate(final AEqualPredicate node) {
        inAEqualPredicate(node);
        maxLevelOrLeafReached(node);
        outAEqualPredicate(node);
    }

    @Override
    public void caseANotEqualPredicate(final ANotEqualPredicate node) {
        inANotEqualPredicate(node);
        maxLevelOrLeafReached(node);
        outANotEqualPredicate(node);
    }

    @Override
    public void caseALessPredicate(final ALessPredicate node) {
        inALessPredicate(node);
        maxLevelOrLeafReached(node);
        outALessPredicate(node);
    }

    @Override
    public void caseALessEqualPredicate(final ALessEqualPredicate node) {
        inALessEqualPredicate(node);
        maxLevelOrLeafReached(node);
        outALessEqualPredicate(node);
    }

    @Override
    public void caseAGreaterPredicate(final AGreaterPredicate node) {
        inAGreaterPredicate(node);
        maxLevelOrLeafReached(node);
        outAGreaterPredicate(node);
    }

    @Override
    public void caseAGreaterEqualPredicate(final AGreaterEqualPredicate node) {
        inAGreaterEqualPredicate(node);
        maxLevelOrLeafReached(node);
        outAGreaterEqualPredicate(node);
    }
}
