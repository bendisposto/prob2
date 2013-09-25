package de.prob.model.eventb.proof;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class SimpleProofNode extends AbstractElement {
	protected final EventB goal;
	protected Set<EventB> hypotheses;
	protected final boolean discharged;
	protected final String description;
	protected final Set<SimpleProofNode> childrenStates = new HashSet<SimpleProofNode>();

	public SimpleProofNode(final EventB goal, final Set<EventB> hypotheses,
			final boolean discharged, final String description) {
		this.goal = goal;
		this.hypotheses = hypotheses;
		this.discharged = discharged;
		this.description = description;
	}

	public void addChildrenNodes(final Collection<SimpleProofNode> children) {
		put(SimpleProofNode.class, children);
		childrenStates.addAll(children);
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

	public Set<SimpleProofNode> getChildrenNodes() {
		return childrenStates;
	}
}
