package de.prob.model.eventb.theory;

import de.prob.model.eventb.EventBAxiom;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class Theory extends AbstractElement {

	private final String name;
	private ModelElementList<DataType> dataTypes = new ModelElementList<DataType>();
	private ModelElementList<Theory> imported = new ModelElementList<Theory>();
	private ModelElementList<Operator> operators = new ModelElementList<Operator>();
	private ModelElementList<ProofRulesBlock> proofRules = new ModelElementList<ProofRulesBlock>();
	private ModelElementList<EventBAxiom> theorems = new ModelElementList<EventBAxiom>();
	private ModelElementList<Type> typeParameters = new ModelElementList<Type>();
	private final String parentDirectory;

	public Theory(final String name, final String parentDirectory) {
		this.name = name;
		this.parentDirectory = parentDirectory;
	}

	public void addOperators(final ModelElementList<Operator> operators) {
		put(Operator.class, operators);
		this.operators = operators;
	}

	public void addDataTypes(final ModelElementList<DataType> dataTypes) {
		put(DataType.class, dataTypes);
		this.dataTypes = dataTypes;
	}

	public void addTheorems(final ModelElementList<EventBAxiom> theorems) {
		put(EventBAxiom.class, theorems);
		this.theorems = theorems;
	}

	public void addProofRules(final ModelElementList<ProofRulesBlock> proofRules) {
		put(ProofRulesBlock.class, proofRules);
		this.proofRules = proofRules;
	}

	public void addTypeParameters(final ModelElementList<Type> parameters) {
		put(Type.class, parameters);
		typeParameters = parameters;
	}

	public void addImported(final ModelElementList<Theory> theories) {
		put(Theory.class, theories);
		imported = theories;
	}

	public ModelElementList<DataType> getDataTypes() {
		return dataTypes;
	}

	public ModelElementList<Theory> getImported() {
		return imported;
	}

	public ModelElementList<Operator> getOperators() {
		return operators;
	}

	public ModelElementList<ProofRulesBlock> getProofRules() {
		return proofRules;
	}

	public ModelElementList<EventBAxiom> getTheorems() {
		return theorems;
	}

	public ModelElementList<Type> getTypeParameters() {
		return typeParameters;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getParentDirectoryName() {
		return parentDirectory;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Theory) {
			return name.equals(((Theory) obj).getName())
					&& parentDirectory.equals(((Theory) obj)
							.getParentDirectoryName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 13 * name.hashCode() + 27 * parentDirectory.hashCode();
	}
}
