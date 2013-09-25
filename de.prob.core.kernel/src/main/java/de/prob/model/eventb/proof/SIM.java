package de.prob.model.eventb.proof;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBAction;
import de.prob.model.eventb.translate.ProofTreeCreator;

public class SIM extends ProofObligation {

	private final EventBAction action;
	private final Event event;

	public SIM(final String proofName, final Event event,
			final EventBAction action, final EventB goal,
			final boolean discharged, final String description,
			final ProofTreeCreator creator) {
		super(proofName, goal, discharged, description, creator);
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
