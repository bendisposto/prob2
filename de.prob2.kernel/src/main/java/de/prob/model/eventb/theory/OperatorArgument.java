package de.prob.model.eventb.theory;

import java.util.Objects;
import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.model.representation.AbstractElement;

import org.eventb.core.ast.extension.IFormulaExtension;

public class OperatorArgument extends AbstractElement {

	private final EventB identifier;
	private final EventB type;

	public OperatorArgument(final String identifier, final String type,
			final Set<IFormulaExtension> typeEnv) {
		this.identifier = new EventB(identifier, typeEnv, FormulaExpand.EXPAND);
		this.type = new EventB(type, typeEnv, FormulaExpand.EXPAND);
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
		return Objects.hash(identifier, type);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final OperatorArgument other = (OperatorArgument)obj;
		return Objects.equals(this.getIdentifier(), other.getIdentifier())
				&& Objects.equals(this.getType(), other.getType());
	}

}
