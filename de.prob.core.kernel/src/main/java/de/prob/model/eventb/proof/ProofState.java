package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;

public class ProofState {
	private final EventB goal;
	private final Set<EventB> hypotheses;
	private final boolean discharged;
	private final String description;
	private final Set<ProofState> childrenStates;

	public ProofState(final EventB goal, final Set<EventB> hypotheses,
			final boolean discharged, final String description,
			final Set<ProofState> childrenStates) {
		this.goal = goal;
		this.hypotheses = hypotheses;
		this.discharged = discharged;
		this.description = description;
		this.childrenStates = childrenStates;
	}

	public EventB getGoal() {
		return goal;
	}

	public Set<EventB> getHypotheses() {
		return hypotheses;
	}

	public boolean isDischarged() {
		return discharged;
	}

	public String getDescription() {
		return description;
	}

	public Set<ProofState> getChildrenStates() {
		return childrenStates;
	}
}
