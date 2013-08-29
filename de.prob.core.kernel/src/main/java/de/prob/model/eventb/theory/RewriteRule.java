package de.prob.model.eventb.theory;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class RewriteRule extends AbstractElement {
	private final String name;
	private final String applicability;
	private final boolean complete;
	private final String desc;
	private final List<RewriteRuleRHS> rightHandSideRules = new ModelElementList<RewriteRuleRHS>();
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

	public void addRightHandSide(final List<RewriteRuleRHS> rightHandSides) {
		put(RewriteRuleRHS.class, rightHandSides);
		rightHandSideRules.addAll(rightHandSides);
	}

	public List<RewriteRuleRHS> getRightHandSideRules() {
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
		return 13 * name.hashCode() + 17 * applicability.hashCode()
				+ (complete ? 23 : 0) + 27 * formula.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof RewriteRule) {
			return name.equals(((RewriteRule) obj).getName())
					&& applicability.equals(((RewriteRule) obj)
							.getApplicability())
					&& complete == ((RewriteRule) obj).isComplete()
					&& formula.equals(((RewriteRule) obj).getFormula());
		}
		return false;
	}
}
