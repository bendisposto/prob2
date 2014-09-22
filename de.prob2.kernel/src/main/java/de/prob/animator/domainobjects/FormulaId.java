package de.prob.animator.domainobjects;

public class FormulaId {

	private final String id;
	private final IEvalElement formula;

	public FormulaId(final String id, final IEvalElement formula) {
		this.id = id;
		this.formula = formula;
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean equals(final Object that) {
		if (that instanceof FormulaId) {
			return id.equals(((FormulaId) that).id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public IEvalElement getFormula() {
		return formula;
	}

}
