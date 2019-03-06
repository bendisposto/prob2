package de.prob.analysis.mcdc;

import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.node.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MCDCASTVisitor extends DepthFirstAdapter {

    private ArrayList<ConcreteMCDCTestCase> tempTestCases = new ArrayList<>();
    private int maxLevel;
    private int currentLevel;

    MCDCASTVisitor(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    ArrayList<ConcreteMCDCTestCase> getMCDCTestCases(PPredicate node) {
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

    @Override
    public void caseAMemberPredicate(AMemberPredicate node)
    {
        inAMemberPredicate(node);
        maxLevelOrLeafReached(node);
        outAMemberPredicate(node);
    }

    @Override
    public void caseADescriptionPredicate(ADescriptionPredicate node)
    {
        inADescriptionPredicate(node);
        maxLevelOrLeafReached(node);
        outADescriptionPredicate(node);
    }

    @Override
    public void caseALabelPredicate(ALabelPredicate node)
    {
        inALabelPredicate(node);
        maxLevelOrLeafReached(node);
        outALabelPredicate(node);
    }

    @Override
    public void caseASubstitutionPredicate(ASubstitutionPredicate node)
    {
        inASubstitutionPredicate(node);
        maxLevelOrLeafReached(node);
        outASubstitutionPredicate(node);
    }

    @Override
    public void caseAForallPredicate(AForallPredicate node)
    {
        inAForallPredicate(node);
        maxLevelOrLeafReached(node);
        outAForallPredicate(node);
    }

    @Override
    public void caseAExistsPredicate(AExistsPredicate node)
    {
        inAExistsPredicate(node);
        maxLevelOrLeafReached(node);
        outAExistsPredicate(node);
    }

    @Override
    public void caseANotMemberPredicate(ANotMemberPredicate node)
    {
        inANotMemberPredicate(node);
        maxLevelOrLeafReached(node);
        outANotMemberPredicate(node);
    }

    @Override
    public void caseASubsetPredicate(ASubsetPredicate node)
    {
        inASubsetPredicate(node);
        maxLevelOrLeafReached(node);
        outASubsetPredicate(node);
    }

    @Override
    public void caseASubsetStrictPredicate(ASubsetStrictPredicate node)
    {
        inASubsetStrictPredicate(node);
        maxLevelOrLeafReached(node);
        outASubsetStrictPredicate(node);
    }

    @Override
    public void caseANotSubsetPredicate(ANotSubsetPredicate node)
    {
        inANotSubsetPredicate(node);
        maxLevelOrLeafReached(node);
        outANotSubsetPredicate(node);
    }

    @Override
    public void caseANotSubsetStrictPredicate(ANotSubsetStrictPredicate node)
    {
        inANotSubsetStrictPredicate(node);
        maxLevelOrLeafReached(node);
        outANotSubsetStrictPredicate(node);
    }

    @Override
    public void caseATruthPredicate(ATruthPredicate node)
    {
        inATruthPredicate(node);
        maxLevelOrLeafReached(node);
        outATruthPredicate(node);
    }

    @Override
    public void caseAFalsityPredicate(AFalsityPredicate node)
    {
        inAFalsityPredicate(node);
        maxLevelOrLeafReached(node);
        outAFalsityPredicate(node);
    }

    @Override
    public void caseAFinitePredicate(AFinitePredicate node)
    {
        inAFinitePredicate(node);
        maxLevelOrLeafReached(node);
        outAFinitePredicate(node);
    }

    @Override
    public void caseAPartitionPredicate(APartitionPredicate node)
    {
        inAPartitionPredicate(node);
        maxLevelOrLeafReached(node);
        outAPartitionPredicate(node);
    }

    @Override
    public void caseADefinitionPredicate(ADefinitionPredicate node)
    {
        inADefinitionPredicate(node);
        maxLevelOrLeafReached(node);
        outADefinitionPredicate(node);
    }

    @Override
    public void caseAPredicateIdentifierPredicate(APredicateIdentifierPredicate node)
    {
        inAPredicateIdentifierPredicate(node);
        maxLevelOrLeafReached(node);
        outAPredicateIdentifierPredicate(node);
    }

    @Override
    public void caseAPredicateFunctionPredicate(APredicateFunctionPredicate node)
    {
        inAPredicateFunctionPredicate(node);
        maxLevelOrLeafReached(node);
        outAPredicateFunctionPredicate(node);
    }

    @Override
    public void caseALetPredicatePredicate(ALetPredicatePredicate node)
    {
        inALetPredicatePredicate(node);
        maxLevelOrLeafReached(node);
        outALetPredicatePredicate(node);
    }

    @Override
    public void caseAIfPredicatePredicate(AIfPredicatePredicate node)
    {
        inAIfPredicatePredicate(node);
        maxLevelOrLeafReached(node);
        outAIfPredicatePredicate(node);
    }

    @Override
    public void caseAExtendedPredPredicate(AExtendedPredPredicate node)
    {
        inAExtendedPredPredicate(node);
        maxLevelOrLeafReached(node);
        outAExtendedPredPredicate(node);
    }

    @Override
    public void caseAOperatorPredicate(AOperatorPredicate node)
    {
        inAOperatorPredicate(node);
        maxLevelOrLeafReached(node);
        outAOperatorPredicate(node);
    }
}
