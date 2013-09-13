package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.prolog.output.IPrologTermOutput;

public class MRG extends SimpleProofNode implements IProofObligation {
	private final Event event;
	private final String name;

	public MRG(final String proofName, final Event event, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(goal, hypotheses, discharged, description);
		name = proofName;
		this.event = event;
	}

	public String getName() {
		return name;
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
