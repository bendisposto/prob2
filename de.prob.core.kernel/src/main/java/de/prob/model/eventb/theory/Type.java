package de.prob.model.eventb.theory;

import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extension.StandardGroup;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.unicode.UnicodeTranslator;

public class Type extends AbstractElement {

	private final EventB identifier;
	private IFormulaExtension ext;

	public Type(final String identifier, final Set<IFormulaExtension> typeEnv) {
		this.identifier = new EventB(identifier, typeEnv);
	}

	public EventB getIdentifier() {
		return identifier;
	}

	@Override
	public String toString() {
		return identifier.getCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Type) {
			return identifier.equals(((Type) obj).getIdentifier());
		}
		return false;
	}

	public IFormulaExtension getFormulaExtension() {
		if (ext == null) {
			ext = new TypeExtension();
		}
		return ext;
	}

	private class TypeExtension implements IExpressionExtension {

		String symbol;

		public TypeExtension() {
			symbol = UnicodeTranslator.toUnicode(identifier.getCode());
		}

		@Override
		public String getSyntaxSymbol() {
			return symbol;
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
			return symbol + " Type";
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
			// No compatibilities
		}

		@Override
		public void addPriorities(final IPriorityMediator mediator) {
			// No priorities
		}

		@Override
		public org.eventb.core.ast.Type synthesizeType(
				final Expression[] childExprs, final Predicate[] childPreds,
				final ITypeMediator mediator) {
			// Not intended to be type checked.
			return null;
		}

		@Override
		public boolean verifyType(final org.eventb.core.ast.Type proposedType,
				final Expression[] childExprs, final Predicate[] childPreds) {
			// Not intended to be type checked.
			return false;
		}

		@Override
		public org.eventb.core.ast.Type typeCheck(
				final ExtendedExpression expression,
				final ITypeCheckMediator tcMediator) {
			// Not intended to be type checked.
			return null;
		}

		@Override
		public boolean isATypeConstructor() {
			return true;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof TypeExtension) {
				return this.getSyntaxSymbol().equals(
						((TypeExtension) obj).getSyntaxSymbol());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return getSyntaxSymbol().hashCode();
		}

		@Override
		public String toString() {
			return "type " + getSyntaxSymbol();
		}

	}
}
