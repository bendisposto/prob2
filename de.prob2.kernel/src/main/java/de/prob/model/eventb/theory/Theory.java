package de.prob.model.eventb.theory;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

import com.github.krukow.clj_lang.PersistentHashMap;
import com.google.common.base.Objects;

import de.prob.model.eventb.EventBAxiom;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;
import de.prob.tmparser.OperatorMapping;

public class Theory extends AbstractElement {

	private final String name;
	private final String parentDirectory;
	private final Collection<OperatorMapping> proBMappings;
	private Set<IFormulaExtension> typeEnvironment;

	public Theory(final String name, final String parentDirectory,
			final Collection<OperatorMapping> mappings) {
		this(
				name,
				parentDirectory,
				mappings,
				Collections.<IFormulaExtension> emptySet(),
				PersistentHashMap
						.<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> emptyMap());
	}

	private Theory(
			final String name,
			final String parentDirectory,
			final Collection<OperatorMapping> proBMappings,
			Set<IFormulaExtension> typeEnvironment,
			PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children) {
		super(children);
		this.name = name;
		this.parentDirectory = parentDirectory;
		this.proBMappings = proBMappings;
		this.typeEnvironment = typeEnvironment;
	}

	public Theory set(Class<? extends AbstractElement> clazz,
			ModelElementList<? extends AbstractElement> elements) {
		return new Theory(name, parentDirectory, proBMappings, typeEnvironment,
				assoc(clazz, elements));
	}

	public ModelElementList<DataType> getDataTypes() {
		return getChildrenOfType(DataType.class);
	}

	public ModelElementList<Theory> getImported() {
		return getChildrenOfType(Theory.class);
	}

	public ModelElementList<Operator> getOperators() {
		return getChildrenOfType(Operator.class);
	}

	public ModelElementList<AxiomaticDefinitionBlock> getAxiomaticDefinitionBlocks() {
		return getChildrenOfType(AxiomaticDefinitionBlock.class);
	}

	public ModelElementList<ProofRulesBlock> getProofRules() {
		return getChildrenOfType(ProofRulesBlock.class);
	}

	public ModelElementList<EventBAxiom> getTheorems() {
		return getChildrenOfType(EventBAxiom.class);
	}

	public ModelElementList<Type> getTypeParameters() {
		return getChildrenOfType(Type.class);
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

	public Theory setTypeEnvironment(Set<IFormulaExtension> typeEnvironment) {
		return new Theory(name, parentDirectory, proBMappings, typeEnvironment,
				children);
	}

	public Set<IFormulaExtension> getTypeEnvironment() {
		return typeEnvironment;
	}
}
