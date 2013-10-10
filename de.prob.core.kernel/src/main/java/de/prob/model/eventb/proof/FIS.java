package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.EventBAction;
import de.prob.prolog.output.IPrologTermOutput;

public class FIS extends SimpleProofNode implements IProofObligation {

	private final String name;
	private final EventBAction action;

	public FIS(final String proofName, final EventBAction action,
			final EventB goal, final Set<EventB> hypotheses,
			final boolean discharged, final String description) {
		super(goal, hypotheses, discharged, description);
		name = proofName;
		this.action = action;
	}

	public String getName() {
		return name;
	}

	public EventBAction getAction() {
		return action;
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
