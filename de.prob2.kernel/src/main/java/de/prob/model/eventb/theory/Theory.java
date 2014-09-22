package de.prob.model.eventb.theory;

import java.util.Collection;

import com.google.common.base.Objects;

import de.prob.model.eventb.EventBAxiom;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;
import de.prob.tmparser.OperatorMapping;

public class Theory extends AbstractElement {

	private final String name;
	private ModelElementList<DataType> dataTypes = new ModelElementList<DataType>();
	private ModelElementList<Theory> imported = new ModelElementList<Theory>();
	private ModelElementList<Operator> operators = new ModelElementList<Operator>();
	private ModelElementList<ProofRulesBlock> proofRules = new ModelElementList<ProofRulesBlock>();
	private ModelElementList<EventBAxiom> theorems = new ModelElementList<EventBAxiom>();
	private ModelElementList<Type> typeParameters = new ModelElementList<Type>();
	private final String parentDirectory;
	private ModelElementList<AxiomaticDefinitionBlock> axiomaticDefinitionBlocks;
	private final Collection<OperatorMapping> proBMappings;

	public Theory(final String name, final String parentDirectory,
			final Collection<OperatorMapping> mappings) {
		this.name = name;
		this.parentDirectory = parentDirectory;
		proBMappings = mappings;
	}

	public void addOperators(final ModelElementList<Operator> operators) {
		put(Operator.class, operators);
		this.operators = operators;
	}

	public void addAxiomaticDefintionsBlocks(
			final ModelElementList<AxiomaticDefinitionBlock> axiomaticDefinitionBlocks) {
		put(AxiomaticDefinitionBlock.class, axiomaticDefinitionBlocks);
		this.axiomaticDefinitionBlocks = axiomaticDefinitionBlocks;
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

	public ModelElementList<AxiomaticDefinitionBlock> getAxiomaticDefinitionBlocks() {
		return axiomaticDefinitionBlocks;
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Theory other = (Theory) obj;
		return Objects.equal(parentDirectory, other.parentDirectory)
				&& Objects.equal(name, other.name);
	}

	public Collection<OperatorMapping> getProBMappings() {
		return proBMappings;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(parentDirectory, name);
	}
}
