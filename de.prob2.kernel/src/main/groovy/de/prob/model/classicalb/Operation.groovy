package de.prob.model.classicalb;

import com.google.common.base.Joiner

import de.prob.model.representation.Action
import de.prob.model.representation.BEvent
import de.prob.model.representation.Guard
import de.prob.model.representation.ModelElementList

public class Operation extends BEvent {

	def final List<String> parameters;
	def final List<String> output;
	def ModelElementList<Action> actions = new ModelElementList<Action>()
	def ModelElementList<Guard> guards = new ModelElementList<Guard>()

	public Operation(final String name, final List<String> parameters,
	final List<String> output) {
		super(name);
		this.parameters = parameters;
		this.output = output;
	}

	public void addGuards(final ModelElementList<ClassicalBGuard> guards) {
		put(Guard.class, guards);
		this.guards = guards
	}

	public void addActions(final ModelElementList<ClassicalBAction> actions) {
		put(Action.class, actions)
		this.actions = actions
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (!output.isEmpty()) {
			for (String string : output) {
				sb.append(string + " <-- ");
			}
		}
		sb.append(getName());
		if (!parameters.isEmpty()) {
			sb.append("(");
			sb.append(Joiner.on(",").join(parameters.iterator()));
			sb.append(")");
		}
		return sb.toString();
	}
}
