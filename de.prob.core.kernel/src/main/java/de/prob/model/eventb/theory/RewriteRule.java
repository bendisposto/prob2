package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import com.google.common.base.Objects;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class RewriteRule extends AbstractElement {
	private final String name;
	private final String applicability;
	private final boolean complete;
	private final String desc;
	private ModelElementList<RewriteRuleRHS> rightHandSideRules = new ModelElementList<RewriteRuleRHS>();
	private final EventB formula;

	public RewriteRule(final String name, final String applicability,
			final boolean complete, final String desc, final String formula,
			final Set<IFormulaExtension> typeEnv) {
		this.name = name;
		this.applicability = applicability;
		this.complete = complete;
		this.desc = desc;
		this.formula = new EventB(formula, typeEnv);
	}

	public void addRightHandSide(
			final ModelElementList<RewriteRuleRHS> rightHandSides) {
		put(RewriteRuleRHS.class, rightHandSides);
		rightHandSideRules = rightHandSides;
	}

	public ModelElementList<RewriteRuleRHS> getRightHandSideRules() {
		return rightHandSideRules;
	}

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

	public EventB getFormula() {
		return formula;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(applicability, complete, formula, name);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RewriteRule other = (RewriteRule) obj;
		return Objects.equal(applicability, other.applicability)
				&& Objects.equal(complete, other.complete)
				&& Objects.equal(formula, other.formula)
				&& Objects.equal(name, other.name);
	}
}
