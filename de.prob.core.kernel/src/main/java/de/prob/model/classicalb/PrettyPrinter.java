package de.prob.model.classicalb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections15.map.HashedMap;

import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.node.*;

public class PrettyPrinter extends DepthFirstAdapter {

	StringBuffer sb = new StringBuffer();

	public String getPrettyPrint() {
		return sb.toString();
	}

	@Override
	public void caseAIntegerExpression(AIntegerExpression node) {
		sb.append(node.getLiteral().getText());
	}

	@Override
	public void caseAAddExpression(AAddExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append("+");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAMinusOrSetSubtractExpression(
			AMinusOrSetSubtractExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append("-");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseASetSubtractionExpression(ASetSubtractionExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("\\");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAMultOrCartExpression(AMultOrCartExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("*");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseADivExpression(ADivExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("/");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAModuloExpression(AModuloExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append(" mod ");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAPowerOfExpression(APowerOfExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("**");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseARelationsExpression(ARelationsExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("<->");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAPartialFunctionExpression(APartialFunctionExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("+->");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseATotalFunctionExpression(ATotalFunctionExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("-->");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAPartialInjectionExpression(APartialInjectionExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append(">+>");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseATotalInjectionExpression(ATotalInjectionExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append(">->");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAPartialSurjectionExpression(
			APartialSurjectionExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("+->>");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseATotalSurjectionExpression(ATotalSurjectionExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("-->>");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAPartialBijectionExpression(APartialBijectionExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append(">+>>");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseATotalBijectionExpression(ATotalBijectionExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append(">->>");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseATotalRelationExpression(ATotalRelationExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("<<->");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseASurjectionRelationExpression(
			ASurjectionRelationExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("<->>");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseATotalSurjectionRelationExpression(
			ATotalSurjectionRelationExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("<<->>");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAOverwriteExpression(AOverwriteExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("<+");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseADirectProductExpression(ADirectProductExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("><");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAConcatExpression(AConcatExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("^");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseADomainRestrictionExpression(
			ADomainRestrictionExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("<|");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseADomainSubtractionExpression(
			ADomainSubtractionExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("<<|");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseARangeRestrictionExpression(ARangeRestrictionExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("|>");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseARangeSubtractionExpression(ARangeSubtractionExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("|>>");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAInsertFrontExpression(AInsertFrontExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("->");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAInsertTailExpression(AInsertTailExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("<-");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAUnionExpression(AUnionExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("\\/");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAIntersectionExpression(AIntersectionExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("/\\");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseARestrictFrontExpression(ARestrictFrontExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("/|\\");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseARestrictTailExpression(ARestrictTailExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("\\|/");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseACoupleExpression(ACoupleExpression node) {
		final List<PExpression> copy = new ArrayList<PExpression>(
				node.getList());
		sb.append("(");
		copy.get(0).apply(this);
		sb.append(",");
		copy.get(1).apply(this);
		sb.append(")");
	}

	@Override
	public void caseAIdentifierExpression(AIdentifierExpression node) {
		final List<TIdentifierLiteral> copy = new ArrayList<TIdentifierLiteral>(
				node.getIdentifier());
		for (final Iterator<TIdentifierLiteral> iterator = copy.iterator(); iterator
				.hasNext();) {
			final TIdentifierLiteral e = iterator.next();
			e.apply(this);
		}
	}

	@Override
	public void caseAIntervalExpression(AIntervalExpression node) {
		if (node.getLeftBorder() != null)
			node.getLeftBorder().apply(this);

		sb.append("..");

		if (node.getRightBorder() != null)
			node.getRightBorder().apply(this);
	}

	@Override
	public void caseAUnaryMinusExpression(AUnaryMinusExpression node) {
		sb.append("-");

		if (node.getExpression() != null)
			node.getExpression().apply(this);
	}

	@Override
	public void caseAReverseExpression(AReverseExpression node) {
		if (node.getExpression() != null)
			node.getExpression().apply(this);
		sb.append("~");
	}

	@Override
	public void caseAImageExpression(AImageExpression node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("[");

		if (node.getRight() != null)
			node.getRight().apply(this);
		sb.append("]");

	}

	@Override
	public void caseAParallelProductExpression(AParallelProductExpression node) {
		sb.append("(");

		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("||");

		if (node.getRight() != null)
			node.getRight().apply(this);

		sb.append(")");
	}

	@Override
	public void caseACompositionExpression(ACompositionExpression node) {
		sb.append("(");
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append(";");

		if (node.getRight() != null)
			node.getRight().apply(this);
		sb.append(")");
	}

	@Override
	public void caseAConvertBoolExpression(AConvertBoolExpression node) {
		sb.append("bool(");
		if (node.getPredicate() != null)
			node.getPredicate().apply(this);
		sb.append(")");
	}

	@Override
	public void caseALessPredicate(ALessPredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("<");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAMaxExpression(AMaxExpression node) {
		sb.append("max(");
		if (node.getExpression() != null)
			node.getExpression().apply(this);
		sb.append(")");
	}

	@Override
	public void caseASetExtensionExpression(ASetExtensionExpression node) {
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
	public void caseAMinExpression(AMinExpression node) {
		sb.append("min(");
		if (node.getExpression() != null)
			node.getExpression().apply(this);
		sb.append(")");
	}

	@Override
	public void caseACardExpression(ACardExpression node) {
		sb.append("card(");
		if (node.getExpression() != null)
			node.getExpression().apply(this);
		sb.append(")");
	}

	@Override
	public void caseAGeneralSumExpression(AGeneralSumExpression node) {
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
	public void caseAGeneralProductExpression(AGeneralProductExpression node) {
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
	public void caseAConjunctPredicate(AConjunctPredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);

		sb.append("&");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAPowSubsetExpression(APowSubsetExpression node) {
		sb.append("POW(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAPow1SubsetExpression(APow1SubsetExpression node) {
		sb.append("POW1(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAFinSubsetExpression(AFinSubsetExpression node) {
		sb.append("FIN(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAFin1SubsetExpression(AFin1SubsetExpression node) {
		sb.append("FIN1(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAGeneralUnionExpression(AGeneralUnionExpression node) {
		sb.append("union(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAGeneralIntersectionExpression(
			AGeneralIntersectionExpression node) {
		sb.append("inter(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAIdentityExpression(AIdentityExpression node) {
		sb.append("id(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAReflexiveClosureExpression(AReflexiveClosureExpression node) {
		sb.append("closure(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAClosureExpression(AClosureExpression node) {
		sb.append("closure1(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseADomainExpression(ADomainExpression node) {
		sb.append("dom(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseARangeExpression(ARangeExpression node) {
		sb.append("ran(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseALambdaExpression(ALambdaExpression node) {
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
	public void caseATransFunctionExpression(ATransFunctionExpression node) {
		sb.append("fnc(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseATransRelationExpression(ATransRelationExpression node) {
		sb.append("rel(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseASeqExpression(ASeqExpression node) {
		sb.append("seq(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseASeq1Expression(ASeq1Expression node) {
		sb.append("seq1(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAIseqExpression(AIseqExpression node) {
		sb.append("iseq(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAIseq1Expression(AIseq1Expression node) {
		sb.append("iseq1(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAPermExpression(APermExpression node) {
		sb.append("perm(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAEmptySequenceExpression(AEmptySequenceExpression arg0) {
		sb.append("[]");
	}

	@Override
	public void caseASizeExpression(ASizeExpression node) {
		sb.append("size(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAFirstExpression(AFirstExpression node) {
		sb.append("first(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseALastExpression(ALastExpression node) {
		sb.append("last(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAFrontExpression(AFrontExpression node) {
		sb.append("front(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseATailExpression(ATailExpression node) {
		sb.append("tail(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseARevExpression(ARevExpression node) {
		sb.append("rev(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAFirstProjectionExpression(AFirstProjectionExpression node) {
		sb.append("prj1(");
		if (node.getExp1() != null)
			node.getExp1().apply(this);

		sb.append(",");

		if (node.getExp2() != null)
			node.getExp2().apply(this);
		sb.append(")");
	}

	@Override
	public void caseASecondProjectionExpression(ASecondProjectionExpression node) {
		sb.append("prj2(");
		if (node.getExp1() != null)
			node.getExp1().apply(this);

		sb.append(",");

		if (node.getExp2() != null)
			node.getExp2().apply(this);
		sb.append(")");
	}

	@Override
	public void caseAIterationExpression(AIterationExpression node) {
		sb.append("iterate(");
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append(",");
		if (node.getRight() != null)
			node.getRight().apply(this);
		sb.append(")");
	}

	@Override
	public void caseAComprehensionSetExpression(AComprehensionSetExpression node) {
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
	public void caseTIdentifierLiteral(TIdentifierLiteral node) {
		if (node.getText() != null) {
			sb.append(node.getText());
		}
	}

	@Override
	public void caseAQuantifiedUnionExpression(AQuantifiedUnionExpression node) {
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
			AQuantifiedIntersectionExpression node) {
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
			ASequenceExtensionExpression node) {
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
	public void caseAGeneralConcatExpression(AGeneralConcatExpression node) {
		sb.append("conc(");
		if (node.getExpression() != null) {
			node.getExpression().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseABooleanTrueExpression(ABooleanTrueExpression arg0) {
		sb.append("TRUE");
	}

	@Override
	public void caseABooleanFalseExpression(ABooleanFalseExpression arg0) {
		sb.append("FALSE");
	}

	@Override
	public void caseAMaxIntExpression(AMaxIntExpression arg0) {
		sb.append("MAXINT");
	}

	@Override
	public void caseAMinIntExpression(AMinIntExpression arg0) {
		sb.append("MININT");
	}

	@Override
	public void caseAEmptySetExpression(AEmptySetExpression node) {
		sb.append("{}");
	}

	@Override
	public void caseAIntegerSetExpression(AIntegerSetExpression arg0) {
		sb.append("INTEGER");
	}

	@Override
	public void caseANaturalSetExpression(ANaturalSetExpression arg0) {
		sb.append("NATURAL");
	}

	@Override
	public void caseANatural1SetExpression(ANatural1SetExpression arg0) {
		sb.append("NATURAL1");
	}

	@Override
	public void caseANatSetExpression(ANatSetExpression arg0) {
		sb.append("NAT");
	}

	@Override
	public void caseANat1SetExpression(ANat1SetExpression arg0) {
		sb.append("NAT1");
	}

	@Override
	public void caseAIntSetExpression(AIntSetExpression arg0) {
		sb.append("INT");
	}

	@Override
	public void caseABoolSetExpression(ABoolSetExpression arg0) {
		sb.append("BOOL");
	}

	@Override
	public void caseAStringSetExpression(AStringSetExpression arg0) {
		sb.append("STRING");
	}

	@Override
	public void caseAImplicationPredicate(AImplicationPredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append(" => ");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseADisjunctPredicate(ADisjunctPredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append(" or ");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAEquivalencePredicate(AEquivalencePredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append(" <=> ");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAEqualPredicate(AEqualPredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append("=");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAMemberPredicate(AMemberPredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append(":");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseASubsetPredicate(ASubsetPredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append("<:");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseASubsetStrictPredicate(ASubsetStrictPredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append("<<:");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseANotSubsetPredicate(ANotSubsetPredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append("/<:");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseANotSubsetStrictPredicate(ANotSubsetStrictPredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append("/<<:");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseANotEqualPredicate(ANotEqualPredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append("/=");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseANotMemberPredicate(ANotMemberPredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append("/:");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseALessEqualPredicate(ALessEqualPredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append("<=");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAGreaterEqualPredicate(AGreaterEqualPredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append(">=");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAGreaterPredicate(AGreaterPredicate node) {
		if (node.getLeft() != null)
			node.getLeft().apply(this);
		sb.append(">");

		if (node.getRight() != null)
			node.getRight().apply(this);
	}

	@Override
	public void caseAForallPredicate(AForallPredicate node) {
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
	public void caseAExistsPredicate(AExistsPredicate node) {
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
	public void caseANegationPredicate(ANegationPredicate node) {
		sb.append("not(");
		if (node.getPredicate() != null) {
			node.getPredicate().apply(this);
		}
		sb.append(")");
	}

	@Override
	public void caseAStringExpression(AStringExpression node) {
		sb.append("\"");
		if (node.getContent() != null) {
			sb.append(node.getContent().getText());
		}
		sb.append("\"");

	}

	@Override
	public void caseASuccessorExpression(ASuccessorExpression node) {
		sb.append("succ");
	}

	@Override
	public void caseAPredecessorExpression(APredecessorExpression node) {
		sb.append("pred");
	}

	@Override
	public void caseADefinitionExpression(ADefinitionExpression node) {
		String defLiteral = node.getDefLiteral().getText();
		sb.append(defLiteral + "(");
		printExprList(node.getParameters());
		sb.append(")");
	}

	private void printExprList(LinkedList<PExpression> parameters) {
		for (final Iterator<PExpression> iterator = parameters.iterator(); iterator
				.hasNext();) {
			final PExpression e = iterator.next();
			e.apply(this);
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
	}

	@Override
	public void caseADefinitionPredicate(ADefinitionPredicate node) {
		String defLiteral = node.getDefLiteral().getText();
		sb.append(defLiteral + "(");
		printExprList(node.getParameters());
		sb.append(")");
	}

	@Override
	public void caseAFunctionExpression(AFunctionExpression node) {
		node.getIdentifier().apply(this);
		sb.append("(");
		printExprList(node.getParameters());
		sb.append(")");
	}

	@Override
	public void caseARecExpression(ARecExpression node) {
		sb.append("rec(");
		List<PRecEntry> list = node.getEntries();
		for (final Iterator<PRecEntry> iterator = list.iterator(); iterator
				.hasNext();) {
			final PRecEntry e = iterator.next();
			e.apply(this);
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append(")");
	}

	@Override
	public void caseARecEntry(ARecEntry node) {
		node.getIdentifier().apply(this);
		sb.append(":");
		node.getValue().apply(this);
	}

	@Override
	public void caseARecordFieldExpression(ARecordFieldExpression node) {
		node.getRecord().apply(this);
		sb.append("'");
		node.getIdentifier().apply(this);
	}

	HashedMap<Class<? extends Node>, Integer> prio = new HashedMap<Class<? extends Node>, Integer>();

}
