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
	private final String type;

	public ClassicalBEvalElement(final String code, final EvalElementType type) {
		this.code = code;
		this.type = type.toString();
	}

	public ClassicalBEvalElement(final String code) {
		this.code = code;
		this.type = null;
	}

	public Start parse() throws BException {
		if (type != null)
			return tryparse(type);
		try {
			return tryparse("EXPRESSION");
		} catch (BException e) {
			return tryparse("PREDICATE");
		}
	}

	public Start tryparse(final String prefix) throws BException {
		final BParser parser = new BParser();
		final Start modelAst = parser.parse("#" + prefix + " " + code, false);
		return modelAst;
	}

}
