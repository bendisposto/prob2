package de.prob.model.eventb;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.Invariant;

public class EventBInvariant extends Invariant {

	private final String name;
	private final boolean theorem;
	private String comment;

	public EventBInvariant(final String name, final String code,
			final Boolean theorem, final Set<IFormulaExtension> typeEnv) {
		this(name, new EventB(code, typeEnv), theorem, "");
	}

	public EventBInvariant(final String name, final EventB predicate,
			final Boolean theorem, String comment) {
		super(predicate);
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
