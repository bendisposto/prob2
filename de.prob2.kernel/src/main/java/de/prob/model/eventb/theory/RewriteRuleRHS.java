package de.prob.model.eventb.theory;

import java.util.Objects;
import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.model.representation.AbstractFormulaElement;
import de.prob.model.representation.Named;

import org.eventb.core.ast.extension.IFormulaExtension;

public class RewriteRuleRHS extends AbstractFormulaElement implements Named {

	private final String name;
	private final EventB predicate;
	private final EventB formula;

	public RewriteRuleRHS(final String name, final String predicate,
			final String formula, final Set<IFormulaExtension> typeEnv) {
		this.name = name;
		this.predicate = new EventB(predicate, typeEnv, FormulaExpand.EXPAND);
		this.formula = new EventB(formula, typeEnv, FormulaExpand.EXPAND);
	}

	@Override
	public String getName() {
		return name;
	}

	public EventB getPredicate() {
		return predicate;
	}

	@Override
	public EventB getFormula() {
		return formula;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getName(), this.getFormula(), this.getPredicate());
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final RewriteRuleRHS other = (RewriteRuleRHS)obj;
		return Objects.equals(this.getName(), other.getName())
				&& Objects.equals(this.getFormula(), other.getFormula())
				&& Objects.equals(this.getPredicate(), other.getPredicate());
	}
}
