package de.prob.model.eventb.theory;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import com.github.krukow.clj_lang.PersistentHashMap;

import de.prob.model.eventb.EventBAxiom;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;
import de.prob.model.representation.Named;
import de.prob.tmparser.OperatorMapping;

import org.eventb.core.ast.extension.IFormulaExtension;

public class Theory extends AbstractElement implements Named {

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

	@Override
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
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final Theory other = (Theory)obj;
		return Objects.equals(this.getParentDirectoryName(), other.getParentDirectoryName())
				&& Objects.equals(this.getName(), other.getName());
	}

	public Collection<OperatorMapping> getProBMappings() {
		return proBMappings;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getParentDirectoryName(), this.getName());
	}

	public Theory setTypeEnvironment(Set<IFormulaExtension> typeEnvironment) {
		return new Theory(name, parentDirectory, proBMappings, typeEnvironment,
				children);
	}

	public Set<IFormulaExtension> getTypeEnvironment() {
		return typeEnvironment;
	}
}
