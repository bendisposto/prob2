package de.prob.model.eventb.theory;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class RewriteRuleRHS extends AbstractElement {

	private final String name;
	private final EventB predicate;
	private final EventB formula;

	public RewriteRuleRHS(final String name, final String predicate,
			final String formula) {
		this.name = name;
		this.predicate = new EventB(predicate);
		this.formula = new EventB(formula);
	}

	public String getName() {
		return name;
	}

	public EventB getPredicate() {
		return predicate;
	}

	public EventB getFormula() {
		return formula;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
