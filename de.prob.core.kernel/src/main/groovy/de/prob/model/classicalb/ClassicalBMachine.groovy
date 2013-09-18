package de.prob.model.classicalb;

import de.prob.model.representation.BEvent
import de.prob.model.representation.BSet
import de.prob.model.representation.Constant
import de.prob.model.representation.Invariant
import de.prob.model.representation.Machine
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.Variable

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

	public List<Parameter> getParameters() {
		List<Parameter> list = new ModelElementList<Parameter>();
		list.addAll(getChildrenOfType(Parameter.class));
		return list;
	}

	public List<BSet> getSets() {
		List<BSet> list = new ModelElementList<BSet>();
		list.addAll(getChildrenOfType(BSet.class));
		return list;
	}

	public List<Constraint> getConstraints() {
		List<Constraint> list = new ModelElementList<Constraint>();
		list.addAll(getChildrenOfType(Constraint.class));
		return list;
	}

	public List<ClassicalBConstant> getConstants() {
		List<ClassicalBConstant> list = new ModelElementList<ClassicalBConstant>();
		Set<Constant> kids = getChildrenOfType(Constant.class);
		for (Constant e : kids) {
			if (e instanceof ClassicalBConstant) {
				list.add((ClassicalBConstant) e);
			}
		}
		return list;
	}

	public List<Property> getProperties() {
		List<Property> list = new ModelElementList<Property>();
		list.addAll(getChildrenOfType(Property.class));
		return list;
	}

	public List<ClassicalBVariable> getVariables() {
		List<ClassicalBVariable> list = new ModelElementList<ClassicalBVariable>();
		Set<Variable> kids = getChildrenOfType(Variable.class);
		for (Variable e : kids) {
			if (e instanceof ClassicalBVariable) {
				list.add((ClassicalBVariable) e);
			}
		}
		return list;
	}

	public List<ClassicalBInvariant> getInvariants() {
		List<ClassicalBInvariant> list = new ModelElementList<ClassicalBInvariant>();
		Set<Invariant> kids = getChildrenOfType(Invariant.class);
		for (Invariant e : kids) {
			if (e instanceof ClassicalBInvariant) {
				list.add((ClassicalBInvariant) e);
			}
		}
		return list;
	}

	public List<Assertion> getAssertions() {
		List<Assertion> list = new ModelElementList<Assertion>();
		list.addAll(getChildrenOfType(Assertion.class));
		return list;
	}

	public List<Operation> getOperations() {
		List<Operation> list = new ModelElementList<Operation>();
		Set<BEvent> kids = getChildrenOfType(BEvent.class);
		for (BEvent e : kids) {
			if (e instanceof Operation) {
				list.add((Operation) e);
			}
		}
		return list;
	}

	public List<Operation> getEvents() {
		return getOperations()
	}

	def getProperty(String prop) {
		if(prop == "sets") {
			return getSets()
		} else if(prop == "constraints") {
			return getConstraints()
		} else if(prop == "constants") {
			return getConstants()
		} else if(prop == "properties") {
			return getProperties()
		} else if(prop == "variables") {
			return getVariables()
		} else if(prop == "invariants") {
			return getInvariants()
		} else if(prop == "assertions") {
			return getAssertions()
		} else if(prop == "operations") {
			return getOperations()
		}
		ClassicalBMachine.getMetaClass().getProperty(this, prop)
	}
}
