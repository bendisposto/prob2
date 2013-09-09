package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBGuard;

public class GRD extends SimpleProofNode {

	private final String name;
	private final EventBGuard guard;
	private final Event event;

	public GRD(final String proofName, final EventBGuard guard,
			final Event event, final EventB goal, final Set<EventB> hypotheses,
			final boolean discharged, final String description) {
		super(goal, hypotheses, discharged, description);
		name = proofName;
		this.guard = guard;
		this.event = event;
	}

	public String getName() {
		return name;
	}

	public EventBGuard getGuard() {
		return guard;
	}

	public Event getEvent() {
		return event;
	}

	@Override
	public String toString() {
		return name;
	}
}
