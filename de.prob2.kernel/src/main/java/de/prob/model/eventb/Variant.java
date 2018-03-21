package de.prob.model.eventb;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractFormulaElement;

import org.eventb.core.ast.extension.IFormulaExtension;

public class Variant extends AbstractFormulaElement {
	private final IEvalElement expression;
	private final String comment;

	public Variant(final String code, final Set<IFormulaExtension> typeEnv) {
		this(new EventB(code, typeEnv), "");
	}

	public Variant(EventB expression, String comment) {
		this.comment = comment == null ? "" : comment;
		this.expression = expression;
	}

	public IEvalElement getExpression() {
		return expression;
	}

	public String getComment() {
		return comment;
	}

	@Override
	public IEvalElement getFormula() {
		return this.getExpression();
	}
}
