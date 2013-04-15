package de.prob.animator.domainobjects;

import java.util.List;

public class CachedFormula {
	private final String formulaId;
	private final String label;
	private final List<String> childrenIds;

	public CachedFormula(final String formulaId, final String label,
			final List<String> childrenIds) {
		this.formulaId = formulaId;
		this.label = label;
		this.childrenIds = childrenIds;
	}

	public String getFormulaId() {
		return formulaId;
	}

	public String getLabel() {
		return label;
	}

	public List<String> getChildrenIds() {
		return childrenIds;
	}

	@Override
	public String toString() {
		return "Id: " + formulaId + ", Label: " + label + ", Subformulas: "
				+ childrenIds.toString();
	}
}
