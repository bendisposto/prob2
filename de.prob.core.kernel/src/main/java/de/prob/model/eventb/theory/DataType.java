package de.prob.model.eventb.theory;

import java.util.List;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class DataType extends AbstractElement {

	private final EventB identifier;

	public DataType(final String identifier) {
		this.identifier = new EventB(identifier);
	}

	public void addTypeArguments(final List<Type> arguments) {
		put(Type.class, arguments);
	}

	public void addConstructors(final List<DataTypeConstructor> constructors) {
		put(DataTypeConstructor.class, constructors);
	}

	public EventB getTypeIdentifier() {
		return identifier;
	}

	public List<Type> getTypeArguments() {
		return new ModelElementList<Type>(getChildrenOfType(Type.class));
	}

	public List<DataTypeConstructor> getDataTypeConstructors() {
		return new ModelElementList<DataTypeConstructor>(
				getChildrenOfType(DataTypeConstructor.class));
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
