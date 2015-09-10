package de.prob.model.eventb;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import com.google.common.base.Objects;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.Guard;

public class EventBGuard extends Guard {

	private final String name;
	private final boolean theorem;
	private final String comment;

	public EventBGuard(final String name, final String code,
			final boolean theorem, final Set<IFormulaExtension> typeEnv) {
		this(name, new EventB(code, typeEnv), theorem, "");
	}

	public EventBGuard(final String name, final EventB predicate,
			final boolean theorem, String comment) {
		super(predicate);
		this.name = name;
		this.theorem = theorem;
		this.comment = comment == null ? "" : comment;
	}

	public String getName() {
		return name;
	}

	public boolean isTheorem() {
		return theorem;
	}

	public String getComment() {
		return comment;
	}

	@Override
	public boolean equals(final Object that) {
		if (that == this) {
			return true;
		}
		if (that instanceof EventBGuard) {
			return this.theorem == ((EventBGuard) that).isTheorem()
					&& getPredicate().getCode().equals(
							((EventBGuard) that).getPredicate().getCode());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.theorem, getPredicate().getCode());
	}
}
