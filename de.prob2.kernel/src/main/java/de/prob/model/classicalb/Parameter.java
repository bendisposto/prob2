package de.prob.model.classicalb;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractFormulaElement;

public class Parameter extends AbstractFormulaElement {

	private final ClassicalB expression;

	public Parameter(final Start expression) {
		this.expression = new ClassicalB(expression);
	}

	public ClassicalB getExpression() {
		return expression;
	}

	@Override
	public IEvalElement getFormula() {
		return this.getExpression();
	}

}
