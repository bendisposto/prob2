package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.EventBAction;
import de.prob.prolog.output.IPrologTermOutput;

public class FIS extends CalculatedPO {

	private final EventBAction action;

	public FIS(final String sourceName, final String proofName,
			final EventBAction action, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(sourceName, proofName, discharged, description, goal, hypotheses);
		this.action = action;
	}

	public EventBAction getAction() {
		return action;
	}

	@Override
	protected void printElements(final IPrologTermOutput pto) {
	}

}
