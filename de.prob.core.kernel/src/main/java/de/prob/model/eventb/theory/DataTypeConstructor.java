package de.prob.model.eventb.theory;

import java.util.List;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class DataTypeConstructor extends AbstractElement {

	private final EventB identifier;

	DataTypeConstructor(final String identifier) {
		this.identifier = new EventB(identifier);
	}

	public void addDestructors(final List<DataTypeDestructor> destructors) {
		put(DataTypeDestructor.class, destructors);
	}

	public EventB getIdentifier() {
		return identifier;
	}

	public List<DataTypeDestructor> getDestructors() {
		return new ModelElementList<DataTypeDestructor>(
				getChildrenOfType(DataTypeDestructor.class));
	}

	@Override
	public String toString() {
		return identifier.getCode();
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}
}
