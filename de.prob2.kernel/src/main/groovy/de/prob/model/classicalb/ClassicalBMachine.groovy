package de.prob.model.classicalb;

import de.prob.model.representation.BEvent
import de.prob.model.representation.BSet
import de.prob.model.representation.Constant
import de.prob.model.representation.Invariant
import de.prob.model.representation.Machine
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.Variable

public class ClassicalBMachine extends Machine {

	def ModelElementList<Parameter> parameters = new ModelElementList<Parameter>()
	def ModelElementList<BSet> sets = new ModelElementList<BSet>()
	def ModelElementList<Constraint> constraints = new ModelElementList<Constraint>()
	def ModelElementList<ClassicalBConstant> constants = new ModelElementList<Constant>()
	def ModelElementList<Property> properties = new ModelElementList<Property>()
	def ModelElementList<ClassicalBVariable> variables = new ModelElementList<ClassicalBVariable>()
	def ModelElementList<ClassicalBInvariant> invariants = new ModelElementList<ClassicalBInvariant>()
	def ModelElementList<Operation> operations = new ModelElementList<Operation>()
	def ModelElementList<Assertion> assertions = new ModelElementList<Assertion>()

	public ClassicalBMachine(final String name) {
		super(name);
	}

	public void addParameters(final ModelElementList<Parameter> parameters) {
		put(Parameter.class, parameters)
		this.parameters = parameters
	}

	public void addSets(final ModelElementList<BSet> sets) {
		put(BSet.class, sets)
		this.sets = sets
	}

	public void addConstraints(final ModelElementList<Constraint> constraints) {
		put(Constraint.class, constraints)
		this.constraints = constraints
	}

	public void addConstants(final ModelElementList<ClassicalBConstant> constants) {
		put(Constant.class, constants)
		this.constants = constants
	}

	public void addProperties(final ModelElementList<Property> properties) {
		put(Property.class, properties)
		this.properties = properties
	}

	public void addVariables(final ModelElementList<ClassicalBVariable> variables) {
		put(Variable.class, variables)
		this.variables = variables
	}

	public void addInvariants(final ModelElementList<ClassicalBInvariant> invariants) {
		put(Invariant.class, invariants)
		this.invariants = invariants
	}

	public void addAssertions(final ModelElementList<Assertion> assertions) {
		put(Assertion.class, assertions)
		this.assertions = assertions
	}

	public void addOperations(final ModelElementList<Operation> operations) {
		put(BEvent.class, operations)
		this.operations = operations
	}

	public ModelElementList<Operation> getEvents() {
		return operations
	}

	public Operation getOperation(String name) {
		return operations[name]
	}
}
