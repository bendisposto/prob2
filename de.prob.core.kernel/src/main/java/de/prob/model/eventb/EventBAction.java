package de.prob.model.eventb;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.Action;

public class EventBAction extends Action {

	private final String name;
	private final Event parentEvent;

	public EventBAction(final Event parentEvent, final String name,
			final String code, final Set<IFormulaExtension> typeEnv) {
		super(new EventB(code, typeEnv));
		this.parentEvent = parentEvent;
		this.name = name;
	}

	public Event getParentEvent() {
		return parentEvent;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name + ": " + getCode();
	}
}
