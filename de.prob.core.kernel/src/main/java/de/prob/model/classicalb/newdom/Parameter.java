package de.prob.model.classicalb.newdom;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.newdom.AbstractElement;

public class Parameter extends AbstractElement {

	private final ClassicalB expression;

	public Parameter(final String code) throws BException {
		expression = new ClassicalB(code);
	}

	public ClassicalB getExpression() {
		return expression;
	}

}
