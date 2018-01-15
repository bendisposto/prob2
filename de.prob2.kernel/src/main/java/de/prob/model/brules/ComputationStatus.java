package de.prob.model.brules;

import java.util.HashMap;
import java.util.Map;

import de.be4.classicalb.core.parser.rules.RulesTransformation;
import de.prob.animator.domainobjects.AbstractEvalResult;

public enum ComputationStatus implements OperationStatus {

	EXECUTED(RulesTransformation.COMPUTATION_EXECUTED), DISABLED(
			RulesTransformation.COMPUTATION_DISABLED), NOT_EXECUTED(RulesTransformation.COMPUTATION_NOT_EXECUTED);

	private final String bValue;

	private static Map<String, ComputationStatus> mapping = new HashMap<>();
	static {
		for (ComputationStatus compState : ComputationStatus.values()) {
			mapping.put(compState.bValue, compState);
		}
	}

	ComputationStatus(String value) {
		this.bValue = value;
	}

	public static ComputationStatus valueOf(AbstractEvalResult evalResult) {
		String key = evalResult.toString();
		key = key.substring(1, key.length() - 1);
		if (mapping.containsKey(key)) {
			return mapping.get(key);
		} else {
			throw new IllegalArgumentException(String.format(
					"The result value '%s' is not valid result of a computation operation.", evalResult.toString()));
		}

	}

	@Override
	public boolean isExecuted() {
		return this == EXECUTED;
	}

	@Override
	public boolean isNotExecuted() {
		return this != EXECUTED;
	}

	@Override
	public boolean isDisabled() {
		return this == DISABLED;
	}

}
