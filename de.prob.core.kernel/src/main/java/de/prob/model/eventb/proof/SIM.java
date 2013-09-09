package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBAction;

public class SIM extends SimpleProofNode {

	private final String name;
	private final EventBAction action;
	private final Event event;

	public SIM(final String proofName, final EventBAction action,
			final Event event, final EventB goal, final Set<EventB> hypotheses,
			final boolean discharged, final String description) {
		super(goal, hypotheses, discharged, description);
		name = proofName;
		this.action = action;
		this.event = event;
	}

	public String getName() {
		return name;
	}

	public EventBAction getAction() {
		return action;
	}

	public Event getEvent() {
		return event;
	}

	@Override
	public String toString() {
		return name;
	}

}
