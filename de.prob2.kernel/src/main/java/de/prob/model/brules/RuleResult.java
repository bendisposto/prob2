package de.prob.model.brules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.be4.classicalb.core.parser.rules.*;
import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.TranslatedEvalResult;
import de.prob.translator.types.BObject;
import de.prob.translator.types.Tuple;

public class RuleResult {
	private final RuleOperation ruleOperation;
	private final RuleState ruleState;
	private final int numberOfViolations;
	private final List<CounterExample> counterExamples = new ArrayList<>();

	// causes leading to NOT_CHECKED result
	private final ArrayList<String> allFailedDependencies = new ArrayList<>();
	private final ArrayList<String> allNotCheckedDependencies = new ArrayList<>();

	public RuleResult(RuleOperation rule, AbstractEvalResult result, AbstractEvalResult numberOfCounterExamples,
			AbstractEvalResult counterExampleResult) {
		this.ruleOperation = rule;
		this.ruleState = RuleState.valueOf(result);
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
		EvalResult evalCurrent = (EvalResult) abstractEvalResult;
		TranslatedEvalResult translatedResult = null;
		try {
			translatedResult = evalCurrent.translate();
		} catch (Exception e) {
			/*- fall back solution if the result can not be parsed (e.g. {1,...,1000}) 
			 * should not not happen because MAX_DISPLAY_SET is set to -1 
			 * and hence, no truncated terms are delivered by ProBCore
			 * */
			final String message = evalCurrent.getValue().replaceAll("\"", "");
			counterExamples.add(new CounterExample(1, message));
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
					counterExamples.add(new CounterExample(errorType, message));
				} else {
					throw new AssertionError();
				}
			}
		} else if (translatedResult.getValue() instanceof de.prob.translator.types.Sequence) {
			de.prob.translator.types.Sequence sequence = (de.prob.translator.types.Sequence) translatedResult
					.getValue();
			for (int i = 1; i <= sequence.size(); i++) {
				de.prob.translator.types.String value = (de.prob.translator.types.String) sequence.get(i);
				String message = value.getValue();
				counterExamples.add(new CounterExample(i, message));
			}
		} else {
			// fall back: should not happen
			counterExamples.add(new CounterExample(1, evalCurrent.getValue()));
		}

	}

	public void addAdditionalInformation(Set<String> allFailingRules, Set<String> allNotCheckedRules) {
		for (AbstractOperation abstractOperation : ruleOperation.getTransitiveDependencies()) {
			String operationName = abstractOperation.getName();
			if (allFailingRules.contains(operationName)) {
				this.allFailedDependencies.add(operationName);
			} else if (allNotCheckedRules.contains(operationName)) {
				allNotCheckedDependencies.add(operationName);
			}
		}
	}

	public List<String> getFailedDependencies() {
		return this.allFailedDependencies;
	}

	public List<String> getNotCheckedDependencies() {
		return this.allNotCheckedDependencies;
	}

	public List<CounterExample> getCounterExamples() {
		return this.counterExamples;
	}

	public String getRuleName() {
		return this.ruleOperation.getName();
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
		sb.append(", Result: ").append(this.getRuleState());
		if (this.getRuleId() != null) {
			sb.append(", RuleID: " + this.getRuleId());
		}
		if (this.numberOfViolations > 0) {
			sb.append(", NumberOfViolations: " + this.numberOfViolations);
			sb.append(", Violations: " + this.counterExamples);
		}
		if (!this.allFailedDependencies.isEmpty()) {
			sb.append(", FailedDependencies: " + this.allFailedDependencies);
		}
		if (!this.allNotCheckedDependencies.isEmpty()) {
			sb.append(", NotCheckedDependencies: " + this.allNotCheckedDependencies);
		}
		sb.append("]");
		return sb.toString();
	}

	public RuleState getRuleState() {
		return this.ruleState;
	}

	public boolean hasFailed() {
		return this.ruleState == RuleState.FAIL;
	}

	public class CounterExample {
		private final int errorType;
		private final String message;

		public CounterExample(int errorType, String message) {
			this.errorType = errorType;
			this.message = message;
		}

		public String getMessage() {
			return this.message;
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
