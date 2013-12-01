package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import com.google.common.base.Objects;

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
	public int hashCode() {
		return Objects.hashCode(identifier, type);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		OperatorArgument other = (OperatorArgument) obj;
		return Objects.equal(identifier, other.getIdentifier())
				&& Objects.equal(type, other.getType());
	}

}
