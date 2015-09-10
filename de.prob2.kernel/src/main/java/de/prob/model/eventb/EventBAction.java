package de.prob.model.eventb;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.Action;

public class EventBAction extends Action {

	private final String name;
	private final String comment;

	public EventBAction(final String name, final String code,
			final Set<IFormulaExtension> typeEnv) {
		this(name, new EventB(code, typeEnv), "");
	}

	public EventBAction(final String name, EventB code, String comment) {
		super(code);
		this.name = name;
		this.comment = comment == null ? "" : comment;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name + ": " + getCode();
	}

	public String getComment() {
		return comment;
	}

	@Override
	public boolean equals(final Object that) {
		if (this == that) {
			return true;
		}
		if (that instanceof EventBAction) {
			return getCode().getCode().equals(
					((EventBAction) that).getCode().getCode());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getCode().hashCode();
	}
}
