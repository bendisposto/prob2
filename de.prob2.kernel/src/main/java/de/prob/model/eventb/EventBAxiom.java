package de.prob.model.eventb;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.model.representation.Axiom;
import de.prob.model.representation.Named;

import org.eventb.core.ast.extension.IFormulaExtension;

public class EventBAxiom extends Axiom implements Named {

	private final String name;
	private final boolean theorem;
	private final String comment;

	public EventBAxiom(final String name, final String code,
			final boolean theorem, final Set<IFormulaExtension> typeEnv) {
		this(name, new EventB(code, typeEnv, FormulaExpand.EXPAND), theorem, "");
	}

	public EventBAxiom(final String name, final EventB predicate,
			final boolean theorem, String comment) {
		super(predicate);
		this.name = name;
		this.theorem = theorem;
		this.comment = comment == null ? "" : comment;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getComment() {
		return comment;
	}

	@Override
	public boolean isTheorem() {
		return theorem;
	}
}
