package de.prob.model.representation;

import java.util.ArrayList;
import java.util.List;

public class Label extends AbstractDomTreeElement {

	private final String label;
	private final List<AbstractDomTreeElement> subformulas;

	public Label(final String label) {
		this.label = label;
		this.subformulas = new ArrayList<AbstractDomTreeElement>();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public List<AbstractDomTreeElement> getSubcomponents() {
		return subformulas;
	}

	public void addFormula(final AbstractDomTreeElement formula) {
		subformulas.add(formula);
	}

	@Override
	public boolean toEvaluate() {
		return false;
	}

}
