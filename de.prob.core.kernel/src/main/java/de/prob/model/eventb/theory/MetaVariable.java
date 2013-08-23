package de.prob.model.eventb.theory;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.Variable;

public class MetaVariable extends Variable {

	private final String type;

	public MetaVariable(final String identifier, final String type) {
		super(new EventB(identifier));
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
