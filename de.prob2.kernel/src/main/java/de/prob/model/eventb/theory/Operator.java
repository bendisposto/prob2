package de.prob.model.eventb.theory;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ExtensionFactory;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.internal.core.ast.extension.ExtensionKind;

import com.google.common.base.Objects;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;
import de.prob.unicode.UnicodeTranslator;

public class Operator extends AbstractElement {

	private final EventB syntax;
	private final boolean associative;
	private final IOperatorProperties.FormulaType formulaType;
	private final IOperatorProperties.Notation notation;
	private final boolean commutative;
	private IOperatorDefinition definition;
	private final String theoryName;
	private IFormulaExtension extension;
	private ModelElementList<OperatorArgument> operatorArguments;
	private final String groupId;
	private final EventB wd;
	private final EventB type;
	private final EventB predicate;

	public Operator(final String theoryName, final String operator,
			final boolean associative, final boolean commutative,
			final boolean formulaType, final String notationType,
			final String groupId, final String type, final String wd,
			final String predicate, final Set<IFormulaExtension> typeEnv) {
		this.theoryName = theoryName;
		this.groupId = groupId;
		syntax = new EventB(operator, typeEnv);
		this.associative = associative;
		this.commutative = commutative;
		this.formulaType = formulaType ? IOperatorProperties.FormulaType.EXPRESSION
				: IOperatorProperties.FormulaType.PREDICATE;
		notation = notationType.equals("PREFIX") ? IOperatorProperties.Notation.PREFIX
				: (notationType.equals("INFIX") ? IOperatorProperties.Notation.INFIX
						: IOperatorProperties.Notation.POSTFIX);
		this.type = type == null ? null : new EventB(type, typeEnv);
		this.wd = new EventB(wd, typeEnv);
		this.predicate = new EventB(predicate, typeEnv);
	}

	public void addArguments(final ModelElementList<OperatorArgument> arguments) {
		put(OperatorArgument.class, arguments);
		operatorArguments = arguments;
	}

	public EventB getSyntax() {
		return syntax;
	}

	public boolean isAssociative() {
		return associative;
	}

	public boolean isCommutative() {
		return commutative;
	}

	public IOperatorProperties.FormulaType getFormulaType() {
		return formulaType;
	}

	public IOperatorProperties.Notation getNotation() {
		return notation;
	}

	public IOperatorDefinition getDefinition() {
		return definition;
	}

	/**
	 * @param definition
	 *            of type {@link IOperatorDefinition}. For
	 *            AxiomaticOperatorDefinitions this will never be set.
	 */
	public void setDefinition(final IOperatorDefinition definition) {
		this.definition = definition;
	}

	public List<OperatorArgument> getArguments() {
		return operatorArguments;
	}

	public String getParentTheory() {
		return theoryName;
	}

	public EventB getWD() {
		return wd;
	}

	public EventB getPredicate() {
		return predicate;
	}

	public EventB getType() {
		return type;
	}

	@Override
	public String toString() {
		return syntax.getCode();
	}

	public IFormulaExtension getFormulaExtension() {
		if (extension == null) {
			if (formulaType.equals(IOperatorProperties.FormulaType.PREDICATE)) {
				extension = new PredicateOperatorExtension(syntax);
			} else {
				extension = new ExpressionOperatorExtension(syntax);
			}

		}
		return extension;
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
		Operator other = (Operator) obj;
		return Objects.equal(syntax, other.syntax)
				&& Objects.equal(theoryName, other.theoryName);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(syntax, theoryName);
	}

	private class OperatorExtension implements IFormulaExtension {

		private final String unicode;

		public OperatorExtension(final EventB syntax) {
			unicode = UnicodeTranslator.toUnicode(syntax.getCode());
		}

		@Override
		public String getSyntaxSymbol() {
			return unicode;
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
			return theoryName + "." + unicode;
		}

		@Override
		public String getGroupId() {
			return groupId;
		}

		@Override
		public IExtensionKind getKind() {
			if (formulaType.equals(FormulaType.EXPRESSION)
					&& notation.equals(Notation.INFIX) && associative) {
				return new ExtensionKind(notation, formulaType,
						ExtensionFactory.TWO_OR_MORE_EXPRS, true);
			}
			return new ExtensionKind(notation, formulaType,
					ExtensionFactory.makeAllExpr(ExtensionFactory.makeArity(
							getArguments().size(), getArguments().size())),
					false);
		}

		@Override
		public Object getOrigin() {
			return null;
		}

		@Override
		public void addCompatibilities(final ICompatibilityMediator mediator) {
			// Nothing to add
		}

		@Override
		public void addPriorities(final IPriorityMediator mediator) {
			// Nothing to add
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof OperatorExtension) {
				return ((OperatorExtension) obj).getId().equals(getId());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return getId().hashCode();
		}

		@Override
		public String toString() {
			return "operator " + getSyntaxSymbol();
		}
	}

	private class PredicateOperatorExtension extends OperatorExtension
			implements IPredicateExtension {

		public PredicateOperatorExtension(final EventB syntax) {
			super(syntax);
		}

		@Override
		public void typeCheck(final ExtendedPredicate predicate,
				final ITypeCheckMediator tcMediator) {
			// This extension is intended to adapt the parser but not the
			// typechecker
		}
	}

	private class ExpressionOperatorExtension extends OperatorExtension
			implements IExpressionExtension {

		public ExpressionOperatorExtension(final EventB syntax) {
			super(syntax);
		}

		@Override
		public Type synthesizeType(final Expression[] childExprs,
				final Predicate[] childPreds, final ITypeMediator mediator) {
			// This extension is intended to adapt the parser but not the
			// typechecker
			return null;
		}

		@Override
		public boolean verifyType(final Type proposedType,
				final Expression[] childExprs, final Predicate[] childPreds) {
			// This extension is intended to adapt the parser but not the
			// typechecker
			return false;
		}

		@Override
		public Type typeCheck(final ExtendedExpression expression,
				final ITypeCheckMediator tcMediator) {
			// This extension is intended to adapt the parser but not the
			// typechecker
			return null;
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

	}

}
