package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class RewriteRuleRHS extends AbstractElement {

	private final String name;
	private final EventB predicate;
	private final EventB formula;

	public RewriteRuleRHS(final String name, final String predicate,
			final String formula, final Set<IFormulaExtension> typeEnv) {
		this.name = name;
		this.predicate = new EventB(predicate, typeEnv);
		this.formula = new EventB(formula, typeEnv);
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
		return 13 * name.hashCode() + 17 * predicate.hashCode() + 23
				* formula.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof RewriteRuleRHS) {
			return name.equals(((RewriteRuleRHS) obj).getName())
					&& formula.equals(((RewriteRuleRHS) obj).getFormula())
					&& predicate.equals(((RewriteRuleRHS) obj).getPredicate());
		}
		return false;
	}
}
