package de.prob.model.eventb.theory;

import de.prob.model.representation.AbstractElement;

public class RecursiveDefinitionCase extends AbstractElement {

	private final String expression;
	private final String formula;

	public RecursiveDefinitionCase(final String expression, final String formula) {
		this.expression = expression;
		this.formula = formula;
	}

	public String getExpression() {
		return expression;
	}

	public String getFormula() {
		return formula;
	}

}
