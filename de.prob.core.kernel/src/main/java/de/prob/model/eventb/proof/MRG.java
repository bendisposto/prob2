package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;

public class MRG extends ProofObligation {
	private final Event event;

	public MRG(final String proofName, final Event event, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(proofName, goal, hypotheses, discharged, description);
		this.event = event;
	}

	public Event getEvent() {
		return event;
	}

	@Override
	public String toString() {
		return name;
	}

}
