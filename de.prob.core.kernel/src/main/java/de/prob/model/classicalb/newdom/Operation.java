package de.prob.model.classicalb.newdom;

import java.util.List;

import de.prob.model.representation.newdom.AbstractEvent;
import de.prob.model.representation.newdom.Action;
import de.prob.model.representation.newdom.Guard;

public class Operation extends AbstractEvent {

	public Operation(final String name) {
		super(name);
	}

	public void addGuards(final List<Guard> guards) {
		put(Guard.class, guards);
	}

	public void addActions(final List<Action> actions) {
		put(Action.class, actions);
	}
}
