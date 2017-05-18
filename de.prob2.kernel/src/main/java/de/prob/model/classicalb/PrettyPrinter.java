package de.prob.model.classicalb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.node.*;

public class PrettyPrinter extends DepthFirstAdapter {

	HashMap<Class<? extends Node>, Integer> prio = new HashMap<Class<? extends Node>, Integer>();
	private static final int PRIORITY20 = 20;
	private static final int PRIORITY30 = 30;
	private static final int PRIORITY40 = 40;
	private static final int PRIORITY60 = 60;
	private static final int PRIORITY125 = 125;
	private static final int PRIORITY160 = 160;
	private static final int PRIORITY170 = 170;
	private static final int PRIORITY180 = 180;
	private static final int PRIORITY190 = 190;
	private static final int PRIORITY200 = 200;
	private static final int PRIORITY210 = 210;
	private static final int PRIORITY230 = 230;
	private static final int PRIORITY231 = 231;

	public void setup() {
		prio.put(AParallelProductExpression.class, PRIORITY20);
		prio.put(ARelationsExpression.class, PRIORITY125);
		prio.put(ATotalFunctionExpression.class, PRIORITY125);
		prio.put(APartialInjectionExpression.class, PRIORITY125);
		prio.put(ATotalInjectionExpression.class, PRIORITY125);
		prio.put(APartialSurjectionExpression.class, PRIORITY125);
		prio.put(ATotalSurjectionExpression.class, PRIORITY125);
		prio.put(APartialBijectionExpression.class, PRIORITY125);
		prio.put(ATotalBijectionExpression.class, PRIORITY125);
		prio.put(ATotalRelationExpression.class, PRIORITY125);
		prio.put(ATotalSurjectionRelationExpression.class, PRIORITY125);
		prio.put(AOverwriteExpression.class, PRIORITY160);
		prio.put(ADirectProductExpression.class, PRIORITY160);
		prio.put(AConcatExpression.class, PRIORITY160);
		prio.put(ADomainRestrictionExpression.class, PRIORITY160);
		prio.put(ADomainSubtractionExpression.class, PRIORITY160);
		prio.put(ARangeRestrictionExpression.class, PRIORITY160);
		prio.put(ARangeSubtractionExpression.class, PRIORITY160);
		prio.put(AInsertFrontExpression.class, PRIORITY160);
		prio.put(AInsertTailExpression.class, PRIORITY160);
		prio.put(AUnionExpression.class, PRIORITY160);
		prio.put(AIntersectionExpression.class, PRIORITY160);
		prio.put(ARestrictFrontExpression.class, PRIORITY160);
		prio.put(ARestrictTailExpression.class, PRIORITY160);
		prio.put(ACoupleExpression.class, PRIORITY160);
		prio.put(AIntervalExpression.class, PRIORITY170);
		prio.put(AMinusOrSetSubtractExpression.class, PRIORITY180);
		prio.put(AAddExpression.class, PRIORITY180);
		prio.put(ASetSubtractionExpression.class, PRIORITY180);
		prio.put(AMultiplicationExpression.class, PRIORITY190);
		prio.put(AMultOrCartExpression.class, PRIORITY190);
		prio.put(ADivExpression.class, PRIORITY190);
		prio.put(AModuloExpression.class, PRIORITY190);
		prio.put(APowerOfExpression.class, PRIORITY200); // right associative
		prio.put(AUnaryMinusExpression.class, PRIORITY210);
		prio.put(AReverseExpression.class, PRIORITY230);
		prio.put(AImageExpression.class, PRIORITY231);
		prio.put(AImplicationPredicate.class, PRIORITY30);
		prio.put(ADisjunctPredicate.class, PRIORITY40);
		prio.put(AConjunctPredicate.class, PRIORITY40);
		prio.put(AEquivalencePredicate.class, PRIORITY60);
	}

	public PrettyPrinter() {
		setup();
	}

	private final StringBuilder sb = new StringBuilder();

	public String getPrettyPrint() {
		return sb.toString();
	}

	@Override
	public void caseAAssignSubstitution(AAssignSubstitution node) {
		LinkedList<PExpression> lhs = node.getLhsExpression();
		commaSeparatedExpressionList(lhs);
		sb.append(":=");
		LinkedList<PExpression> rhs = node.getRhsExpressions();
		commaSeparatedExpressionList(rhs);
	}

