package de.prob.model.eventb.proof;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBVariable;
import de.prob.model.eventb.translate.ProofTreeCreator;

public class EQL extends ProofObligation {

	private final EventBVariable variable;
	private final Event event;

	public EQL(final String proofName, final Event event,
			final EventBVariable variable, final EventB goal,
			final boolean discharged, final String description,
			final ProofTreeCreator creator) {
		super(proofName, goal, discharged, description, creator);
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
