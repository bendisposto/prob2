package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class DataTypeDestructor extends AbstractElement {

	private final EventB identifier;
	private final EventB type;

	public DataTypeDestructor(final String identifier, final String type,
			final Set<IFormulaExtension> typeEnv) {
		this.identifier = new EventB(type, typeEnv);
		this.type = new EventB(type, typeEnv);

	}

	public EventB getIdentifier() {
		return identifier;
	}

	public EventB getType() {
		return type;
	}

	@Override
	public String toString() {
		return identifier + " : " + type.getCode();
	}

	@Override
	public int hashCode() {
		return identifier.hashCode() + type.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof DataTypeDestructor) {
			return identifier
					.equals(((DataTypeDestructor) obj).getIdentifier())
					&& type.equals(((DataTypeDestructor) obj).getType());
		}
		return false;
	}
}