	@Override
	public void caseASkipSubstitution(ASkipSubstitution node) {
		sb.append(" skip ");
	}

	@Override
	public void caseABecomesElementOfSubstitution(
			ABecomesElementOfSubstitution node) {
		commaSeparatedExpressionList(node.getIdentifiers());
		sb.append("::");
		node.getSet().apply(this);

	}

	@Override
	public void caseABecomesSuchSubstitution(ABecomesSuchSubstitution node) {
		commaSeparatedExpressionList(node.getIdentifiers());
		sb.append(" : (");

		node.getPredicate().apply(this);
		sb.append(") ");
	}

	@Override
	public void caseAOperationCallSubstitution(AOperationCallSubstitution node) {
		commaSeparatedExpressionList(node.getResultIdentifiers());
		sb.append("<--");
		ArrayList<TIdentifierLiteral> copy = new ArrayList<TIdentifierLiteral>(
				node.getOperation());
		for (final Iterator<TIdentifierLiteral> iterator = copy.iterator(); iterator
				.hasNext();) {
			final TIdentifierLiteral e = iterator.next();
			e.apply(this);
		}
		if (!node.getParameters().isEmpty()) {
			sb.append("(");
			commaSeparatedExpressionList(node.getParameters());
			sb.append(")");
		}
	}

	@Override
	public void caseAParallelSubstitution(AParallelSubstitution node) {
		List<PSubstitution> copy = new ArrayList<PSubstitution>(
				node.getSubstitutions());
		copy.get(0).apply(this);
		for (int i = 1; i < copy.size(); i++) {
			sb.append(" || ");
			copy.get(i).apply(this);
		}
	}

	@Override
	public void caseASequenceSubstitution(ASequenceSubstitution node) {
		List<PSubstitution> copy = new ArrayList<PSubstitution>(
				node.getSubstitutions());
		copy.get(0).apply(this);
		for (int i = 1; i < copy.size(); i++) {
			sb.append(" ; ");
			copy.get(i).apply(this);
		}
	}

	@Override
	public void caseAAnySubstitution(AAnySubstitution node) {
		sb.append("ANY ");
		commaSeparatedExpressionList(node.getIdentifiers());
		sb.append(" WHERE ");
		node.getWhere().apply(this);
		sb.append(" THEN ");
		node.getThen().apply(this);
		sb.append(" END ");
	}

	@Override
	public void caseALetSubstitution(ALetSubstitution node) {
		sb.append("LET ");
		commaSeparatedExpressionList(node.getIdentifiers());
		sb.append(" BE ");
		node.getPredicate().apply(this);
		sb.append(" IN ");
		node.getSubstitution().apply(this);
		sb.append(" END ");
	}

	@Override
	public void caseAVarSubstitution(AVarSubstitution node) {
		sb.append("VAR ");
		commaSeparatedExpressionList(node.getIdentifiers());
		sb.append(" IN ");
		node.getSubstitution().apply(this);
		sb.append(" END ");
	}

	@Override
	public void caseAPreconditionSubstitution(APreconditionSubstitution node) {
		sb.append("PRE ");
		node.getPredicate().apply(this);
		sb.append(" THEN ");
		node.getSubstitution().apply(this);
		sb.append(" END ");
	}

	@Override
	public void caseAAssertionSubstitution(AAssertionSubstitution node) {
		sb.append("ASSERT ");
		node.getPredicate().apply(this);
		sb.append(" THEN ");
		node.getSubstitution().apply(this);
		sb.append(" END ");
	}

	@Override
	public void caseAChoiceSubstitution(AChoiceSubstitution node) {
		sb.append("CHOICE ");
		List<PSubstitution> copy = new ArrayList<PSubstitution>(
				node.getSubstitutions());
		for (PSubstitution e : copy) {
			e.apply(this);
		}
		sb.append(" END ");
	}

	@Override
	public void caseAChoiceOrSubstitution(AChoiceOrSubstitution node) {
		sb.append(" OR ");
		node.getSubstitution().apply(this);
	}

