package de.prob.model.classicalb;

import de.prob.model.representation.BEvent
import de.prob.model.representation.Guard
import de.prob.model.representation.ModelElementList

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

	public List<Guard> getGuards() {
		List<Guard> guards = new ModelElementList<Guard>();
		guards.addAll(getChildrenOfType(Guard.class));
		return guards;
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
		Set<Guard> childrenOfType = getChildrenOfType(Guard.class);
		if (!childrenOfType.isEmpty()) {
			sb.append("Guards \n");
			for (Guard guard : childrenOfType) {
				sb.append(guard.toString() + '\n');
			}
		}
		return sb.toString();
	}

	def getProperty(String prop) {
		if(prop == "guards") {
			return getGuards()
		}
		Operation.getMetaClass().getProperty(this, prop)
	}
}
