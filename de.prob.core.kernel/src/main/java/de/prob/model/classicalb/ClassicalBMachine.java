package de.prob.model.classicalb;

import java.util.List;

import de.prob.model.representation.BEvent;
import de.prob.model.representation.BSet;
import de.prob.model.representation.Constant;
import de.prob.model.representation.Invariant;
import de.prob.model.representation.Machine;
import de.prob.model.representation.Variable;
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