	@Override
	public void caseASelectWhenSubstitution(ASelectWhenSubstitution node) {
		sb.append(" WHEN ");
		node.getCondition().apply(this);
		sb.append(" THEN ");
		node.getSubstitution().apply(this);
	}

	@Override
	public void caseASelectSubstitution(ASelectSubstitution node) {
		sb.append("SELECT ");
		node.getCondition().apply(this);
		sb.append(" THEN ");
		node.getThen().apply(this);
		{
			List<PSubstitution> copy = new ArrayList<PSubstitution>(
					node.getWhenSubstitutions());
			for (PSubstitution e : copy) {
				e.apply(this);
			}
		}
		if (node.getElse() != null) {
			sb.append(" ELSE ");
			node.getElse().apply(this);
		}
		sb.append(" END ");
	}

	@Override
	public void caseAIfElsifSubstitution(AIfElsifSubstitution node) {
		sb.append(" ELSIF ");
		node.getCondition().apply(this);
		sb.append(" THEN ");
		node.getThenSubstitution().apply(this);
	}

	@Override
	public void caseAIfSubstitution(AIfSubstitution node) {
		sb.append("IF ");
		node.getCondition().apply(this);
		sb.append(" THEN ");
		node.getThen().apply(this);
		{
			List<PSubstitution> copy = new ArrayList<PSubstitution>(
					node.getElsifSubstitutions());
			for (PSubstitution e : copy) {
				e.apply(this);
			}
		}
		if (node.getElse() != null) {
			sb.append(" ELSE ");
			node.getElse().apply(this);
		}
		sb.append(" END ");
	}

	@Override
	public void caseACaseOrSubstitution(ACaseOrSubstitution node) {
		sb.append(" OR ");
		commaSeparatedExpressionList(node.getExpressions());
		sb.append(" THEN ");
		node.getSubstitution().apply(this);
	}

	@Override
	public void caseACaseSubstitution(ACaseSubstitution node) {
		sb.append("CASE ");
		node.getExpression().apply(this);
		sb.append(" OF EITHER ");
		commaSeparatedExpressionList(node.getEitherExpr());
		sb.append(" THEN ");
		node.getEitherSubst().apply(this);
		List<PSubstitution> copy = new ArrayList<PSubstitution>(
				node.getOrSubstitutions());
		for (PSubstitution e : copy)
			e.apply(this);
		if (node.getElse() != null) {
			sb.append(" ELSE ");
			node.getElse().apply(this);
		}
		sb.append(" END END ");
	}

	@Override
	public void caseAWhileSubstitution(AWhileSubstitution node) {
		sb.append("WHILE ");
		node.getCondition().apply(this);
		sb.append(" DO ");
		node.getDoSubst().apply(this);
		sb.append(" INVARIANT ");
		node.getInvariant().apply(this);
		sb.append(" VARIANT ");
		node.getVariant().apply(this);
		sb.append(" END ");
	}

	private void commaSeparatedExpressionList(List<PExpression> list) {
		list.get(0).apply(this);
		for (int i = 1; i < list.size(); i++) {
			sb.append(",");
			list.get(i).apply(this);
		}
	}

	public void leftParAssoc(final Node node, final Node right) {
		Integer priorityNode = prio.get(node.getClass());
		Integer priorityRight = prio.get(right.getClass());
		if (priorityNode != null && priorityRight != null
				&& priorityRight < priorityNode) {  // we do not insert parentheses when priority the same
			sb.append("(");
		}
	}

	public void rightParAssoc(final Node node, final Node right) {
		Integer priorityNode = prio.get(node.getClass());
		Integer priorityRight = prio.get(right.getClass());
		if (priorityNode != null && priorityRight != null
				&& priorityRight < priorityNode) {
			sb.append(")");
		}
	}

	public void leftPar(final Node node, final Node right) {
		Integer priorityNode = prio.get(node.getClass());
		Integer priorityRight = prio.get(right.getClass());
		if (priorityNode != null && priorityRight != null
				&& priorityRight <= priorityNode) {
			sb.append("(");
		}
	}

	public void rightPar(final Node node, final Node right) {
		Integer priorityNode = prio.get(node.getClass());
		Integer priorityRight = prio.get(right.getClass());
		if (priorityNode != null && priorityRight != null
				&& priorityRight <= priorityNode) {
			sb.append(")");
		}
	}

