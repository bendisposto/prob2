package de.prob.model.classicalb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Operation;

public class ClassicalBMachine implements AbstractElement {

	private final NodeIdAssignment astMapping;

	public ClassicalBMachine(final NodeIdAssignment nodeIdAssignment) {
		this.astMapping = nodeIdAssignment;
	}

	public Node getNode(final int i) {
		return astMapping.lookupById(i);
	}

	private String name;
	private boolean locked = false;
	private final List<ClassicalBEntity> sets = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> parameters = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> constraints = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> constants = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> properties = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> variables = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> invariant = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> assertions = new ArrayList<ClassicalBEntity>();
	private final List<Operation> operations = new ArrayList<Operation>();

	public List<ClassicalBEntity> constants() {
		return lock(constants);
	}

	public List<ClassicalBEntity> variables() {
		return lock(variables);
	}

	public List<ClassicalBEntity> parameters() {
		return lock(parameters);
	}

	public List<ClassicalBEntity> invariant() {
		return lock(invariant);
	}

	public List<ClassicalBEntity> assertions() {
		return lock(assertions);
	}

	public List<ClassicalBEntity> constraints() {
		return lock(constraints);
	}

	public List<ClassicalBEntity> properties() {
		return lock(properties);
	}

	public List<Operation> operations() {
		if (locked)
			return Collections.unmodifiableList(operations);
		return operations;
	}

	public List<ClassicalBEntity> sets() {
		return lock(sets);
	}

	public String name() {
		return name;
	}

	public void setName(final String name) {
		if (locked)
			throw new UnsupportedOperationException(
					"Must not modify Machine after it was locked");
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public void close() {
		locked = true;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ClassicalBMachine) {
			ClassicalBMachine that = (ClassicalBMachine) obj;
			return that.name.equals(name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	private List<ClassicalBEntity> lock(final List<ClassicalBEntity> p) {
		if (locked)
			return Collections.unmodifiableList(p);
		return p;
	}

	public String print() {
		StringBuilder sb = new StringBuilder();
		sb.append("Sets:\n");
		for (ClassicalBEntity set : sets) {
			sb.append("  " + set.toString() + "\n");
		}
		sb.append("Parameters:\n");
		for (ClassicalBEntity parameter : parameters) {
			sb.append("  " + parameter.toString() + "\n");
		}
		sb.append("Constraints:\n");
		for (ClassicalBEntity constraint : constraints) {
			sb.append("  " + constraint.toString() + "\n");
		}
		sb.append("Constants:\n");
		for (ClassicalBEntity constant : constants) {
			sb.append("  " + constant.toString() + "\n");
		}
		sb.append("Properties:\n");
		for (ClassicalBEntity property : properties) {
			sb.append("  " + property.toString() + "\n");
		}
		sb.append("Variables:\n");
		for (ClassicalBEntity variable : variables) {
			sb.append("  " + variable.toString() + "\n");
		}
		sb.append("Invariant:\n");
		for (ClassicalBEntity inv : invariant) {
			sb.append("  " + inv.toString() + "\n");
		}
		sb.append("Assertions:\n");
		for (ClassicalBEntity assertion : assertions) {
			sb.append("  " + assertion.toString() + "\n");
		}
		sb.append("Operations:\n");
		for (Operation operation : operations) {
			sb.append("  " + operation.toString() + "\n");
		}
		return sb.toString();
	}

	@Override
	public List<String> getVariables() {
		List<String> vars = new ArrayList<String>();
		for (ClassicalBEntity var : variables) {
			vars.add(var.getIdentifier());
		}
		return vars;
	}

	@Override
	public List<String> getConstants() {
		List<String> cons = new ArrayList<String>();
		for (ClassicalBEntity con : constants) {
			cons.add(con.getIdentifier());
		}
		return cons;
	}

	@Override
	public List<String> getOperations() {
		List<String> ops = new ArrayList<String>();
		for (Operation op : operations) {
			ops.add(op.toString());
		}
		return ops;
	}

	@Override
	public String getName() {
		return name;
	}
}
