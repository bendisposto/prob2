package de.prob.model.brules;

import java.util.ArrayList;
import java.util.Collections;
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
	private final List<RuleResult> generalRules = new ArrayList<>();
	private final List<RuleResult> specificRules = new ArrayList<>();

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
				specificRules.add(ruleResult);
				this.reqIds.add(ruleResult.getRuleId());
			} else {
				generalRules.add(ruleResult);
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
				continue;
			case SUCCESS:
				numberOfRulesSucceeded++;
				continue;
			case NOT_CHECKED:
				numberOfRulesNotChecked++;
				continue;
			case DISABLED:
				numberOfRulesDisabled++;
				continue;
			default:
				throw new IllegalStateException();
			}
		}
		this.summary = new ResultSummary(numberOfRules, numberOfRulesFailed, numberOfRulesSucceeded,
				numberOfRulesNotChecked, numberOfRulesDisabled, 0);// TODO
	}

	public List<RuleResult> getGeneralRules() {
		return this.generalRules;
	}

	public List<RuleResult> getFailingGeneralRules() {
		List<RuleResult> failingRules = new ArrayList<>();
		for (RuleResult ruleResult : this.generalRules) {
			RESULT_ENUM result = ruleResult.getResultEnum();
			if (result == RESULT_ENUM.FAIL) {
				failingRules.add(ruleResult);
			}
		}
		return failingRules;
	}

	public List<RuleResult> getSpecificRules() {
		return this.specificRules;
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

	public List<RuleResult> getSortedReportRules() {
		List<RuleResult> list = new ArrayList<>();
		for (RuleResult ruleResult : specificRules) {
			if (ruleResult.getResultEnum() != RESULT_ENUM.DISABLED) {
				list.add(ruleResult);
			}
		}
		list.addAll(this.getFailingGeneralRules());
		Collections.sort(list);
		return list;
	}

	public class ResultSummary {
		public final int numberOfRules;
		public final int numberOfRulesFailed;
		public final int numberOfRulesSucceeded;
		public final int numberOfRulesNotChecked;
		public final int numberOfRulesDisabled;
		public final int numberOfRulesNotImplemented;

		protected ResultSummary(int numberOfRules, int numberOfRulesFailed, int numberOfRulesSucceeded,
				int numberOfRulesNotChecked, int numberOfRulesDisabled, int numberOfRulesNotImplemented) {
			this.numberOfRules = numberOfRules;
			this.numberOfRulesFailed = numberOfRulesFailed;
			this.numberOfRulesSucceeded = numberOfRulesSucceeded;
			this.numberOfRulesNotChecked = numberOfRulesNotChecked;
			this.numberOfRulesDisabled = numberOfRulesDisabled;
			this.numberOfRulesNotImplemented = numberOfRulesNotImplemented;
		}
	}
}
