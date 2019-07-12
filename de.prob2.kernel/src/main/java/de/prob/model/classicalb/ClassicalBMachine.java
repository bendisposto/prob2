package de.prob.model.classicalb;

import com.github.krukow.clj_lang.PersistentHashMap;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.BEvent;
import de.prob.model.representation.Constant;
import de.prob.model.representation.Invariant;
import de.prob.model.representation.Machine;
import de.prob.model.representation.ModelElementList;
import de.prob.model.representation.Set;
import de.prob.model.representation.Variable;

public class ClassicalBMachine extends Machine {
	public ClassicalBMachine(final String name) {
		super(name, PersistentHashMap.emptyMap());
	}

	private ClassicalBMachine(final String name, final PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children) {
		super(name, children);
	}

	public ClassicalBMachine addTo(final AbstractElement element) {
		@SuppressWarnings("unchecked")
		final ModelElementList<AbstractElement> childrenList = (ModelElementList<AbstractElement>)getChildren().get(AbstractElement.class);
		return new ClassicalBMachine(getName(), assoc(AbstractElement.class, childrenList.addElement(element)));
	}

	public ClassicalBMachine set(final Class<? extends AbstractElement> clazz, final ModelElementList<? extends AbstractElement> elements) {
		return new ClassicalBMachine(getName(), assoc(clazz, elements));
	}

	public ModelElementList<Parameter> getParameters() {
		return getChildrenOfType(Parameter.class);
	}

	public ModelElementList<Set> getSets() {
		return getChildrenOfType(Set.class);
	}

	public ModelElementList<Constraint> getConstraints() {
		return getChildrenOfType(Constraint.class);
	}

	public ModelElementList<ClassicalBConstant> getConstants() {
		return getChildrenAndCast(Constant.class, ClassicalBConstant.class);
	}

	public ModelElementList<ClassicalBVariable> getVariables() {
		return getChildrenAndCast(Variable.class, ClassicalBVariable.class);
	}

	public ModelElementList<ClassicalBInvariant> getInvariants() {
		return getChildrenAndCast(Invariant.class, ClassicalBInvariant.class);
	}

	public ModelElementList<Assertion> getAssertions() {
		return getChildrenOfType(Assertion.class);
	}

	public ModelElementList<Operation> getOperations() {
		return getChildrenAndCast(BEvent.class, Operation.class);
	}

	public ModelElementList<Operation> getEvents() {
		return getChildrenAndCast(BEvent.class, Operation.class);
	}

	public Operation getOperation(String name) {
		return getOperations().getElement(name);
	}
}
