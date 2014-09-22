package de.prob.model.eventb.theory;

import de.prob.model.eventb.EventBAxiom;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Axiom;
import de.prob.model.representation.ModelElementList;

public class AxiomaticDefinitionBlock extends AbstractElement {

	private final String name;
	private ModelElementList<Type> typeParameters;
	private ModelElementList<Operator> operators;
	private ModelElementList<EventBAxiom> axioms;

	public AxiomaticDefinitionBlock(final String name) {
		this.name = name;
	}

	public void addTypeParameters(final ModelElementList<Type> typeParameters) {
		put(Type.class, typeParameters);
		this.typeParameters = typeParameters;
	}

	public void addOperators(final ModelElementList<Operator> operators) {
		put(Operator.class, operators);
		this.operators = operators;
	}

	public void addDefinitionAxioms(final ModelElementList<EventBAxiom> axioms) {
		put(Axiom.class, axioms);
		this.axioms = axioms;
	}

	public String getName() {
		return name;
	}

	public ModelElementList<Type> getTypeParameters() {
		return typeParameters;
	}

	public ModelElementList<Operator> getOperators() {
		return operators;
	}

	public ModelElementList<EventBAxiom> getAxioms() {
		return axioms;
	}

}
