package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBInvariant;

public class INV extends SimpleProofNode {

	private final Event event;
	private final EventBInvariant invariant;
	private final String proofName;

	public INV(final String proofName, final Event event,
			final EventBInvariant invariant, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(goal, hypotheses, discharged, description);
		this.proofName = proofName;
		this.event = event;
		this.invariant = invariant;
	}

	public EventBInvariant getInvariant() {
		return invariant;
	}

	public Event getEvent() {
		return event;
	}

	public String getName() {
		return proofName;
	}
}
