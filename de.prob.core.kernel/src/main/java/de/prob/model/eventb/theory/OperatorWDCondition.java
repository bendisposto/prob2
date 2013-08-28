package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class OperatorWDCondition extends AbstractElement {

	private final EventB predicate;

	public OperatorWDCondition(final String predicate,
			final Set<IFormulaExtension> typeEnv) {
		this.predicate = new EventB(predicate, typeEnv);
	}

	public EventB getPredicate() {
		return predicate;
	}

	@Override
	public String toString() {
		return predicate.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof OperatorWDCondition) {
			return predicate.equals(((OperatorWDCondition) obj).getPredicate());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return predicate.hashCode();
	}
}
