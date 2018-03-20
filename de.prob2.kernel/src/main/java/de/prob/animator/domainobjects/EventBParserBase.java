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
	public void parseExpression(IPrologTermOutput pto, String expression, boolean wrap) throws ProBParseException {
		toPrologTerm(pto, new EventB(expression).getAst(), wrap, EXPR_WRAPPER);
	}

	@Override
	public void parsePredicate(IPrologTermOutput pto, String predicate, boolean wrap) throws ProBParseException {
		toPrologTerm(pto, new EventB(predicate).getAst(), wrap, PRED_WRAPPER);
	}

	@Override
	public void parseTransitionPredicate(IPrologTermOutput pto, String transPredicate, boolean wrap) throws ProBParseException {
		Start ast;
		try {
			ast = BParser.parse(BParser.OPERATION_PATTERN_PREFIX + transPredicate);
		} catch (BCompoundException e) {
			throw (ProBParseException)new ProBParseException(e.getLocalizedMessage()).initCause(e);
		}
		toPrologTerm(pto, ast, wrap, TRANS_WRAPPER);
	}

	private static void toPrologTerm(final IPrologTermOutput pto, final Node ast, final boolean wrap, final String wrapper) {
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
