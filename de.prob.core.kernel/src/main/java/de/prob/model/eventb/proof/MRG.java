package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.prolog.output.IPrologTermOutput;

public class MRG extends CalculatedPO {
	private final Event event;

	public MRG(final String sourceName, final String proofName,
			final Event event, final EventB goal, final Set<EventB> hypotheses,
			final boolean discharged, final String description) {
		super(sourceName, proofName, discharged, description, goal, hypotheses);
		this.event = event;
	}

	public Event getEvent() {
		return event;
	}

	@Override
	protected void printElements(final IPrologTermOutput pto) {
	}

}
