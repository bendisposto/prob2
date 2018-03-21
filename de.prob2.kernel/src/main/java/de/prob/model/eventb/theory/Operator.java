package de.prob.model.eventb.theory;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;
import de.prob.unicode.UnicodeTranslator;

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
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.internal.core.ast.extension.ExtensionKind;

public class Operator extends AbstractElement {

	private final boolean associative;
	private final IOperatorProperties.FormulaType formulaType;
	private final IOperatorProperties.Notation notation;
	private final boolean commutative;
	private final IOperatorDefinition definition;
	private final String theoryName;
	private IFormulaExtension extension;
	private final ModelElementList<OperatorArgument> operatorArguments;
	private final String groupId;
	private final EventB wd;
	private final EventB type;
	private final EventB predicate;
	private final EventB syntax;

	private static IOperatorProperties.FormulaType formulaTypeFromBoolean(final boolean formulaType) {
		return formulaType ? IOperatorProperties.FormulaType.EXPRESSION : IOperatorProperties.FormulaType.PREDICATE;
	}

	private static IOperatorProperties.Notation notationTypeFromString(final String notationType) {
		switch (notationType) {
			case "PREFIX":
				return IOperatorProperties.Notation.PREFIX;

			case "INFIX":
				return IOperatorProperties.Notation.INFIX;

			default:
				return IOperatorProperties.Notation.POSTFIX;
		}
	}

	public Operator(final String theoryName, final String operator,
			final boolean associative, final boolean commutative,
			final boolean formulaType, final String notationType,
			final String groupId, final String type, final String wd,
			final String predicate, final Set<IFormulaExtension> typeEnv) {
		this(
			theoryName,
			new EventB(operator, typeEnv),
			associative,
			commutative,
			formulaTypeFromBoolean(formulaType),
			notationTypeFromString(notationType),
			groupId,
			type == null ? null : new EventB(type, typeEnv),
			new EventB(wd, typeEnv),
			new EventB(predicate, typeEnv),
			null,
			null
		);
	}

	public Operator(final String theoryName, final EventB syntax,
			final boolean associative, final boolean commutative,
			final IOperatorProperties.FormulaType formulaType,
			final IOperatorProperties.Notation notationType,
			final String groupId, final EventB type, final EventB wd,
			final EventB predicate,
			ModelElementList<OperatorArgument> operatorArguments,
			IOperatorDefinition definition) {
		this.theoryName = theoryName;
		this.groupId = groupId;
		this.syntax = syntax;
		this.associative = associative;
		this.commutative = commutative;
		this.formulaType = formulaType;
		this.notation = notationType;
		this.type = type;
		this.wd = wd;
		this.predicate = predicate;
		this.definition = definition;
		this.operatorArguments = operatorArguments;
	}

	public Operator addArguments(
			final ModelElementList<OperatorArgument> arguments) {
		return new Operator(theoryName, syntax, associative, commutative,
				formulaType, notation, groupId, type, wd, predicate, arguments,
				definition);
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
	public Operator setDefinition(final IOperatorDefinition definition) {
		return new Operator(theoryName, syntax, associative, commutative,
				formulaType, notation, groupId, type, wd, predicate,
				operatorArguments, definition);
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
		return Objects.equals(syntax, other.syntax)
				&& Objects.equals(theoryName, other.theoryName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(syntax, theoryName);
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
			if (formulaType.equals(IOperatorProperties.FormulaType.EXPRESSION)
					&& notation.equals(IOperatorProperties.Notation.INFIX) && associative) {
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
			return true;
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
