package de.prob.model.eventb.theory;

import java.util.Objects;
import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.model.representation.AbstractFormulaElement;

import org.eventb.core.ast.extension.IFormulaExtension;

public class RecursiveDefinitionCase extends AbstractFormulaElement {

	private final String expressionString;
	private final String formulaString;
	private EventB expression;
	private EventB formula;

	public RecursiveDefinitionCase(final String expression, final String formula) {
		expressionString = expression;
		formulaString = formula;
	}

	public EventB getExpression() {
		return expression;
	}

	@Override
	public EventB getFormula() {
		return formula;
	}

	public void parseCase(final Set<IFormulaExtension> typeEnv) {
		expression = new EventB(expressionString, typeEnv, FormulaExpand.EXPAND);
		formula = new EventB(formulaString, typeEnv, FormulaExpand.EXPAND);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.expressionString, this.formulaString);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final RecursiveDefinitionCase other = (RecursiveDefinitionCase)obj;
		return Objects.equals(this.expressionString, other.expressionString)
				&& Objects.equals(this.formulaString, other.formulaString);
	}

}
