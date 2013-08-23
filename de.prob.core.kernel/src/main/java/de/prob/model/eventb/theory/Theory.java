package de.prob.model.eventb.theory;

import java.util.List;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class Theory extends AbstractElement {

	private final String name;

	public Theory(final String name) {
		this.name = name;
	}

	public void addAxiomaticDefinitions(
			final List<AxiomaticDefinitionsBlock> blocks) {
		put(AxiomaticDefinitionsBlock.class, blocks);
	}

	public void addOperators(final List<Operator> operators) {
		put(Operator.class, operators);
	}

	public void addDataTypes(final List<DataType> dataTypes) {
		put(DataType.class, dataTypes);
	}

	public void addTheorems(final List<Theorem> theorems) {
		put(Theorem.class, theorems);
	}

	public void addProofRules(final List<ProofRulesBlock> proofRules) {
		put(ProofRulesBlock.class, proofRules);
	}

	public void addTypeParameters(final List<Type> parameters) {
		put(Type.class, parameters);
	}

	public void addImported(final List<Theory> theories) {
		put(Theory.class, theories);
	}

	public List<Theorem> getTheorems() {
		return new ModelElementList<Theorem>(getChildrenOfType(Theorem.class));
	}

	public List<Type> getTypeParameters() {
		return new ModelElementList<Type>(getChildrenOfType(Type.class));
	}

	public List<DataType> getDataTypes() {
		return new ModelElementList<DataType>(getChildrenOfType(DataType.class));
	}

	public List<Operator> getOperators() {
		return new ModelElementList<Operator>(getChildrenOfType(Operator.class));
	}

	public List<ProofRulesBlock> getProofRules() {
		return new ModelElementList<ProofRulesBlock>(
				getChildrenOfType(ProofRulesBlock.class));
	}

	public List<AxiomaticDefinitionsBlock> getAxiomaticDefinitions() {
		return new ModelElementList<AxiomaticDefinitionsBlock>(
				getChildrenOfType(AxiomaticDefinitionsBlock.class));
	}

	public List<Theory> getImported() {
		return new ModelElementList<Theory>(getChildrenOfType(Theory.class));
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