	public void applyLeftAssociative(final Node left, final Node node,
			final Node right, final String operatorStr) {
		if (left != null) {
			leftParAssoc(node, left);
			left.apply(this);
			rightParAssoc(node, left);
		}

		sb.append(operatorStr);

		if (right != null) {
			leftPar(node, right);
			right.apply(this);
			rightPar(node, right);
		}
	}

	public void applyRightAssociative(final Node left, final Node node,
			final Node right, final String operatorStr) {
		if (left != null) {
			leftPar(node, left);
			left.apply(this);
			rightPar(node, left);
		}

		sb.append(operatorStr);

		if (right != null) {
			leftParAssoc(node, right);
			right.apply(this);
			rightParAssoc(node, right);
		}
	}

	@Override
	public void caseAPowerOfExpression(final APowerOfExpression node) {
		applyRightAssociative(node.getLeft(), node, node.getRight(), "**");
	}

	@Override
	public void caseAIntegerExpression(final AIntegerExpression node) {
		sb.append(node.getLiteral().getText());
	}

	@Override
	public void caseAAddExpression(final AAddExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "+");
	}

	@Override
	public void caseAMinusOrSetSubtractExpression(
			final AMinusOrSetSubtractExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "-");
	}

	@Override
	public void caseASetSubtractionExpression(
			final ASetSubtractionExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "\\");
	}

	@Override
	public void caseAMultOrCartExpression(final AMultOrCartExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "*");
	}

	@Override
	public void caseADivExpression(final ADivExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "/");
	}

	@Override
	public void caseAModuloExpression(final AModuloExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), " mod ");
	}

	@Override
	public void caseARelationsExpression(final ARelationsExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "<->");
	}

	@Override
	public void caseAPartialFunctionExpression(
			final APartialFunctionExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "+->");
	}

	@Override
	public void caseATotalFunctionExpression(final ATotalFunctionExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "-->");
	}

	@Override
	public void caseAPartialInjectionExpression(
			final APartialInjectionExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), ">+>");
	}

	@Override
	public void caseATotalInjectionExpression(
			final ATotalInjectionExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), ">->");
	}

	@Override
	public void caseAPartialSurjectionExpression(
			final APartialSurjectionExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "+->>");
	}

	@Override
	public void caseATotalSurjectionExpression(
			final ATotalSurjectionExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "-->>");
	}

	@Override
	public void caseAPartialBijectionExpression(
			final APartialBijectionExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), ">+>>");
	}

	@Override
	public void caseATotalBijectionExpression(
			final ATotalBijectionExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), ">->>");
	}

	@Override
	public void caseATotalRelationExpression(final ATotalRelationExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "<<->");
	}

	@Override
	public void caseASurjectionRelationExpression(
			final ASurjectionRelationExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "<->>");
	}

	@Override
	public void caseATotalSurjectionRelationExpression(
			final ATotalSurjectionRelationExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "<<->>");
	}

	@Override
	public void caseAOverwriteExpression(final AOverwriteExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "<+");
	}

	@Override
	public void caseADirectProductExpression(final ADirectProductExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "><");
	}

	@Override
	public void caseAConcatExpression(final AConcatExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "^");
	}

	@Override
	public void caseADomainRestrictionExpression(
			final ADomainRestrictionExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "<|");
	}

	@Override
	public void caseADomainSubtractionExpression(
			final ADomainSubtractionExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "<<|");
	}

	@Override
	public void caseARangeRestrictionExpression(
			final ARangeRestrictionExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "|>");
	}

	@Override
	public void caseARangeSubtractionExpression(
			final ARangeSubtractionExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "|>>");
	}

	@Override
	public void caseAInsertFrontExpression(final AInsertFrontExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "->");
	}

	@Override
	public void caseAInsertTailExpression(final AInsertTailExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "<-");
	}

	@Override
	public void caseAUnionExpression(final AUnionExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "\\/");
	}

	@Override
	public void caseAIntersectionExpression(final AIntersectionExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "/\\");
	}

	@Override
	public void caseARestrictFrontExpression(final ARestrictFrontExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "/|\\");
	}

	@Override
	public void caseARestrictTailExpression(final ARestrictTailExpression node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "\\|/");
	}

	@Override
	public void caseACoupleExpression(final ACoupleExpression node) {
		final List<PExpression> copy = new ArrayList<PExpression>(
				node.getList());
		sb.append("(");
		copy.get(0).apply(this);
		sb.append(",");
		copy.get(1).apply(this);
		sb.append(")");
	}

	@Override
	public void caseAIdentifierExpression(final AIdentifierExpression node) {
		final List<TIdentifierLiteral> copy = new ArrayList<TIdentifierLiteral>(
				node.getIdentifier());
		for (final Iterator<TIdentifierLiteral> iterator = copy.iterator(); iterator
				.hasNext();) {
			final TIdentifierLiteral e = iterator.next();
			e.apply(this);
		}
	}

	@Override
	public void caseAIntervalExpression(final AIntervalExpression node) {
		applyLeftAssociative(node.getLeftBorder(), node, node.getRightBorder(),
				"..");
	}

	@Override
	public void caseAUnaryMinusExpression(final AUnaryMinusExpression node) {
		sb.append("-");

		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
	}

	@Override
	public void caseAReverseExpression(final AReverseExpression node) {
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append("~");
	}

	@Override
	public void caseAImageExpression(final AImageExpression node) {
		if (node.getLeft() != null) {
			node.getLeft().apply(this);
		}

		sb.append("[");

		if (node.getRight() != null) {
			node.getRight().apply(this);
		}
		sb.append("]");

	}

	@Override
	public void caseAParallelProductExpression(
			final AParallelProductExpression node) {
		sb.append("(");

		if (node.getLeft() != null) {
			node.getLeft().apply(this);
		}

		sb.append("||");

		if (node.getRight() != null) {
			node.getRight().apply(this);
		}

		sb.append(")");
	}

	@Override
	public void caseACompositionExpression(final ACompositionExpression node) {
		sb.append("(");
		if (node.getLeft() != null) {
			node.getLeft().apply(this);
		}

		sb.append(";");

		if (node.getRight() != null) {
			node.getRight().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAConvertBoolExpression(final AConvertBoolExpression node) {
		sb.append("bool(");
		if (node.getPredicate() != null) {
			node.getPredicate().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseALessPredicate(final ALessPredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "<");
	}

	@Override
	public void caseAMaxExpression(final AMaxExpression node) {
		sb.append("max(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseASetExtensionExpression(final ASetExtensionExpression node) {
		sb.append("{");
		final List<PExpression> copy = new ArrayList<PExpression>(
				node.getExpressions());
		for (final Iterator<PExpression> iterator = copy.iterator(); iterator
				.hasNext();) {
			final PExpression e = iterator.next();
			e.apply(this);

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("}");
	}

	@Override
	public void caseAMinExpression(final AMinExpression node) {
		sb.append("min(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseACardExpression(final ACardExpression node) {
		sb.append("card(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAGeneralSumExpression(final AGeneralSumExpression node) {
		sb.append("SIGMA");
		final List<PExpression> copy = new ArrayList<PExpression>(
				node.getIdentifiers());
		sb.append("(");
		for (final Iterator<PExpression> iterator = copy.iterator(); iterator
				.hasNext();) {
			final PExpression e = iterator.next();
			e.apply(this);

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append(").(");
		if (node.getPredicates() != null) {
			node.getPredicates().apply(this);
		}
		sb.append("|");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAGeneralProductExpression(
			final AGeneralProductExpression node) {
		sb.append("PI");
		final List<PExpression> copy = new ArrayList<PExpression>(
				node.getIdentifiers());
		sb.append("(");
		for (final Iterator<PExpression> iterator = copy.iterator(); iterator
				.hasNext();) {
			final PExpression e = iterator.next();
			e.apply(this);

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append(").(");
		if (node.getPredicates() != null) {
			node.getPredicates().apply(this);
		}
		sb.append("|");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAConjunctPredicate(final AConjunctPredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "&");
	}

	@Override
	public void caseAPowSubsetExpression(final APowSubsetExpression node) {
		sb.append("POW(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAPow1SubsetExpression(final APow1SubsetExpression node) {
		sb.append("POW1(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAFinSubsetExpression(final AFinSubsetExpression node) {
		sb.append("FIN(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAFin1SubsetExpression(final AFin1SubsetExpression node) {
		sb.append("FIN1(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAGeneralUnionExpression(final AGeneralUnionExpression node) {
		sb.append("union(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAGeneralIntersectionExpression(
			final AGeneralIntersectionExpression node) {
		sb.append("inter(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAIdentityExpression(final AIdentityExpression node) {
		sb.append("id(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAReflexiveClosureExpression(
			final AReflexiveClosureExpression node) {
		sb.append("closure(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAClosureExpression(final AClosureExpression node) {
		sb.append("closure1(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseADomainExpression(final ADomainExpression node) {
		sb.append("dom(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseARangeExpression(final ARangeExpression node) {
		sb.append("ran(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseALambdaExpression(final ALambdaExpression node) {
		sb.append("%");
		final List<PExpression> copy = new ArrayList<PExpression>(
				node.getIdentifiers());
		for (final Iterator<PExpression> iterator = copy.iterator(); iterator
				.hasNext();) {
			final PExpression e = iterator.next();
			e.apply(this);

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		sb.append(".(");
		if (node.getPredicate() != null) {
			node.getPredicate().apply(this);
		}
		sb.append("|");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseATransFunctionExpression(final ATransFunctionExpression node) {
		sb.append("fnc(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseATransRelationExpression(final ATransRelationExpression node) {
		sb.append("rel(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseASeqExpression(final ASeqExpression node) {
		sb.append("seq(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseASeq1Expression(final ASeq1Expression node) {
		sb.append("seq1(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAIseqExpression(final AIseqExpression node) {
		sb.append("iseq(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAIseq1Expression(final AIseq1Expression node) {
		sb.append("iseq1(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAPermExpression(final APermExpression node) {
		sb.append("perm(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAEmptySequenceExpression(final AEmptySequenceExpression arg0) {
		sb.append("[]");
	}

	@Override
	public void caseASizeExpression(final ASizeExpression node) {
		sb.append("size(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAFirstExpression(final AFirstExpression node) {
		sb.append("first(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseALastExpression(final ALastExpression node) {
		sb.append("last(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAFrontExpression(final AFrontExpression node) {
		sb.append("front(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseATailExpression(final ATailExpression node) {
		sb.append("tail(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseARevExpression(final ARevExpression node) {
		sb.append("rev(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAFirstProjectionExpression(
			final AFirstProjectionExpression node) {
		sb.append("prj1(");
		if (node.getExp1() != null) {
			node.getExp1().apply(this);
		}

		sb.append(",");

		if (node.getExp2() != null) {
			node.getExp2().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseASecondProjectionExpression(
			final ASecondProjectionExpression node) {
		sb.append("prj2(");
		if (node.getExp1() != null) {
			node.getExp1().apply(this);
		}

		sb.append(",");

		if (node.getExp2() != null) {
			node.getExp2().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAIterationExpression(final AIterationExpression node) {
		sb.append("iterate(");
		if (node.getLeft() != null) {
			node.getLeft().apply(this);
		}
		sb.append(",");
		if (node.getRight() != null) {
			node.getRight().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAComprehensionSetExpression(
			final AComprehensionSetExpression node) {
		sb.append("{");
		final List<PExpression> copy = new ArrayList<PExpression>(
				node.getIdentifiers());
		for (final Iterator<PExpression> iterator = copy.iterator(); iterator
				.hasNext();) {
			final PExpression e = iterator.next();
			e.apply(this);

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		sb.append("|");
		if (node.getPredicates() != null) {
			node.getPredicates().apply(this);
		}
		sb.append("}");
	}

	@Override
	public void caseTIdentifierLiteral(final TIdentifierLiteral node) {
		if (node.getText() != null) {
			sb.append(node.getText());
		}
	}

	@Override
	public void caseAQuantifiedUnionExpression(
			final AQuantifiedUnionExpression node) {
		sb.append("UNION(");
		final List<PExpression> copy = new ArrayList<PExpression>(
				node.getIdentifiers());
		for (final Iterator<PExpression> iterator = copy.iterator(); iterator
				.hasNext();) {
			final PExpression e = iterator.next();
			e.apply(this);

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append(").(");
		if (node.getPredicates() != null) {
			node.getPredicates().apply(this);
		}
		sb.append("|");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAQuantifiedIntersectionExpression(
			final AQuantifiedIntersectionExpression node) {
		sb.append("INTER(");
		final List<PExpression> copy = new ArrayList<PExpression>(
				node.getIdentifiers());
		for (final Iterator<PExpression> iterator = copy.iterator(); iterator
				.hasNext();) {
			final PExpression e = iterator.next();
			e.apply(this);

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append(").(");
		if (node.getPredicates() != null) {
			node.getPredicates().apply(this);
		}
		sb.append("|");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseASequenceExtensionExpression(
			final ASequenceExtensionExpression node) {
		final List<PExpression> copy = new ArrayList<PExpression>(
				node.getExpression());
		sb.append("[");
		for (final Iterator<PExpression> iterator = copy.iterator(); iterator
				.hasNext();) {
			final PExpression e = iterator.next();
			e.apply(this);

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("]");
	}

	@Override
	public void caseAGeneralConcatExpression(final AGeneralConcatExpression node) {
		sb.append("conc(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseABooleanTrueExpression(final ABooleanTrueExpression arg0) {
		sb.append("TRUE");
	}

	@Override
	public void caseABooleanFalseExpression(final ABooleanFalseExpression arg0) {
		sb.append("FALSE");
	}

	@Override
	public void caseAMaxIntExpression(final AMaxIntExpression arg0) {
		sb.append("MAXINT");
	}

	@Override
	public void caseAMinIntExpression(final AMinIntExpression arg0) {
		sb.append("MININT");
	}

	@Override
	public void caseAEmptySetExpression(final AEmptySetExpression node) {
		sb.append("{}");
	}

	@Override
	public void caseAIntegerSetExpression(final AIntegerSetExpression arg0) {
		sb.append("INTEGER");
	}

	@Override
	public void caseANaturalSetExpression(final ANaturalSetExpression arg0) {
		sb.append("NATURAL");
	}

	@Override
	public void caseANatural1SetExpression(final ANatural1SetExpression arg0) {
		sb.append("NATURAL1");
	}

	@Override
	public void caseANatSetExpression(final ANatSetExpression arg0) {
		sb.append("NAT");
	}

	@Override
	public void caseANat1SetExpression(final ANat1SetExpression arg0) {
		sb.append("NAT1");
	}

	@Override
	public void caseAIntSetExpression(final AIntSetExpression arg0) {
		sb.append("INT");
	}

	@Override
	public void caseABoolSetExpression(final ABoolSetExpression arg0) {
		sb.append("BOOL");
	}

	@Override
	public void caseAStringSetExpression(final AStringSetExpression arg0) {
		sb.append("STRING");
	}

	@Override
	public void caseAImplicationPredicate(final AImplicationPredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), " => ");
	}

	@Override
	public void caseADisjunctPredicate(final ADisjunctPredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), " or ");
	}

	@Override
	public void caseAEquivalencePredicate(final AEquivalencePredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), " <=> ");

	}

	@Override
	public void caseAEqualPredicate(final AEqualPredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "=");
	}

	@Override
	public void caseAMemberPredicate(final AMemberPredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), ":");
	}

	@Override
	public void caseASubsetPredicate(final ASubsetPredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "<:");

	}

	@Override
	public void caseASubsetStrictPredicate(final ASubsetStrictPredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "<<:");
	}

	@Override
	public void caseANotSubsetPredicate(final ANotSubsetPredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "/<:");

	}

	@Override
	public void caseANotSubsetStrictPredicate(
			final ANotSubsetStrictPredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "/<<:");

	}

	@Override
	public void caseANotEqualPredicate(final ANotEqualPredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "/=");
	}

	@Override
	public void caseANotMemberPredicate(final ANotMemberPredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "/:");

	}

	@Override
	public void caseALessEqualPredicate(final ALessEqualPredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), "<=");

	}

	@Override
	public void caseAGreaterEqualPredicate(final AGreaterEqualPredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), ">=");
	}

	@Override
	public void caseAGreaterPredicate(final AGreaterPredicate node) {
		applyLeftAssociative(node.getLeft(), node, node.getRight(), ">");
	}

	@Override
	public void caseAForallPredicate(final AForallPredicate node) {
		sb.append("!");
		final List<PExpression> copy = new ArrayList<PExpression>(
				node.getIdentifiers());

		for (final Iterator<PExpression> iterator = copy.iterator(); iterator
				.hasNext();) {
			final PExpression e = iterator.next();
			e.apply(this);

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		sb.append(".(");
		if (node.getImplication() != null) {
			node.getImplication().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAExistsPredicate(final AExistsPredicate node) {
		sb.append("#");
		final List<PExpression> copy = new ArrayList<PExpression>(
				node.getIdentifiers());

		for (final Iterator<PExpression> iterator = copy.iterator(); iterator
				.hasNext();) {
			final PExpression e = iterator.next();
			e.apply(this);

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		sb.append(".(");
		if (node.getPredicate() != null) {
			node.getPredicate().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseANegationPredicate(final ANegationPredicate node) {
		sb.append("not(");
		if (node.getPredicate() != null) {
			node.getPredicate().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAStringExpression(final AStringExpression node) {
		sb.append("\"");
		if (node.getContent() != null) {
			sb.append(node.getContent().getText());
		}
		sb.append("\"");

	}

	@Override
	public void caseASuccessorExpression(final ASuccessorExpression node) {
		sb.append("succ");
	}

	@Override
	public void caseAPredecessorExpression(final APredecessorExpression node) {
		sb.append("pred");
	}

	@Override
	public void caseADefinitionExpression(final ADefinitionExpression node) {
		String defLiteral = node.getDefLiteral().getText();
		sb.append(defLiteral);
		if (!node.getParameters().isEmpty()) {
			sb.append("(");
			printExprList(node.getParameters());
			sb.append(")");
		}
	}

	private void printExprList(final LinkedList<PExpression> parameters) {
		for (final Iterator<PExpression> iterator = parameters.iterator(); iterator.hasNext();) {
			final PExpression e = iterator.next();
			e.apply(this);
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
	}

	@Override
	public void caseADefinitionPredicate(final ADefinitionPredicate node) {
		String defLiteral = node.getDefLiteral().getText();
		sb.append(defLiteral);
		if (!node.getParameters().isEmpty()) {
			sb.append("(");
			printExprList(node.getParameters());
			sb.append(")");
		}
	}

	@Override
	public void caseAFunctionExpression(final AFunctionExpression node) {
		node.getIdentifier().apply(this);
		if (!node.getParameters().isEmpty()) {
			sb.append("(");
			printExprList(node.getParameters());
			sb.append(")");
		}
	}

	@Override
	public void caseAStructExpression(final AStructExpression node) {
		sb.append("struct(");
		processEntries(node.getEntries());
		sb.append(")");
	}

	@Override
	public void caseARecExpression(final ARecExpression node) {
		sb.append("rec(");
		processEntries(node.getEntries());
		sb.append(")");
	}

	private void processEntries(final List<PRecEntry> list) {
		for (final Iterator<PRecEntry> iterator = list.iterator(); iterator
				.hasNext();) {
			final PRecEntry e = iterator.next();
			e.apply(this);
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
	}

	@Override
	public void caseARecEntry(final ARecEntry node) {
		node.getIdentifier().apply(this);
		sb.append(":");
		node.getValue().apply(this);
	}

	@Override
	public void caseARecordFieldExpression(final ARecordFieldExpression node) {
		node.getRecord().apply(this);
		sb.append("'");
		node.getIdentifier().apply(this);
	}

	@Override
	public void caseAEnumeratedSetSet(final AEnumeratedSetSet node) {
		final List<TIdentifierLiteral> copy = new ArrayList<TIdentifierLiteral>(
				node.getIdentifier());

		for (final Iterator<TIdentifierLiteral> iterator = copy.iterator(); iterator
				.hasNext();) {
			final TIdentifierLiteral e = iterator.next();
			e.apply(this);

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("=");

		final List<PExpression> copy2 = new ArrayList<PExpression>(
				node.getElements());
		sb.append("{");
		for (final Iterator<PExpression> iterator = copy2.iterator(); iterator
				.hasNext();) {
			final PExpression e = iterator.next();
			e.apply(this);

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("}");

	}

}
