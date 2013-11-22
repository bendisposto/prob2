package de.prob.model.eventb.theory;

import de.prob.model.eventb.EventBAxiom;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Axiom;
import de.prob.model.representation.ModelElementList;

public class AxiomaticOperatorDefinition extends AbstractElement implements
		IOperatorDefinition {

	private ModelElementList<EventBAxiom> definitionAxioms;

	public void addDefinitionAxioms(final ModelElementList<EventBAxiom> axioms) {
		put(Axiom.class, axioms);
		definitionAxioms = axioms;
	}

	public ModelElementList<EventBAxiom> getDefinitionAxioms() {
		return definitionAxioms;
	}

}
