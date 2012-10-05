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
	public FormulaUUID getId() {
		return uuid;
	}

	@Override
	public List<IFormula> getSubcomponents() {
		return subformulas;
	}

	public void addFormula(final IFormula formula) {
		subformulas.add(formula);
	}

	@Override
	public boolean toEvaluate() {
		return false;
	}

}
