package de.prob.model.representation;

import java.util.ArrayList;
import java.util.List;

import de.prob.model.StateSpace;

public class ClassicalBMachine extends AbstractModel {

	public ClassicalBMachine(final StateSpace statespace) {
		this.statespace = statespace;
	}

	private String name;

	private final List<NamedEntity> variables = new ArrayList<NamedEntity>();
	private final List<NamedEntity> constants = new ArrayList<NamedEntity>();
	private Predicate invariant;
	private final List<Predicate> assertions = new ArrayList<Predicate>();
	private final List<Operation> operations = new ArrayList<Operation>();

	public List<NamedEntity> getConstants() {
		return constants;
	}

	public List<NamedEntity> getVariables() {
		return variables;
	}

	public Predicate getInvariant() {
		return invariant;
	}

	public List<Predicate> getAssertions() {
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

	public void addVariable(final NamedEntity v) {
		this.variables.add(v);
	}

	public void addConstant(final NamedEntity v) {
		this.constants.add(v);
	}

	public void addAssertion(final Predicate p) {
		this.assertions.add(p);
	}

	public void addOperation(final Operation o) {
		this.operations.add(o);
	}

	public void setInvariant(final Predicate invariant) {
		this.invariant = invariant;
	}

}
