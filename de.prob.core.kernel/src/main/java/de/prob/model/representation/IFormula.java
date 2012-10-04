package de.prob.model.representation;

import java.util.List;

public interface IFormula {
	public String getLabel();

	public String getValue();

	public FormulaUUID getId();

	public List<IFormula> getAllSubformulas();

	public List<IFormula> getVisibleSubformulas();

	public boolean isVisible();
}
