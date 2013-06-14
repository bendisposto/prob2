package de.prob.animator.domainobjects;

public class FormulaId {

	private final String id;

	public FormulaId(final String id) {
		this.id = id;
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

}
