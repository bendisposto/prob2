package de.prob.model.eventb.theory;

import java.util.Objects;
import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.model.representation.AbstractFormulaElement;
import de.prob.model.representation.ModelElementList;
import de.prob.model.representation.Named;

import org.eventb.core.ast.extension.IFormulaExtension;

public class RewriteRule extends AbstractFormulaElement implements Named {
	private final String name;
	private final String applicability;
	private final boolean complete;
	private final String desc;
	private final ModelElementList<RewriteRuleRHS> rightHandSideRules;
	private final EventB formula;

	public RewriteRule(final String name, final String applicability,
			final boolean complete, final String desc, final String formula,
			final Set<IFormulaExtension> typeEnv) {
		this(name, applicability, complete, desc, new EventB(formula, typeEnv, FormulaExpand.EXPAND), new ModelElementList<RewriteRuleRHS>());
	}

	public RewriteRule(final String name, final String applicability,
			final boolean complete, final String desc, final EventB formula,
			ModelElementList<RewriteRuleRHS> rightHandSideRules) {
		this.name = name;
		this.applicability = applicability;
		this.complete = complete;
		this.desc = desc;
		this.formula = formula;
		this.rightHandSideRules = rightHandSideRules;
	}

	public RewriteRule addRightHandSide(
			final ModelElementList<RewriteRuleRHS> rightHandSides) {
		return new RewriteRule(name, applicability, complete, desc, formula, rightHandSides);
	}

	public ModelElementList<RewriteRuleRHS> getRightHandSideRules() {
		return rightHandSideRules;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getApplicability() {
		return applicability;
	}

	public boolean isComplete() {
		return complete;
	}

	public String getDesc() {
		return desc;
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
		return Objects.hash(this.getApplicability(), this.isComplete(), this.getFormula(), this.getName());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final RewriteRule other = (RewriteRule)obj;
		return Objects.equals(this.getApplicability(), other.getApplicability())
				&& Objects.equals(this.isComplete(), other.isComplete())
				&& Objects.equals(this.getFormula(), other.getFormula())
				&& Objects.equals(this.getName(), other.getName());
	}
}
