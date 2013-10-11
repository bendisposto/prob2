package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class RecursiveDefinitionCase extends AbstractElement {

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

	public EventB getFormula() {
		return formula;
	}

	public void parseCase(final Set<IFormulaExtension> typeEnv) {
		expression = new EventB(expressionString, typeEnv);
		formula = new EventB(formulaString, typeEnv);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof RecursiveDefinitionCase) {
			if (expression != null && formula != null) {
				return expression.equals(((RecursiveDefinitionCase) obj)
						.getExpression())
						&& formula.equals(((RecursiveDefinitionCase) obj)
								.getFormula());
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 17 * expression.hashCode() + 23 * formula.hashCode();
	}
}
