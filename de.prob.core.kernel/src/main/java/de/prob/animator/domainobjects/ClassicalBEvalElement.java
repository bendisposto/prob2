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

	public ClassicalBEvalElement(String code, EvalElementType type) {
		this.code = code;
		this.type = type.toString();
	}

	public Start parse() throws BException {
		final BParser parser = new BParser();
		final Start modelAst = parser.parse("#" + type + " " + code, false);
		return modelAst;
	}

}
