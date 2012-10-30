package de.prob.model.classicalb.newdom;

import java.util.ArrayList;

import de.prob.model.representation.newdom.AbstractEvent;
import de.prob.model.representation.newdom.Constant;
import de.prob.model.representation.newdom.Invariant;
import de.prob.model.representation.newdom.Machine;
import de.prob.model.representation.newdom.Set;
import de.prob.model.representation.newdom.Variable;

public class ClassicalBMachine extends Machine {

	public ClassicalBMachine(final String name) {
		super(name);
		put(Parameter.class, new ArrayList<Parameter>());
		put(Set.class, new ArrayList<ClassicalBSet>());
		put(Constraint.class, new ArrayList<Constraint>());
		put(Constant.class, new ArrayList<ClassicalBConstant>());
		put(Property.class, new ArrayList<Property>());
		put(Variable.class, new ArrayList<ClassicalBVariable>());
		put(Invariant.class, new ArrayList<ClassicalBInvariant>());
		put(Assertion.class, new ArrayList<Assertion>());
		put(AbstractEvent.class, new ArrayList<Operation>());
	}

	public void addParameter(final Parameter parameter) {
	}

	public void addSet(final ClassicalBSet set) {
	}

	public void addConstraint(final Constraint constraint) {
	}

	public void addConstant(final ClassicalBConstant constant) {
	}

	public void addProperty(final Property property) {
	}

	public void addVariable(final ClassicalBVariable variable) {
	}

	public void addInvariant(final ClassicalBInvariant invariant) {
	}

	public void addAssertion(final Assertion assertion) {
	}

	public void addOperation(final Operation operation) {
	}

}
