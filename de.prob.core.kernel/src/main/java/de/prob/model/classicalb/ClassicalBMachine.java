package de.prob.model.classicalb;

import java.util.ArrayList;
import java.util.Collections;
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
	private boolean locked = false;
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
	public boolean equals(Object obj) {
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

	private List<ClassicalBEntity> lock(List<ClassicalBEntity> p) {
		if (locked)
			return Collections.unmodifiableList(p);
		return p;
	}

}
