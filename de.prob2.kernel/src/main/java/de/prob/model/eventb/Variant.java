package de.prob.model.eventb;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractFormulaElement;

public class Variant extends AbstractFormulaElement {
	private final IEvalElement expression;
	private String comment;

	public Variant(final String code, final Set<IFormulaExtension> typeEnv) {
		this(new EventB(code, typeEnv), "");
	}

	public Variant(EventB expression, String comment) {
		this.comment = comment;
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
		return expression;
	}
}
