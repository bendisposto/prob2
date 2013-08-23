package de.prob.model.eventb.theory;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class DataTypeDestructor extends AbstractElement {
	private final EventB identifier;
	private final EventB type;

	public DataTypeDestructor(final String identifier, final String type) {
		this.identifier = new EventB(identifier);
		this.type = new EventB(type);
	}

	public EventB getIdentifier() {
		return identifier;
	}

	public EventB getType() {
		return type;
	}

	@Override
	public String toString() {
		return identifier.getCode() + " : " + type.getCode();
	}

	@Override
	public int hashCode() {
		return identifier.hashCode() + type.hashCode();
	}
}
