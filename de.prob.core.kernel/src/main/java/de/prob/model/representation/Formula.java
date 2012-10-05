package de.prob.model.representation;

import java.util.ArrayList;
import java.util.List;

public class Formula implements IFormula {

	private final String label;
	private final FormulaUUID uuid;
	private final List<IFormula> subformulas;

	public Formula(final String label) {
		this.label = label;
		this.uuid = new FormulaUUID();
		this.subformulas = new ArrayList<IFormula>();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public FormulaUUID getId() {
		return uuid;
	}

	@Override
	public List<IFormula> getSubcomponents() {
		return subformulas;
	}

	public void addFormula(final Formula formula) {
		subformulas.add(formula);
	}

	@Override
	public boolean toEvaluate() {
		return true;
	}
}
