package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class AxiomaticOperatorDefinition extends AbstractElement implements
		IOperatorDefinition {

	private final EventB type;

	public AxiomaticOperatorDefinition(final String type,
			final Set<IFormulaExtension> typeEnv) {
		this.type = new EventB(type, typeEnv);
	}

	public EventB getType() {
		return type;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof AxiomaticOperatorDefinition) {
			return getType().equals(
					((AxiomaticOperatorDefinition) obj).getType());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}
}
