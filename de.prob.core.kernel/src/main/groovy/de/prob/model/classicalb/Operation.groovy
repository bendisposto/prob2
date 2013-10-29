package de.prob.model.classicalb;

import de.prob.model.representation.BEvent
import de.prob.model.representation.Guard
import de.prob.model.representation.ModelElementList

public class Operation extends BEvent {

	private final List<String> parameters;
	private final List<String> output;
	private ModelElementList<Guard> guards = new ModelElementList<Guard>()

	public Operation(final String name, final List<String> parameters,
	final List<String> output) {
		super(name);
		this.parameters = parameters;
		this.output = output;
	}

	public void addGuards(final ModelElementList<Guard> guards) {
		put(Guard.class, guards);
		this.guards = guards
	}

	public List<String> getParameters() {
		return parameters;
	}

	public List<String> getOutput() {
		return output;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: " + getName() + "\n");
		if (!output.isEmpty()) {
			sb.append("Output: \n");
			for (String string : output) {
				sb.append(string + "\n");
			}
		}
		if (!parameters.isEmpty()) {
			sb.append("Params: \n");
			for (String string : parameters) {
				sb.append(string + "\n");
			}
		}
		if (!guards.isEmpty()) {
			sb.append("Guards \n");
			for (Guard guard : guards) {
				sb.append(guard.toString() + '\n');
			}
		}
		return sb.toString();
	}
}
