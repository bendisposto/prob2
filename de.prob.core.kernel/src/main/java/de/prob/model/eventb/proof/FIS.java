package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.EventBAction;

public class FIS extends ProofObligation {

	private final EventBAction action;

	public FIS(final String proofName, final EventBAction action,
			final EventB goal, final Set<EventB> hypotheses,
			final boolean discharged, final String description) {
		super(proofName, goal, hypotheses, discharged, description);
		this.action = action;
	}

	public EventBAction getAction() {
		return action;
	}

	@Override
	public String toString() {
		return name;
	}

}
