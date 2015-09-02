package de.prob.model.classicalb;

import com.github.krukow.clj_lang.PersistentHashMap

import de.prob.model.representation.AbstractElement
import de.prob.model.representation.BEvent
import de.prob.model.representation.Constant
import de.prob.model.representation.Invariant
import de.prob.model.representation.Machine
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.Set
import de.prob.model.representation.Variable

public class ClassicalBMachine extends Machine {

	public ClassicalBMachine(final String name) {
		super(name, PersistentHashMap.emptyMap())
	}

	public ClassicalBMachine(final String name, children) {
		super(name, children)
	}

	def <T extends AbstractElement> ClassicalBMachine addTo(T element) {
		def kids = children.get(T)
		new ClassicalBMachine(name, children.assoc(T, kids.addElement(element)))
	}

	def ClassicalBMachine set(Class<? extends AbstractElement> clazz, ModelElementList<? extends AbstractElement> elements) {
		new ClassicalBMachine(name, children.assoc(clazz, elements))
	}

	def ModelElementList<Parameter> getParameters() {
		getChildrenOfType(Parameter)
	}

	def ModelElementList<Set> getSets() {
		getChildrenOfType(Set.class)
	}

	def ModelElementList<Constraint> getConstraints() {
		getChildrenOfType(Constraint.class)
	}

	def ModelElementList<ClassicalBConstant> getConstants() {
		getChildrenOfType(Constant.class)
	}

	def ModelElementList<Property> getProperties() {
		getChildrenOfType(Property.class)
	}

	def ModelElementList<ClassicalBVariable> getVariables() {
		getChildrenOfType(Variable.class)
	}

	def ModelElementList<ClassicalBInvariant> getInvariants() {
		getChildrenOfType(Invariant.class)
	}

	def ModelElementList<Assertion> getAssertions() {
		getChildrenOfType(Assertion.class)
	}

	def ModelElementList<Operation> getOperations() {
		getChildrenOfType(BEvent.class)
	}

	def ModelElementList<Operation> getEvents() {
		getChildrenOfType(BEvent.class)
	}

	public Operation getOperation(String name) {
		getOperations().getElement(name)
	}
}
