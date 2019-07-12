package de.prob.model.eventb.theory;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractFormulaElement;

import org.eventb.core.ast.extension.IFormulaExtension;

public class DirectDefinition extends AbstractFormulaElement implements
		IOperatorDefinition {

	EventB formula;

	public DirectDefinition(final String formula,
			final Set<IFormulaExtension> typeEnv) {
		this.formula = new EventB(formula, typeEnv, FormulaExpand.EXPAND);
	}

	@Override
	public IEvalElement getFormula() {
		return formula;
	}

	@Override
	public String toString() {
		return formula.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof DirectDefinition) {
			return getFormula().equals(((DirectDefinition) obj).getFormula());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return formula.hashCode();
	}
}
