package de.prob.model.eventb.theory;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class AxiomaticOperatorDefinition extends AbstractElement implements
		IOperatorDefinition {

	private final EventB type;

	public AxiomaticOperatorDefinition(final String type) {
		this.type = new EventB(type);
	}

	public EventB getType() {
		return type;
	}
}
