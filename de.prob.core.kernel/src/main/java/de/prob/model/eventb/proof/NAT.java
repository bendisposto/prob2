package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.Variant;

public class NAT extends SimpleProofNode {

	private final String name;
	private final Variant variant;
	private final Event event;

	public NAT(final String name, final Event event, final Variant variant,
			final EventB goal, final Set<EventB> hypotheses,
			final boolean discharged, final String description) {
		super(goal, hypotheses, discharged, description);
		this.name = name;
		this.variant = variant;
		this.event = event;
	}

	public String getName() {
		return name;
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
