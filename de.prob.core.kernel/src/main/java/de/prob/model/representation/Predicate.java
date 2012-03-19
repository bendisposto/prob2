package de.prob.model.representation;

public class Predicate {

	private String predicate;

	public Predicate(final String predicate) {
		this.predicate = predicate;
	}

	public String getPredicate() {
		return predicate;
	}

	@Override
	public String toString() {
		return predicate;
	}

}
