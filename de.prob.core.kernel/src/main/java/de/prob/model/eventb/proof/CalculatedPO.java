package de.prob.model.eventb.proof;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.prob.animator.domainobjects.EventB;

public abstract class CalculatedPO extends ProofObligation {

	private final EventB goal;
	private final Set<EventB> hypotheses;
	private Set<SimpleProofNode> childrenNodes;

	public CalculatedPO(final String sourceName, final String name,
			final boolean discharged, final String description,
			final EventB goal, final Set<EventB> hypotheses) {
		super(sourceName, name, discharged, description);
		this.goal = goal;
		this.hypotheses = hypotheses;
	}

	public void addChildrenNodes(final Collection<SimpleProofNode> children) {
		put(SimpleProofNode.class, children);
		childrenNodes = new HashSet<SimpleProofNode>(children);
	}

	public EventB getGoal() {
		return goal;
	}

	public Set<EventB> getHypotheses() {
		return hypotheses;
	}

	public Set<SimpleProofNode> getChildrenNodes() {
		return childrenNodes;
	}

}
