package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Witness;

public class WFIS extends SimpleProofNode {

	private final String name;
	private final EventB paramOrVariable;
	private final Witness witness;

	public WFIS(final String name, final Witness witness,
			final EventB paramOrVariable, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(goal, hypotheses, discharged, description);
		this.name = name;
		this.paramOrVariable = paramOrVariable;
		this.witness = witness;
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
