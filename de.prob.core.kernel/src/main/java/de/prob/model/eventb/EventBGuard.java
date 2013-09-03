package de.prob.model.eventb;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.Guard;

public class EventBGuard extends Guard {

	private final String name;
	private final boolean theorem;

	public EventBGuard(final String name, final String code,
			final boolean theorem, final Set<IFormulaExtension> typeEnv) {
		super(new EventB(code, typeEnv));
		this.name = name;
		this.theorem = theorem;
	}

	public String getName() {
		return name;
	}

	public boolean isTheorem() {
		return theorem;
	}
}
