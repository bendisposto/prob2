package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.Variant;

public class VAR extends ProofObligation {

	private final Variant variant;
	private final Event event;

	public VAR(final String name, final Event event, final Variant variant,
			final EventB goal, final Set<EventB> hypotheses,
			final boolean discharged, final String description) {
		super(name, goal, hypotheses, discharged, description);
		this.variant = variant;
		this.event = event;
	}

	public Variant getVariant() {
		return variant;
	}

	public Event getEvent() {
		return event;
	}

	@Override
	public String toString() {
		return name;
	}

}
