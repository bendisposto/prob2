package de.prob.model.representation;

import java.util.ArrayList;
import java.util.List;

public class Label implements IFormula {

	private final String label;
	private final List<IFormula> subformulas;
	private final FormulaUUID uuid;

	public Label(final String label) {
		this.label = label;
		this.subformulas = new ArrayList<IFormula>();
		this.uuid = new FormulaUUID();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getValue() {
		return "";
	}

	@Override
	public FormulaUUID getId() {
		return uuid;
	}

	@Override
	public List<IFormula> getAllSubformulas() {
		return subformulas;
	}

	public void addFormula(final IFormula formula) {
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
