package de.prob.model.classicalb.newdom;

import java.util.List;

import de.prob.model.representation.newdom.BEvent;
import de.prob.model.representation.newdom.BSet;
import de.prob.model.representation.newdom.Constant;
import de.prob.model.representation.newdom.Invariant;
import de.prob.model.representation.newdom.Machine;
import de.prob.model.representation.newdom.Variable;

public class ClassicalBMachine extends Machine {

	public ClassicalBMachine(final String name) {
		super(name);
	}

	public void addParameters(final List<Parameter> parameters) {
		put(Parameter.class, parameters);
	}

	public void addSets(final List<BSet> sets) {
		put(BSet.class, sets);
	}

	public void addConstraints(final List<Constraint> constraints) {
		put(Constraint.class, constraints);
	}

	public void addConstants(final List<ClassicalBConstant> constants) {
		put(Constant.class, constants);
	}

	public void addProperties(final List<Property> properties) {
		put(Property.class, properties);
	}

	public void addVariables(final List<ClassicalBVariable> variables) {
		put(Variable.class, variables);
	}

	public void addInvariants(final List<ClassicalBInvariant> invariants) {
		put(Invariant.class, invariants);
	}

	public void addAssertions(final List<Assertion> assertions) {
		put(Assertion.class, assertions);
	}

	public void addOperations(final List<Operation> operations) {
		put(BEvent.class, operations);
	}

}
