package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Witness;

public class WWD extends SimpleProofNode {

	private final String name;
	private final Witness witness;
	private final EventB paramOrVariable;

	public WWD(final String name, final Witness witness,
			final EventB paramOrVariable, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(goal, hypotheses, discharged, description);
		this.name = name;
		this.witness = witness;
		this.paramOrVariable = paramOrVariable;
	}

	public EventB getParamOrVariable() {
		return paramOrVariable;
	}

	public String getName() {
		return name;
	}

	public Witness getWitness() {
		return witness;
	}

	@Override
	public String toString() {
		return name;
	}
}
