package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBVariable;

public class EQL extends ProofObligation {

	private final EventBVariable variable;
	private final Event event;

	public EQL(final String proofName, final Event event,
			final EventBVariable variable, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(proofName, goal, hypotheses, discharged, description);
		this.variable = variable;
		this.event = event;
	}

	public EventBVariable getVariable() {
		return variable;
	}

	public Event getEvent() {
		return event;
	}

	@Override
	public String toString() {
		return name;
	}

}
