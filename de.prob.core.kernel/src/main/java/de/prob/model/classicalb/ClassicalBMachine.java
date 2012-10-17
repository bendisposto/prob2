package de.prob.model.classicalb;

import java.util.Arrays;

import de.prob.model.representation.IEntity;
import de.prob.model.representation.Label;

public class ClassicalBMachine extends Label {

	private boolean locked = false;
	public final Label sets = new Label("Sets");
	public final Label parameters = new Label("Parameters");
	public final Label constraints = new Label("Constraints");
	public final Label constants = new Label("Constants");
	public final Label properties = new Label("Properties");
	public final Label variables = new Label("Variables");
	public final Label invariants = new Label("Invariants");
	public final Label assertions = new Label("Assertions");
	public final Label operations = new Label("Operations");

	public ClassicalBMachine() {
		super("");
		children.addAll(Arrays.asList(new Label[] { sets, parameters,
				constraints, constants, properties, variables, invariants,
				assertions, operations }));
	}

	public String name() {
		return name;
	}

	public void setName(final String name) {
		if (locked) {
			throw new UnsupportedOperationException(
					"Must not modify Machine after it has been locked");
		}
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public void close() {
		lock();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ClassicalBMachine) {
			final ClassicalBMachine that = (ClassicalBMachine) obj;
			return that.name.equals(name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public String print() {
		final StringBuilder sb = new StringBuilder();
		if (sets.hasChildren()) {
			sb.append("Sets:\n");
			for (final IEntity set : sets.getChildren()) {
				sb.append("  " + set.toString() + "\n");
			}
		}
		if (parameters.hasChildren()) {
			sb.append("Parameters:\n");
			for (final IEntity parameter : parameters.getChildren()) {
				sb.append("  " + parameter.toString() + "\n");
			}
		}
		if (constraints.hasChildren()) {
			sb.append("Constraints:\n");
			for (final IEntity constraint : constraints.getChildren()) {
				sb.append("  " + constraint.toString() + "\n");
			}
		}
		if (constants.hasChildren()) {
			sb.append("Constants:\n");
			for (final IEntity constant : constants.getChildren()) {
				sb.append("  " + constant.toString() + "\n");
			}
		}
		if (properties.hasChildren()) {
			sb.append("Properties:\n");
			for (final IEntity property : properties.getChildren()) {
				sb.append("  " + property.toString() + "\n");
			}
		}
		if (variables.hasChildren()) {
			sb.append("Variables:\n");
			for (final IEntity variable : variables.getChildren()) {
				sb.append("  " + variable.toString() + "\n");
			}
		}
		if (invariants.hasChildren()) {
			sb.append("Invariant:\n");
			for (final IEntity inv : invariants.getChildren()) {
				sb.append("  " + inv.toString() + "\n");
			}
		}
		if (assertions.hasChildren()) {
			sb.append("Assertions:\n");
			for (final IEntity assertion : assertions.getChildren()) {
				sb.append("  " + assertion.toString() + "\n");
			}
		}
		if (operations.hasChildren()) {
			sb.append("Operations:\n");
			for (final IEntity operation : operations.getChildren()) {
				sb.append("  " + operation.toString() + "\n");
			}
		}
		return sb.toString();
	}

}
