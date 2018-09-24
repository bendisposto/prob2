package de.prob.model.eventb;

import java.util.Objects;
import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.model.representation.Guard;
import de.prob.model.representation.Named;

import org.eventb.core.ast.extension.IFormulaExtension;

public class EventBGuard extends Guard implements Named {

	private final String name;
	private final boolean theorem;
	private final String comment;

	public EventBGuard(final String name, final String code,
			final boolean theorem, final Set<IFormulaExtension> typeEnv) {
		this(name, new EventB(code, typeEnv, FormulaExpand.EXPAND), theorem, "");
	}

	public EventBGuard(final String name, final EventB predicate,
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

	public boolean isTheorem() {
		return theorem;
	}

	public String getComment() {
		return comment;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final EventBGuard other = (EventBGuard)obj;
		return this.isTheorem() == other.isTheorem()
				&& Objects.equals(this.getPredicate().getCode(), other.getPredicate().getCode());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.isTheorem(), this.getPredicate().getCode());
	}
}
