package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class Theorem extends AbstractElement {

	private final String name;
	private final EventB predicate;

	public Theorem(final String name, final String predicate,
			final Set<IFormulaExtension> typeEnv) {
		this.name = name;
		this.predicate = new EventB(predicate, typeEnv);
	}

	public String getName() {
		return name;
	}

	public EventB getPredicate() {
		return predicate;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Theorem) {
			return name.equals(((Theorem) obj).getName())
					&& predicate.equals(((Theorem) obj).getPredicate());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 13 * name.hashCode() + 17 * predicate.hashCode();
	}

}
