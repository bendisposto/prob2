package de.prob.model.brules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.be4.classicalb.core.parser.rules.*;
import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.ComputationNotCompletedResult;
import de.prob.animator.domainobjects.EnumerationWarning;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.TranslatedEvalResult;
import de.prob.animator.domainobjects.WDError;
import de.prob.translator.types.BObject;
import de.prob.translator.types.Tuple;

public class RuleResult {
	private final RuleOperation ruleOperation;
	private final AbstractEvalResult evalResult;
	private final int numberOfViolations;
	private final List<CounterExampleResult> counterExamples = new ArrayList<>();
	// causes leading to NOT_CHECKED result
	private final ArrayList<String> failedDependencies = new ArrayList<>();

	private final ArrayList<String> notCheckedDependencies = new ArrayList<>();

	public enum RESULT_ENUM {
		FAIL, SUCCESS, NOT_CHECKED, DISABLED
	}

	static final Map<String, RESULT_ENUM> resultMapping = new HashMap<>();

	static {
		resultMapping.put(RulesTransformation.RULE_FAIL, RESULT_ENUM.FAIL);
		resultMapping.put(RulesTransformation.RULE_SUCCESS, RESULT_ENUM.SUCCESS);
		resultMapping.put(RulesTransformation.RULE_NOT_CHECKED, RESULT_ENUM.NOT_CHECKED);
		resultMapping.put(RulesTransformation.RULE_DISABLED, RESULT_ENUM.DISABLED);
	}

	public RuleResult(RuleOperation rule, AbstractEvalResult result, AbstractEvalResult numberOfCounterExamples,
			AbstractEvalResult counterExampleResult) {
		this.ruleOperation = rule;
		this.evalResult = result;
		this.numberOfViolations = Integer.parseInt(((EvalResult) numberOfCounterExamples).getValue());
		transformCounterExamples(counterExampleResult);
	}

	public RuleOperation getRuleOperation() {
		return this.ruleOperation;
	}

	public int getNumberOfViolations() {
		return this.numberOfViolations;
	}

	private void transformCounterExamples(AbstractEvalResult abstractEvalResult) {
		// the following tests are only here for sake of completeness
		// because abstractEvalResult should be an instance of EvalResult
		if (abstractEvalResult instanceof ComputationNotCompletedResult) {
			throw new AssertionError(abstractEvalResult.toString());
		} else if (abstractEvalResult instanceof WDError) {
			WDError wdError = (WDError) abstractEvalResult;
			throw new AssertionError(wdError.getResult());
		} else if (abstractEvalResult instanceof EnumerationWarning) {
			throw new AssertionError("Enumeration warning");
		}
		EvalResult evalCurrent = (EvalResult) abstractEvalResult;
		TranslatedEvalResult translatedResult = null;
		try {
			translatedResult = evalCurrent.translate();
		} catch (Exception e) {
			/*- fall back solution if the result can not be parsed (e.g. {1,...,1000}) */
			final String message = evalCurrent.getValue().replaceAll("\"", "");
			counterExamples.add(new CounterExampleResult(1, message));
			return;
		}

		if (translatedResult.getValue() instanceof de.prob.translator.types.Set) {
			de.prob.translator.types.Set set = (de.prob.translator.types.Set) translatedResult.getValue();
			for (final BObject object : set) {
				if (object instanceof Tuple) {
					final Tuple tuple = (Tuple) object;
					de.prob.translator.types.Number first = (de.prob.translator.types.Number) tuple.getFirst();
					int errorType = first.intValue();
					de.prob.translator.types.String second = (de.prob.translator.types.String) tuple.getSecond();
					String message = second.getValue();
					counterExamples.add(new CounterExampleResult(errorType, message));
				} else {
					throw new AssertionError();
				}
			}
		} else if (translatedResult.getValue() instanceof de.prob.translator.types.Sequence) {
			de.prob.translator.types.Sequence sequence = (de.prob.translator.types.Sequence) translatedResult
					.getValue();
			for (int i = 1; i < sequence.size(); i++) {
				de.prob.translator.types.String value = (de.prob.translator.types.String) sequence.get(i);
				String message = value.getValue();
				counterExamples.add(new CounterExampleResult(i, message));
			}
		} else {
			final String message = evalCurrent.getValue().replaceAll("\"", "");
			counterExamples.add(new CounterExampleResult(1, message));
			return;
		}

	}

	public void addAdditionalInformation(Set<String> failingRules, Set<String> allNotCheckedRules) {
		for (AbstractOperation abstractOperation : ruleOperation.getTransitiveDependencies()) {
			String operationName = abstractOperation.getName();
			if (failingRules.contains(operationName)) {
				this.failedDependencies.add(operationName);
			} else if (allNotCheckedRules.contains(operationName)) {
				notCheckedDependencies.add(operationName);
			}
		}
	}

	public List<String> getFailedDependencies() {
		return this.failedDependencies;
	}

	public List<String> getNotCheckedDependencies() {
		return this.failedDependencies;
	}

	public List<CounterExampleResult> getCounterExamples() {
		return this.counterExamples;
	}

	public String getRuleName() {
		return this.ruleOperation.getName();
	}

	public String getResultValue() {
		String res = evalResult.toString();
		res = res.substring(1, res.length() - 1);
		return res;
	}

	public boolean hasRuleId() {
		return ruleOperation.getRuleIdString() != null;
	}

	public String getRuleId() {
		return ruleOperation.getRuleIdString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[OperationName: ").append(this.getRuleName());
		sb.append(", Result: ").append(this.getResultValue());
		if (this.getRuleId() != null) {
			sb.append(", RuleID: " + this.getRuleId());
		}
		if (this.numberOfViolations > 0) {
			sb.append(", NumberOfViolations: " + this.numberOfViolations);
			sb.append(", Violations: " + this.counterExamples);
		}
		if (!this.failedDependencies.isEmpty()) {
			sb.append(", FailedDependencies: " + this.failedDependencies);
		}
		if (!this.notCheckedDependencies.isEmpty()) {
			sb.append(", NotCheckedDependencies: " + this.notCheckedDependencies);
		}
		sb.append("]");
		return sb.toString();
	}

	public RESULT_ENUM getResultEnum() {
		return resultMapping.get(this.getResultValue());
	}

	public boolean hasFailed() {
		return getResultEnum() == RESULT_ENUM.FAIL;
	}

	public class CounterExampleResult {
		private final int errorType;
		private final String message;

		public CounterExampleResult(int errorType, String message) {
			this.errorType = errorType;
			this.message = message;
		}

		public String getMessage() {
			return this.message;
		}

		public String getErrorTypeAsString() {
			return String.valueOf(errorType);
		}

		public int getErrorType() {
			return this.errorType;
		}

		@Override
		public String toString() {
			return this.message;
		}
	}

}
