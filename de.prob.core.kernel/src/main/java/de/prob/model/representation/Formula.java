package de.prob.model.representation;

import java.util.ArrayList;
import java.util.List;

public class Formula implements IFormula {

	private final String label;
	private final String value;
	private final FormulaUUID uuid;
	private final List<IFormula> subformulas;

	public Formula(final String label, final String value) {
		this.label = label;
		this.value = value;
		this.uuid = new FormulaUUID();
		this.subformulas = new ArrayList<IFormula>();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public FormulaUUID getId() {
		return uuid;
	}

	@Override
	public List<IFormula> getAllSubformulas() {
		return subformulas;
	}

	public void addFormula(final Formula formula) {
		subformulas.add(formula);
	}

	@Override
	public List<IFormula> getVisibleSubformulas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}

}
