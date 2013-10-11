package de.prob.model.eventb.theory;

import java.util.List;

import de.prob.model.eventb.EventBAxiom;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Axiom;

public class AxiomaticOperatorDefinition extends AbstractElement implements
		IOperatorDefinition {

	private List<EventBAxiom> definitionAxioms;

	public void addDefinitionAxioms(final List<EventBAxiom> axioms) {
		put(Axiom.class, axioms);
		definitionAxioms = axioms;
	}

	public List<EventBAxiom> getDefinitionAxioms() {
		return definitionAxioms;
	}

}
