/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.domainobjects;

import static de.prob.animator.domainobjects.EvalElementType.*;
import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.AExpressionParseUnit;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.prolog.output.IPrologTermOutput;

public class ClassicalBEvalElement implements IEvalElement {

	private final String code;
	private final Start ast;

	public ClassicalBEvalElement(final String code) throws BException {
		this.code = code;
		this.ast = BParser.parse(BParser.FORMULA_PREFIX + " " + code);
	}

	@Override
	public String getType() {
		return ast.getPParseUnit() instanceof AExpressionParseUnit ? EXPRESSION
				.toString() : PREDICATE.toString();
	}

	@Override
	public String getCode() {
		return code;
	}

	public Start getAst() {
		return ast;
	}

	@Override
	public String toString() {
		return code;
	}

	@Override
	public void printProlog(final IPrologTermOutput pout) {
		final ASTProlog prolog = new ASTProlog(pout, null);
		getAst().apply(prolog);
	}

}
