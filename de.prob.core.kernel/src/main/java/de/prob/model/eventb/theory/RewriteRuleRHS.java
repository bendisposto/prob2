package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import com.google.common.base.Objects;

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
		return Objects.hashCode(name, formula, predicate);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RewriteRuleRHS that = (RewriteRuleRHS) obj;
		return Objects.equal(name, that.getName())
				&& Objects.equal(formula, that.getFormula())
				&& Objects.equal(predicate, that.getPredicate());
	}
}
