package de.prob.model.classicalb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.FormulaUUID;
import de.prob.model.representation.IFormula;
import de.prob.model.representation.Operation;

public class ClassicalBMachine implements AbstractElement {

	private final NodeIdAssignment astMapping;
	private final FormulaUUID uuid = new FormulaUUID();

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
			final ClassicalBMachine that = (ClassicalBMachine) obj;
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
		final StringBuilder sb = new StringBuilder();
		sb.append("Sets:\n");
		for (final ClassicalBEntity set : sets) {
			sb.append("  " + set.toString() + "\n");
		}
		sb.append("Parameters:\n");
		for (final ClassicalBEntity parameter : parameters) {
			sb.append("  " + parameter.toString() + "\n");
		}
		sb.append("Constraints:\n");
		for (final ClassicalBEntity constraint : constraints) {
			sb.append("  " + constraint.toString() + "\n");
		}
		sb.append("Constants:\n");
		for (final ClassicalBEntity constant : constants) {
			sb.append("  " + constant.toString() + "\n");
		}
		sb.append("Properties:\n");
		for (final ClassicalBEntity property : properties) {
			sb.append("  " + property.toString() + "\n");
		}
		sb.append("Variables:\n");
		for (final ClassicalBEntity variable : variables) {
			sb.append("  " + variable.toString() + "\n");
		}
		sb.append("Invariant:\n");
		for (final ClassicalBEntity inv : invariant) {
			sb.append("  " + inv.toString() + "\n");
		}
		sb.append("Assertions:\n");
		for (final ClassicalBEntity assertion : assertions) {
			sb.append("  " + assertion.toString() + "\n");
		}
		sb.append("Operations:\n");
		for (final Operation operation : operations) {
			sb.append("  " + operation.toString() + "\n");
		}
		return sb.toString();
	}

	@Override
	public List<String> getVariableNames() {
		final List<String> vars = new ArrayList<String>();
		for (final ClassicalBEntity var : variables) {
			vars.add(var.getIdentifier());
		}
		return vars;
	}

	@Override
	public List<String> getConstantNames() {
		final List<String> cons = new ArrayList<String>();
		for (final ClassicalBEntity con : constants) {
			cons.add(con.getIdentifier());
		}
		return cons;
	}

	@Override
	public List<String> getOperationNames() {
		final List<String> ops = new ArrayList<String>();
		for (final Operation op : operations) {
			ops.add(op.toString());
		}
		return ops;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLabel() {
		return name;
	}

	@Override
	public String getValue() {
		return "";
	}

	@Override
	public FormulaUUID getId() {
		return uuid;
	}

	@Override
	public List<IFormula> getAllSubformulas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IFormula> getVisibleSubformulas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}
}
