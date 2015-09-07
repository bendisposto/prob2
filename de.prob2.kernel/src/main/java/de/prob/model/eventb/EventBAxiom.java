package de.prob.model.eventb;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.Axiom;

public class EventBAxiom extends Axiom {

	private final String name;
	private final boolean theorem;
	private String comment;

	public EventBAxiom(final String name, final String code,
			final boolean theorem, final Set<IFormulaExtension> typeEnv) {
		this(name, code, theorem, typeEnv, "");
	}

	public EventBAxiom(final String name, final String code,
			final boolean theorem, final Set<IFormulaExtension> typeEnv,
			String comment) {
		super(new EventB(code, typeEnv));
		this.name = name;
		this.theorem = theorem;
		this.comment = comment;
	}

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
