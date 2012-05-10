/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.domainobjects;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.AExpressionParseUnit;
import de.be4.classicalb.core.parser.node.Start;
import static de.prob.animator.domainobjects.EvalElementType.*;

public class ClassicalBEvalElement {

	private final String code;
	private final Start ast;

	public ClassicalBEvalElement(final String code) throws BException {
		this.code = code;
		this.ast = BParser.parse(BParser.FORMULA_PREFIX + " " + code);
	}

	public EvalElementType getType() {
		return ast.getPParseUnit() instanceof AExpressionParseUnit ? EXPRESSION
				: PREDICATE;
	}

	public String getCode() {
		return code;
	}

	public Start getAst() {
		return ast;
	}

}
