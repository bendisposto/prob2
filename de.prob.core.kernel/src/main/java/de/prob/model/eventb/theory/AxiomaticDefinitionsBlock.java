package de.prob.model.eventb.theory;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extension.StandardGroup;

import de.prob.model.eventb.EventBAxiom;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Axiom;
import de.prob.model.representation.ModelElementList;

public class AxiomaticDefinitionsBlock extends AbstractElement {

	private final String name;
	private final List<EventBAxiom> definitionAxioms = new ModelElementList<EventBAxiom>();
	private final List<Operator> operatorDefinitions = new ModelElementList<Operator>();
	private final List<Type> typeDefinitions = new ModelElementList<Type>();

	public AxiomaticDefinitionsBlock(final String name) {
		this.name = name;
	}

	public void addDefinitionAxioms(final List<EventBAxiom> axioms) {
		put(Axiom.class, axioms);
		definitionAxioms.addAll(axioms);
	}

	public void addOperatorDefinitions(final List<Operator> definitions) {
		put(Operator.class, definitions);
		operatorDefinitions.addAll(definitions);
	}

	public void addTypeDefinitions(final List<Type> defs) {
		put(Type.class, defs);
		typeDefinitions.addAll(defs);
	}

	public List<EventBAxiom> getDefinitionAxioms() {
		return definitionAxioms;
	}

	public List<Operator> getOperatorDefinitions() {
		return operatorDefinitions;
	}

	public List<Type> getTypeDefinitions() {
		return typeDefinitions;
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
		if (obj instanceof AxiomaticDefinitionsBlock) {
			return name.equals(((AxiomaticDefinitionsBlock) obj).getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public List<AxiomaticTypeExtension> getAxiomaticTypeExtensions() {
		List<AxiomaticTypeExtension> extensions = new ArrayList<AxiomaticDefinitionsBlock.AxiomaticTypeExtension>();
		for (Type type : getTypeDefinitions()) {
			extensions.add(new AxiomaticTypeExtension(type));
		}
		return extensions;
	}

	private class AxiomaticTypeExtension implements IFormulaExtension {

		private final Type type;

		public AxiomaticTypeExtension(final Type type) {
			this.type = type;
		}

		@Override
		public String getSyntaxSymbol() {
			return type.getName();
		}

		@Override
		public Predicate getWDPredicate(final IExtendedFormula formula,
				final IWDMediator wdMediator) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public String getId() {
			return type.getName() + " Axiomatic Type";
		}

		@Override
		public String getGroupId() {
			return StandardGroup.ATOMIC_EXPR.getId();
		}

		@Override
		public IExtensionKind getKind() {
			return ATOMIC_EXPRESSION;
		}

		@Override
		public Object getOrigin() {
			return null;
		}

		@Override
		public void addCompatibilities(final ICompatibilityMediator mediator) {
			// no compatibilities
		}

		@Override
		public void addPriorities(final IPriorityMediator mediator) {
			// no priorities
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof AxiomaticTypeExtension) {
				return getId().equals(((AxiomaticTypeExtension) obj).getId());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return getId().hashCode();
		}

	}
}
