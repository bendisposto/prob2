package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class Type extends AbstractElement {

	private final EventB identifier;

	public Type(final String identifier, final Set<IFormulaExtension> typeEnv) {
		this.identifier = new EventB(identifier, typeEnv);
	}

	public EventB getIdentifier() {
		return identifier;
	}

	@Override
	public String toString() {
		return identifier.getCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Type) {
			return identifier.equals(((Type) obj).getIdentifier());
		}
		return false;
	}
}
