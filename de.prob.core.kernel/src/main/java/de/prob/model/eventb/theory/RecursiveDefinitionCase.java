package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class RecursiveDefinitionCase extends AbstractElement {

	private final EventB expression;
	private final EventB formula;

	public RecursiveDefinitionCase(final String expression,
			final String formula, final Set<IFormulaExtension> typeEnv) {
		this.expression = new EventB(expression, typeEnv);
		this.formula = new EventB(formula, typeEnv);
	}

	public EventB getExpression() {
		return expression;
	}

	public EventB getFormula() {
		return formula;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof RecursiveDefinitionCase) {
			return expression.equals(((RecursiveDefinitionCase) obj)
					.getExpression())
					&& formula.equals(((RecursiveDefinitionCase) obj)
							.getFormula());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 17 * expression.hashCode() + 23 * formula.hashCode();
	}
}
