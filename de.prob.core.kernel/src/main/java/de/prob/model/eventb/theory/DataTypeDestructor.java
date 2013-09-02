package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class DataTypeDestructor extends AbstractElement {

	private final String identifierString;
	private final String typeString;
	private final EventB identifier;
	private EventB type;

	public DataTypeDestructor(final String identifier, final String type) {
		this.identifier = new EventB(identifier);
		identifierString = identifier;
		typeString = type;
	}

	public EventB getIdentifier() {
		return identifier;
	}

	public EventB getType() {
		return type;
	}

	public String getUnicodeIdentifier() {
		return identifierString;
	}

	public String getUnicodeType() {
		return typeString;
	}

	@Override
	public String toString() {
		return identifierString + " : " + typeString;
	}

	@Override
	public int hashCode() {
		return identifierString.hashCode() + typeString.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof DataTypeDestructor) {
			return identifierString.equals(((DataTypeDestructor) obj)
					.getUnicodeIdentifier())
					&& typeString.equals(((DataTypeDestructor) obj)
							.getUnicodeType());
		}
		return false;
	}

	public void parseElements(final Set<IFormulaExtension> typeEnv) {
		type = new EventB(typeString, typeEnv);
	}
}
