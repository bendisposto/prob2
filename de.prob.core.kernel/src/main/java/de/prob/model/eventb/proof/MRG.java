package de.prob.model.eventb.proof;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.translate.ProofTreeCreator;

public class MRG extends ProofObligation {
	private final Event event;

	public MRG(final String proofName, final Event event, final EventB goal,
			final boolean discharged, final String description,
			final ProofTreeCreator creator) {
		super(proofName, goal, discharged, description, creator);
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
