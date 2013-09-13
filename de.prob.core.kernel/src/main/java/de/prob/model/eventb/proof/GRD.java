package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBGuard;
import de.prob.prolog.output.IPrologTermOutput;

public class GRD extends SimpleProofNode implements IProofObligation {

	private final String name;
	private final EventBGuard guard;
	private final Event event;

	public GRD(final String proofName, final Event event,
			final EventBGuard guard, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
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

	@Override
	public void toProlog(final IPrologTermOutput pto) {
		// TODO Auto-generated method stub

	}
}
