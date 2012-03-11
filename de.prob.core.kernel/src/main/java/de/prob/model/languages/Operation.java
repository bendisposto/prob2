package de.prob.model.languages;

public class Operation {

	private String name;
	private Predicate guard;

	public Operation(final String name, final Predicate guard) {
		this.name = name;
		this.guard = guard;
	}

	public String getName() {
		return name;
	}

	public Predicate getGuard() {
		return guard;
	}
}
