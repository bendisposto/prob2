package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class Theorem extends AbstractElement {

	private final String name;
	private final EventB predicate;

	public Theorem(final String name, final String predicate,
			final Set<IFormulaExtension> extensions) {
		this.name = name;
		this.predicate = new EventB(predicate, extensions);
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

}
