package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.ModelElementList;

public abstract class CalculatedPO extends ProofObligation {

	private final EventB goal;
	private final Set<EventB> hypotheses;
	private ModelElementList<SimpleProofNode> childrenNodes;

	public CalculatedPO(final String sourceName, final String name,
			final boolean discharged, final String description,
			final EventB goal, final Set<EventB> hypotheses) {
		super(sourceName, name, discharged, description);
		this.goal = goal;
		this.hypotheses = hypotheses;
	}

	public void addChildrenNodes(
			final ModelElementList<SimpleProofNode> children) {
		put(SimpleProofNode.class, children);
		childrenNodes = children;
	}

	public EventB getGoal() {
		return goal;
	}

	public Set<EventB> getHypotheses() {
		return hypotheses;
	}

	public ModelElementList<SimpleProofNode> getChildrenNodes() {
		return childrenNodes;
	}

}
