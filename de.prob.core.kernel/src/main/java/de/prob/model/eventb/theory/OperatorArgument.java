package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class OperatorArgument extends AbstractElement {

	private final EventB identifier;
	private final EventB type;

	public OperatorArgument(final String identifier, final String type,
			final Set<IFormulaExtension> typeEnv) {
		this.identifier = new EventB(identifier, typeEnv);
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
		return identifier.getCode() + " : " + type.getCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof OperatorArgument) {
			return identifier.equals(((OperatorArgument) obj).getIdentifier())
					&& type.equals(((OperatorArgument) obj).getType());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 13 * identifier.hashCode() + 17 * type.hashCode();
	}
}
