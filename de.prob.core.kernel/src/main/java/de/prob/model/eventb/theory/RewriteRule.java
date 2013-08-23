package de.prob.model.eventb.theory;

import java.util.List;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class RewriteRule extends AbstractElement {
	private final String name;
	private final String applicability;
	private final boolean complete;
	private final String desc;
	private final String formula;

	public RewriteRule(final String name, final String applicability,
			final boolean complete, final String desc, final String formula) {
		this.name = name;
		this.applicability = applicability;
		this.complete = complete;
		this.desc = desc;
		this.formula = formula;
	}

	public void addRightHandSide(final List<RewriteRuleRHS> rightHandSides) {
		put(RewriteRuleRHS.class, rightHandSides);
	}

	public List<RewriteRuleRHS> getRightHandSide() {
		return new ModelElementList<RewriteRuleRHS>(
				getChildrenOfType(RewriteRuleRHS.class));
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

	public String getFormula() {
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
