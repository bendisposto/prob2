package de.prob.model.eventb;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractFormulaElement;

public class Variant extends AbstractFormulaElement {
	private final IEvalElement expression;

	public Variant(final String code, final Set<IFormulaExtension> typeEnv) {
		expression = new EventB(code, typeEnv);
	}

	public IEvalElement getExpression() {
		return expression;
	}

	@Override
	public IEvalElement getFormula() {
		return expression;
	}
}
