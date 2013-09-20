package de.prob.model.eventb.theory;

import java.util.List;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class Theory extends AbstractElement {

	private final String name;
	private final List<AxiomaticDefinitionsBlock> axiomaticDefinitions = new ModelElementList<AxiomaticDefinitionsBlock>();
	private final List<DataType> dataTypes = new ModelElementList<DataType>();
	private final List<Theory> imported = new ModelElementList<Theory>();
	private final List<Operator> operators = new ModelElementList<Operator>();
	private final List<ProofRulesBlock> proofRules = new ModelElementList<ProofRulesBlock>();
	private final List<Theorem> theorems = new ModelElementList<Theorem>();
	private final List<Type> typeParameters = new ModelElementList<Type>();

	public Theory(final String name) {
		this.name = name;
	}

	public void addAxiomaticDefinitions(
			final List<AxiomaticDefinitionsBlock> blocks) {
		put(AxiomaticDefinitionsBlock.class, blocks);
		axiomaticDefinitions.addAll(blocks);
	}

	public void addOperators(final List<Operator> operators) {
		put(Operator.class, operators);
		this.operators.addAll(operators);
	}

	public void addDataTypes(final List<DataType> dataTypes) {
		put(DataType.class, dataTypes);
		this.dataTypes.addAll(dataTypes);
	}

	public void addTheorems(final List<Theorem> theorems) {
		put(Theorem.class, theorems);
		this.theorems.addAll(theorems);
	}

	public void addProofRules(final List<ProofRulesBlock> proofRules) {
		put(ProofRulesBlock.class, proofRules);
		this.proofRules.addAll(proofRules);
	}

	public void addTypeParameters(final List<Type> parameters) {
		put(Type.class, parameters);
		typeParameters.addAll(parameters);
	}

	public void addImported(final List<Theory> theories) {
		put(Theory.class, theories);
		imported.addAll(theories);
	}

	public List<Theorem> getTheorems() {
		return theorems;
	}

	public List<Type> getTypeParameters() {
		return typeParameters;
	}

	public List<DataType> getDataTypes() {
		return dataTypes;
	}

	public List<Operator> getOperators() {
		return operators;
	}

	public List<ProofRulesBlock> getProofRules() {
		return proofRules;
	}

	public List<AxiomaticDefinitionsBlock> getAxiomaticDefinitions() {
		return axiomaticDefinitions;
	}

	public List<Theory> getImported() {
		return imported;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Theory) {
			return name.equals(((Theory) obj).getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
