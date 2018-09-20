package de.prob.model.eventb.theory;

import java.util.Set;

import com.google.common.base.Objects;

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
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((expressionString == null) ? 0 : expressionString.hashCode());
		result = prime * result
				+ ((formulaString == null) ? 0 : formulaString.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RecursiveDefinitionCase other = (RecursiveDefinitionCase) obj;
		return Objects.equal(expressionString, other.expressionString)
				&& Objects.equal(formulaString, other.formulaString);
	}

}
