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
import org.eventb.core.ast.extension.StandardGroup;
import org.eventb.internal.core.ast.extension.ExtensionKind;

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
	private final IOperatorDefinition definition;
	private final String theoryName;
	private IFormulaExtension extension;
	private final List<OperatorArgument> operatorArguments = new ModelElementList<OperatorArgument>();
	private final List<OperatorWDCondition> wdConditions = new ModelElementList<OperatorWDCondition>();

	public Operator(final String theoryName, final String operator,
			final boolean associative, final boolean commutative,
			final boolean formulaType, final String notationType,
			final IOperatorDefinition definition,
			final Set<IFormulaExtension> typeEnv) {
		this.theoryName = theoryName;
		syntax = new EventB(operator, typeEnv);
		this.associative = associative;
		this.commutative = commutative;
		this.formulaType = formulaType ? IOperatorProperties.FormulaType.EXPRESSION
				: IOperatorProperties.FormulaType.PREDICATE;
		notation = notationType.equals("PREFIX") ? IOperatorProperties.Notation.PREFIX
				: (notationType.equals("INFIX") ? IOperatorProperties.Notation.INFIX
						: IOperatorProperties.Notation.POSTFIX);
		this.definition = definition;
	}

	public void addArguments(final List<OperatorArgument> arguments) {
		put(OperatorArgument.class, arguments);
		operatorArguments.addAll(arguments);
	}

	public void addWDConditions(final List<OperatorWDCondition> conditions) {
		put(OperatorWDCondition.class, conditions);
		wdConditions.addAll(conditions);
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

	public List<OperatorArgument> getArguments() {
		return operatorArguments;
	}

	public List<OperatorWDCondition> getWDConditions() {
		return wdConditions;
	}

	public String getParentTheory() {
		return theoryName;
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
		if (obj == this) {
			return true;
		}
		if (obj instanceof Operator) {
			return theoryName.equals(((Operator) obj).getParentTheory())
					&& syntax.equals(((Operator) obj).getSyntax());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 13 * theoryName.hashCode() + 17 * syntax.hashCode();
	}

	private class OperatorExtension implements IFormulaExtension {

		String unicode;

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
			return getGroupFor(formulaType, notation, getArguments().size());
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

		private String getGroupFor(final FormulaType formulaType,
				final Notation notation, final int arity) {
			String group = "NEW THEORY GROUP";
			switch (formulaType) {
			case EXPRESSION: {
				switch (notation) {
				case INFIX: {
					break;
				}
				case PREFIX: {
					if (arity > 0) {
						group = StandardGroup.CLOSED.getId();
					} else {
						group = StandardGroup.ATOMIC_EXPR.getId();
					}
					break;
				}
				case POSTFIX: {
					// leave as part of the dummy group TODO check this
				}
				}
				break;
			}
			case PREDICATE: {
				switch (notation) {
				case INFIX: {
					if (arity == 0) {
						group = StandardGroup.ATOMIC_PRED.getId();
					}
					// infix makes sense for ops with more than two args
					if (arity > 1) {
						group = StandardGroup.INFIX_PRED.getId();
					}
					break;
				}
				case PREFIX: {
					if (arity > 0) {
						group = StandardGroup.CLOSED.getId();
					} else {
						group = StandardGroup.ATOMIC_PRED.getId();
					}
					break;
				}
				case POSTFIX: {
					// leave as part of the dummy group TODO check this
				}
				}
			}
			}
			return group;
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
	}

	private class PredicateOperatorExtension extends OperatorExtension
			implements IPredicateExtension {

		public PredicateOperatorExtension(final EventB syntax) {
			super(syntax);
		}

		@Override
		public void typeCheck(final ExtendedPredicate predicate,
				final ITypeCheckMediator tcMediator) {
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean verifyType(final Type proposedType,
				final Expression[] childExprs, final Predicate[] childPreds) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Type typeCheck(final ExtendedExpression expression,
				final ITypeCheckMediator tcMediator) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

	}

}
