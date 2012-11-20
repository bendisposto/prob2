package de.prob.model.classicalb;

import java.util.List;

import de.prob.model.representation.BEvent;
import de.prob.model.representation.Guard;

public class Operation extends BEvent {

	private final List<String> parameters;
	private final List<String> output;

	public Operation(final String name, final List<String> parameters,
			final List<String> output) {
		super(name);
		this.parameters = parameters;
		this.output = output;

	}

	public void addGuards(final List<Guard> guards) {
		put(Guard.class, guards);
	}

	public List<String> getParameters() {
		return parameters;
	}

	public List<String> getOutput() {
		return output;
	}
}
