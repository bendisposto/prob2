package de.prob.model.representation;

import java.util.List;

import com.google.common.base.Joiner;

public class Operation {

	private String name;
	private Predicate guard;
	private final List<String> parameters;

	public Operation(final String name, final List<String> parameters,
			final Predicate guard) {
		this.name = name;
		this.parameters = parameters;
		this.guard = guard;
	}

	public String getName() {
		return name;
	}

	public Predicate getGuard() {
		return guard;
	}

	@Override
	public String toString() {
		return name + "(" + Joiner.on(',').join(parameters) + ")";
	}
}
