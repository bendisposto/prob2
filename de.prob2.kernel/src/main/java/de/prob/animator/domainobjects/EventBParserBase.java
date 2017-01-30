package de.prob.animator.domainobjects;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.exceptions.BCompoundException;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.parserbase.ProBParseException;
import de.prob.parserbase.ProBParserBase;
import de.prob.prolog.output.IPrologTermOutput;

public class EventBParserBase implements ProBParserBase {

	private static final String EXPR_WRAPPER = "bexpr";
	private static final String PRED_WRAPPER = "bpred";
	private static final String TRANS_WRAPPER = "btrans";

	@Override
	public void parseExpression(IPrologTermOutput pto, String expression,
			boolean wrap) throws ProBParseException,
			UnsupportedOperationException {
		toPrologTerm(pto, new EventB(expression).getAst(), wrap, EXPR_WRAPPER);
	}

	@Override
	public void parsePredicate(IPrologTermOutput pto, String predicate,
			boolean wrap) throws ProBParseException,
			UnsupportedOperationException {
		toPrologTerm(pto, new EventB(predicate).getAst(), wrap, PRED_WRAPPER);
	}

	@Override
	public void parseTransitionPredicate(IPrologTermOutput pto,
			String transPredicate, boolean wrap) throws ProBParseException,
			UnsupportedOperationException {
		String formula = BParser.OPERATION_PATTERN_PREFIX + transPredicate;

		Start ast;
		try {
			ast = BParser.parse(formula);
		} catch (BCompoundException e) {
			throw new ProBParseException(e.getLocalizedMessage());
		}
		toPrologTerm(pto, ast, wrap, TRANS_WRAPPER);
	}

	private void toPrologTerm(final IPrologTermOutput pto, final Node ast,
			final boolean wrap, final String wrapper) throws ProBParseException {
		if (wrap) {
			pto.openTerm(wrapper);
		}
		final ASTProlog prolog = new ASTProlog(pto, null);
		ast.apply(prolog);
		if (wrap) {
			pto.closeTerm();
		}
	}

}
