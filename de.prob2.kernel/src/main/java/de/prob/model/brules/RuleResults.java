package de.prob.model.brules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.be4.classicalb.core.parser.rules.*;
import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.brules.RuleResult.RESULT_ENUM;
import de.prob.statespace.State;

public class RuleResults {
	private final LinkedHashMap<String, RuleResult> ruleResultsMap = new LinkedHashMap<>();
	private final List<RuleResult> ruleResultList = new ArrayList<>();
	private final List<String> reqIds = new ArrayList<>();

	private ResultSummary summary;

	public RuleResults(RulesProject project, State state) {
		final Map<String, AbstractOperation> operationsMap = project.getOperationsMap();
		final ArrayList<RuleOperation> ruleList = new ArrayList<>();
		final List<IEvalElement> evalElements = new ArrayList<>();
		for (AbstractOperation operation : operationsMap.values()) {
			if (operation instanceof RuleOperation) {
				RuleOperation rule = (RuleOperation) operation;
				ruleList.add(rule);
				ClassicalB ruleObject = new ClassicalB(rule.getName());
				evalElements.add(ruleObject);
				ClassicalB counterExampleObject = new ClassicalB(rule.getCounterExampleVariableName());
				evalElements.add(counterExampleObject);
			}
		}
		List<AbstractEvalResult> evalResults = state.eval(evalElements);
		for (int i = 0; i < ruleList.size(); i++) {
			int index = i * 2;
			RuleOperation ruleOperation = ruleList.get(i);
			RuleResult ruleResult = new RuleResult(ruleOperation, evalResults.get(index), evalResults.get(index + 1));
			ruleResultsMap.put(ruleOperation.getName(), ruleResult);

			ruleResultList.add(ruleResult);
			if (ruleResult.hasRuleId()) {
				this.reqIds.add(ruleResult.getRuleId());
			}
		}
		addNotCheckedCauses();
	}

	private void addNotCheckedCauses() {
		final HashSet<String> failingRules = new HashSet<>();
		for (RuleResult ruleResult : ruleResultList) {
			RESULT_ENUM result = ruleResult.getResultEnum();
			if (result == RESULT_ENUM.FAIL) {
				failingRules.add(ruleResult.getRuleName());
			}
		}
		for (RuleResult ruleResult : ruleResultList) {
			RESULT_ENUM result = ruleResult.getResultEnum();
			if (result == RESULT_ENUM.NOT_CHECKED) {
				ruleResult.setNotCheckedCauses(failingRules);
			}
		}
	}

	private void createSummary() {
		final int numberOfRules = ruleResultList.size();
		int numberOfRulesFailed = 0;
		int numberOfRulesSucceeded = 0;
		int numberOfRulesNotChecked = 0;
		int numberOfRulesDisabled = 0;
		for (RuleResult ruleResult : ruleResultList) {
			RESULT_ENUM resultEnum = ruleResult.getResultEnum();
			switch (resultEnum) {
			case FAIL:
				numberOfRulesFailed++;
				break;
			case SUCCESS:
				numberOfRulesSucceeded++;
				break;
			case NOT_CHECKED:
				numberOfRulesNotChecked++;
				break;
			case DISABLED:
				numberOfRulesDisabled++;
				break;
			default:
				throw new IllegalStateException();
			}
		}
		this.summary = new ResultSummary(numberOfRules, numberOfRulesFailed, numberOfRulesSucceeded,
				numberOfRulesNotChecked, numberOfRulesDisabled);
	}

	public List<RuleResult> getRuleResultList() {
		return this.ruleResultList;
	}

	public ResultSummary getSummary() {
		if (this.summary == null) {
			createSummary();
		}
		return this.summary;
	}

	public RuleResult getRuleResult(final String ruleName) {
		return this.ruleResultsMap.get(ruleName);
	}

	public class ResultSummary {
		public final int numberOfRules;
		public final int numberOfRulesFailed;
		public final int numberOfRulesSucceeded;
		public final int numberOfRulesNotChecked;
		public final int numberOfRulesDisabled;

		protected ResultSummary(int numberOfRules, int numberOfRulesFailed, int numberOfRulesSucceeded,
				int numberOfRulesNotChecked, int numberOfRulesDisabled) {
			this.numberOfRules = numberOfRules;
			this.numberOfRulesFailed = numberOfRulesFailed;
			this.numberOfRulesSucceeded = numberOfRulesSucceeded;
			this.numberOfRulesNotChecked = numberOfRulesNotChecked;
			this.numberOfRulesDisabled = numberOfRulesDisabled;
		}
	}
}
