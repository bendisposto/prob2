package de.prob.model.eventb.proof;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.EventBAction;
import de.prob.model.eventb.translate.ProofTreeCreator;

public class FIS extends ProofObligation {

	private final EventBAction action;

	public FIS(final String proofName, final EventBAction action,
			final EventB goal, final boolean discharged,
			final String description, final ProofTreeCreator creator) {
		super(proofName, goal, discharged, description, creator);
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
