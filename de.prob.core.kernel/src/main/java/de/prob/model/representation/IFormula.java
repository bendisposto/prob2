package de.prob.model.representation;

import java.util.List;

public interface IFormula {
	public String getLabel();

	public FormulaUUID getId();

	public List<IFormula> getSubcomponents();

	public boolean toEvaluate();
}
