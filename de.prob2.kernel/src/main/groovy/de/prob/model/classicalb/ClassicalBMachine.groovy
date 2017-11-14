package de.prob.model.classicalb

import com.github.krukow.clj_lang.PersistentHashMap

import de.prob.model.representation.AbstractElement
import de.prob.model.representation.BEvent
import de.prob.model.representation.Constant
import de.prob.model.representation.Invariant
import de.prob.model.representation.Machine
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.Set
import de.prob.model.representation.Variable

class ClassicalBMachine extends Machine {

	ClassicalBMachine(final String name) {
		super(name, PersistentHashMap.emptyMap())
	}

	ClassicalBMachine(final String name, children) {
		super(name, children)
	}

	def <T extends AbstractElement> ClassicalBMachine addTo(T element) {
		def kids = children.get(T)
		new ClassicalBMachine(name, children.assoc(T, kids.addElement(element)))
	}

	ClassicalBMachine set(Class<? extends AbstractElement> clazz, ModelElementList<? extends AbstractElement> elements) {
		new ClassicalBMachine(name, children.assoc(clazz, elements))
	}

	ModelElementList<Parameter> getParameters() {
		getChildrenOfType(Parameter)
	}

	ModelElementList<Set> getSets() {
		getChildrenOfType(Set.class)
	}

	ModelElementList<Constraint> getConstraints() {
		getChildrenOfType(Constraint.class)
	}

	ModelElementList<ClassicalBConstant> getConstants() {
		getChildrenOfType(Constant.class)
	}

	ModelElementList<Property> getProperties() {
		getChildrenOfType(Property.class)
	}

	ModelElementList<ClassicalBVariable> getVariables() {
		getChildrenOfType(Variable.class)
	}

	ModelElementList<ClassicalBInvariant> getInvariants() {
		getChildrenOfType(Invariant.class)
	}

	ModelElementList<Assertion> getAssertions() {
		getChildrenOfType(Assertion.class)
	}

	ModelElementList<Operation> getOperations() {
		getChildrenOfType(BEvent.class)
	}

	ModelElementList<Operation> getEvents() {
		getChildrenOfType(BEvent.class)
	}

	Operation getOperation(String name) {
		getOperations().getElement(name)
	}
}
