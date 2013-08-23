package de.prob.model.eventb.theory;

import java.util.List;

import de.prob.model.eventb.EventBAxiom;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Axiom;
import de.prob.model.representation.ModelElementList;

public class AxiomaticDefinitionsBlock extends AbstractElement {

	private final String name;

	public AxiomaticDefinitionsBlock(final String name) {
		this.name = name;
	}

	public void addDefinitionAxioms(final List<EventBAxiom> axioms) {
		put(Axiom.class, axioms);
	}

	public void addOperatorDefinitions(final List<Operator> definitions) {
		put(Operator.class, definitions);
	}

	public void addTypeDefinitions(final List<Type> defs) {
		put(Type.class, defs);
	}

	public List<EventBAxiom> getDefinitionAxioms() {
		return new ModelElementList<EventBAxiom>(getChildrenOfType(Axiom.class));
	}

	public List<Operator> getOperators() {
		return new ModelElementList<Operator>(getChildrenOfType(Operator.class));
	}

	public ModelElementList<Type> getTypeDefinitions() {
		return new ModelElementList<Type>(
				getChildrenOfType(Type.class));
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
