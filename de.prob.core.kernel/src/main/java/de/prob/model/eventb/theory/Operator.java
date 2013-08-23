package de.prob.model.eventb.theory;

import java.util.List;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;

public class Operator extends AbstractElement {

	private final String name;
	private final boolean associative;
	private final boolean formulaType;
	private final String notationType;
	private final boolean commutative;
	private final IOperatorDefinition definition;

	public Operator(final String name, final boolean associative,
			final boolean commutative, final boolean formulaType,
			final String notationType, final IOperatorDefinition definition) {
		this.name = name;
		this.associative = associative;
		this.commutative = commutative;
		this.formulaType = formulaType;
		this.notationType = notationType;
		this.definition = definition;
	}

	public void addArguments(final List<OperatorArgument> arguments) {
		put(OperatorArgument.class, arguments);
	}

	public void addWDConditions(final List<OperatorWDCondition> conditions) {
		put(OperatorWDCondition.class, conditions);
	}

	public String getName() {
		return name;
	}

	public boolean isAssociative() {
		return associative;
	}

	public boolean isCommutative() {
		return commutative;
	}

	public boolean isFormulaType() {
		return formulaType;
	}

	public String getNotationType() {
		return notationType;
	}

	public IOperatorDefinition getDefinition() {
		return definition;
	}

	public List<OperatorArgument> getArguments() {
		return new ModelElementList<OperatorArgument>(
				getChildrenOfType(OperatorArgument.class));
	}

	public List<OperatorWDCondition> getWDConditions() {
		return new ModelElementList<OperatorWDCondition>(
				getChildrenOfType(OperatorWDCondition.class));
	}

	@Override
	public String toString() {
		return name;
	}

}
