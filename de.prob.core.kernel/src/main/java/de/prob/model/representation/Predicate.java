package de.prob.model.representation;

public class Predicate {

	private final String predicate;

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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Predicate) {
			Predicate predicate = (Predicate) obj;
			return this.predicate.equals(predicate.predicate);
		}
		return false;
	}

}
