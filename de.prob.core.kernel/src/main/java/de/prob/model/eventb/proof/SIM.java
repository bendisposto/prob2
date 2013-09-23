package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBAction;

public class SIM extends ProofObligation {

	private final EventBAction action;
	private final Event event;

	public SIM(final String proofName, final Event event,
			final EventBAction action, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(proofName, goal, hypotheses, discharged, description);
		this.action = action;
		this.event = event;
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
