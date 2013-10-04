package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.Variant;
import de.prob.prolog.output.IPrologTermOutput;

public class NAT extends CalculatedPO {

	private final Variant variant;
	private final Event event;

	public NAT(final String sourceName, final String name, final Event event,
			final Variant variant, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(sourceName, name, discharged, description, goal, hypotheses);

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
	protected void printElements(final IPrologTermOutput pto) {
	}

}
