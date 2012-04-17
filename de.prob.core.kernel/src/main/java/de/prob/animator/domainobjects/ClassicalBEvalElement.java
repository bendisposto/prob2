/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.domainobjects;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Start;

public class ClassicalBEvalElement {

	private final String code;
	private EvalElementType type;

	public ClassicalBEvalElement(final String code, final EvalElementType type) {
		this.code = code;
		this.type = type;
	}

	public ClassicalBEvalElement(final String code) {
		this.code = code;
		this.type = null;
	}

	public Start parse() throws BException {
		if (type != null)
			return tryparse(type);
		try {
			Start res = tryparse(EvalElementType.EXPRESSION);
			type = EvalElementType.EXPRESSION;
			return res;
		} catch (BException e) {
			Start res = tryparse(EvalElementType.PREDICATE);
			type = EvalElementType.PREDICATE;
			return res;
		}
	}

	public Start tryparse(final EvalElementType type) throws BException {
		final BParser parser = new BParser();
		final Start modelAst = parser.parse(type + " " + code, false);
		return modelAst;
	}

	public EvalElementType getType() {
		return type;
	}

	public String getCode() {
		return code;
	}
}
