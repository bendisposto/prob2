package de.prob.model.eventb.newdom;

import de.prob.model.representation.newdom.Action;

public class EventBAction extends Action {

	private final String name;

	public EventBAction(final String name, final String code) {
		super(code);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
