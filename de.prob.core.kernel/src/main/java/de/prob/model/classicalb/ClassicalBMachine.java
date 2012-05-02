package de.prob.model.classicalb;

import java.util.ArrayList;
import java.util.List;

import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.Operation;

public class ClassicalBMachine extends AbstractModel {

	private final NodeIdAssignment astMapping;

	public ClassicalBMachine(final NodeIdAssignment nodeIdAssignment) {
		this.astMapping = nodeIdAssignment;
	}

	public Node getNode(final int i) {
		return astMapping.lookupById(i);
	}

	private String name;

	private final List<ClassicalBEntity> variables = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> constants = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> invariant = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> assertions = new ArrayList<ClassicalBEntity>();
	private final List<Operation> operations = new ArrayList<Operation>();

	public List<ClassicalBEntity> getConstants() {
		return constants;
	}

	public List<ClassicalBEntity> getVariables() {
		return variables;
	}

	public List<ClassicalBEntity> getInvariant() {
		return invariant;
	}

	public List<ClassicalBEntity> getAssertions() {
		return assertions;
	}

	public List<Operation> getOperations() {
		return operations;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void addVariable(final ClassicalBEntity v) {
		this.variables.add(v);
	}

	public void addConstant(final ClassicalBEntity v) {
		this.constants.add(v);
	}

	public void addAssertion(final ClassicalBEntity p) {
		this.assertions.add(p);
	}

	public void addInvariants(final List<ClassicalBEntity> l) {
		this.invariant.addAll(l);
	}

	public void addOperation(final Operation o) {
		this.operations.add(o);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nVariables:\n");
		for (ClassicalBEntity var : variables) {
			sb.append("  " + var.getIdentifier() + "\n");
		}
		sb.append("Constants:\n");
		for (ClassicalBEntity constant : constants) {
			sb.append("  " + constant.getIdentifier() + "\n");
		}
		sb.append("Invariants:\n");
		for (ClassicalBEntity inv : invariant) {
			sb.append("  " + inv.getIdentifier() + "\n");
		}
		sb.append("Assertions:\n");
		for (ClassicalBEntity assertion : assertions) {
			sb.append("  " + assertion.getIdentifier() + "\n");
		}
		sb.append("Operations:\n");
		for (Operation op : operations) {
			sb.append("  " + op.getName() + "\n");
		}
		return sb.toString();
	}

}
