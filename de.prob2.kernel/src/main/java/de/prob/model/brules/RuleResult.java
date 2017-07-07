package de.prob.model.brules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

public class RuleResult implements Comparable<RuleResult> {
	private final RuleOperation ruleOperation;
	private final AbstractEvalResult ruleResult;
	// private final AbstractEvalResult counterExampleResult;
	private final List<CounterExampleResult> counterExamples = new ArrayList<>();
	// causes leading to NOT_CHECKED result
	private final ArrayList<String> notCheckedCauses = new ArrayList<>();

	public static enum RESULT_ENUM {
		FAIL, SUCCESS, NOT_CHECKED, DISABLED
	}

	public RuleResult(RuleOperation rule, AbstractEvalResult result, AbstractEvalResult counterExampleResult) {
		this.ruleOperation = rule;
		this.ruleResult = result;
		// this.counterExampleResult = counterExampleResult;

		transformCounterExamples(counterExampleResult);
	}

	public RuleOperation getRuleOperation() {
		return this.ruleOperation;
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
					throw new IllegalStateException();
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

	public void setNotCheckedCauses(HashSet<String> failingRules) {
		Set<AbstractOperation> transitiveDependencies = ruleOperation.getTransitiveDependencies();
		for (AbstractOperation abstractOperation : transitiveDependencies) {
			String operationName = abstractOperation.getName();
			if (failingRules.contains(operationName)) {
				this.notCheckedCauses.add(operationName);
			}
		}
	}

	public List<String> getNotCheckedCauses() {
		return this.notCheckedCauses;
	}

	public List<CounterExampleResult> getCounterExamples() {
		return this.counterExamples;
	}

	public String getRuleName() {
		return this.ruleOperation.getName();
	}

	public String getResultValue() {
		String res = ruleResult.toString();
		res = res.substring(1, res.length() - 1);
		return res;
	}

	public boolean hasRuleId() {
		if (ruleOperation.getRuleIdString() == null) {
			return false;
		} else {
			return true;
		}
	}

	public String getRuleId() {
		return ruleOperation.getRuleIdString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Name: ").append(this.getRuleName());
		if (this.getRuleId() != null) {
			sb.append(", RuleID: " + this.getRuleId());
		}
		sb.append(", Result: ").append(this.getResultValue());
		sb.append("]");
		return sb.toString();
	}

	public RESULT_ENUM getResultEnum() {
		switch (this.getResultValue()) {
		case RulesTransformation.RULE_FAIL:
			return RESULT_ENUM.FAIL;
		case RulesTransformation.RULE_SUCCESS:
			return RESULT_ENUM.SUCCESS;
		case RulesTransformation.RULE_NOT_CHECKED:
			return RESULT_ENUM.NOT_CHECKED;
		case RulesTransformation.RULE_DISABLED:
			return RESULT_ENUM.DISABLED;
		default:
			throw new IllegalStateException();
		}
	}

	public boolean hasFailed() {
		return getResultEnum() == RESULT_ENUM.FAIL;
	}

	public int getCompareValue() {
		switch (this.getResultEnum()) {
		case FAIL:
			return 0;
		case NOT_CHECKED:
			return 1;
		case SUCCESS:
			return 2;
		case DISABLED:
			return 3;
		default:
			throw new IllegalStateException();
		}
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
			return "" + errorType;
		}

		public Integer getErrorType() {
			return this.errorType;
		}
	}

	@Override
	public int compareTo(RuleResult o) {
		Integer a = this.getCompareValue();
		Integer b = o.getCompareValue();
		int compareTo = a.compareTo(b);
		if (compareTo == 0) {
			if (!this.hasRuleId() || !o.hasRuleId()) {
				return compareTo;
			} else {
				return this.getRuleId().compareTo(o.getRuleId());
			}
		}
		return compareTo;
	}
}
