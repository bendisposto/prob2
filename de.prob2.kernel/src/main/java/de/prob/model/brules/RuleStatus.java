package de.prob.model.brules;

import java.util.HashMap;
import java.util.Map;

import de.be4.classicalb.core.parser.rules.RulesTransformation;
import de.prob.animator.domainobjects.AbstractEvalResult;

public enum RuleStatus implements OperationStatus {
	FAIL(RulesTransformation.RULE_FAIL), SUCCESS(RulesTransformation.RULE_SUCCESS), NOT_CHECKED(
			RulesTransformation.RULE_NOT_CHECKED), DISABLED(RulesTransformation.RULE_DISABLED);

	private final String bValue;

	RuleStatus(String bValue) {
		this.bValue = bValue;
	}

	private static final Map<String, RuleStatus> mapping = new HashMap<>();
	static {
		for (RuleStatus compState : RuleStatus.values()) {
			mapping.put(compState.bValue, compState);
		}
	}

	public static RuleStatus valueOf(AbstractEvalResult evalResult) {
		String res = evalResult.toString();
		res = res.substring(1, res.length() - 1);
		if (mapping.containsKey(res)) {
			return mapping.get(res);
		} else {
			throw new IllegalArgumentException(String
					.format("The result value '%s' is not valid result of a rule operation.", evalResult.toString()));
		}
	}

	@Override
	public boolean isExecuted() {
		return this == FAIL || this == SUCCESS;
	}

	@Override
	public boolean isNotExecuted() {
		return this == NOT_CHECKED || this == DISABLED;
	}

	@Override
	public boolean isDisabled() {
		return this == RuleStatus.DISABLED;
	}

}
