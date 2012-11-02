package de.prob.model.classicalb.newdom;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.newdom.AbstractElement;

public class Parameter extends AbstractElement {

	private final ClassicalB expression;

	public Parameter(final Start expression) {
		this.expression = new ClassicalB(expression);
	}

	public ClassicalB getExpression() {
		return expression;
	}

}
