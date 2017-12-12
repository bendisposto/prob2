package de.prob.model.brules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.be4.classicalb.core.parser.rules.*;
import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.brules.RuleResult.RESULT_ENUM;
import de.prob.statespace.State;

public class RuleResults {
	private final LinkedHashMap<String, RuleResult> ruleResultsMap = new LinkedHashMap<>();
	private final List<String> reqIds = new ArrayList<>();

	private ResultSummary summary;

	public RuleResults(RulesProject project, State state, int maxNumberOfReportedCounterExamples) {
		this(getRuleOperations(project), state, maxNumberOfReportedCounterExamples);
	}

	private static Set<RuleOperation> getRuleOperations(RulesProject project) {
		final Set<RuleOperation> result = new HashSet<>();
		for (AbstractOperation operation : project.getOperationsMap().values()) {
			if (operation instanceof RuleOperation) {
				result.add((RuleOperation) operation);
			}
		}
		return result;
	}

	public RuleResults(Set<RuleOperation> ruleOperations, State state, int maxNumberOfReportedCounterExamples) {
		final ArrayList<RuleOperation> ruleList = new ArrayList<>();
		final List<IEvalElement> evalElements = new ArrayList<>();
		for (AbstractOperation operation : ruleOperations) {
			RuleOperation rule = (RuleOperation) operation;
			ruleList.add(rule);
			ClassicalB ruleObject = new ClassicalB(rule.getName());
			evalElements.add(ruleObject);
			// get number of counter examples
			String numberOfCtsFormula = String.format("card(%s)", rule.getCounterExampleVariableName());
			ClassicalB numberOfCtsFormulaObject = new ClassicalB(numberOfCtsFormula);
			evalElements.add(numberOfCtsFormulaObject);
			// get the (restricted) set of counter examples
			String ctFormula = String.format("SORT(%s)[1..%s]", rule.getCounterExampleVariableName(),
					maxNumberOfReportedCounterExamples);
			ClassicalB counterExampleObject = new ClassicalB(ctFormula);
			evalElements.add(counterExampleObject);
		}
		List<AbstractEvalResult> evalResults = state.eval(evalElements);
		for (int i = 0; i < ruleList.size(); i++) {
			int index = i * 3;
			RuleOperation ruleOperation = ruleList.get(i);
			RuleResult ruleResult = new RuleResult(ruleOperation, evalResults.get(index), evalResults.get(index + 1),
					evalResults.get(index + 2));
			ruleResultsMap.put(ruleOperation.getName(), ruleResult);

			if (ruleResult.hasRuleId()) {
				this.reqIds.add(ruleResult.getRuleId());
			}
		}
		addNotCheckedCauses();
	}

	private void addNotCheckedCauses() {
		final Set<String> allFailingRules = new HashSet<>();
		final Set<String> allNotCheckedRules = new HashSet<>();
		final Set<RuleResult> allNotCheckedRulesObjects = new HashSet<>();
		for (RuleResult ruleResult : ruleResultsMap.values()) {
			RESULT_ENUM result = ruleResult.getResultEnum();
			if (result == RESULT_ENUM.FAIL) {
				allFailingRules.add(ruleResult.getRuleName());
			} else if (result == RESULT_ENUM.NOT_CHECKED) {
				allNotCheckedRules.add(ruleResult.getRuleName());
				allNotCheckedRulesObjects.add(ruleResult);
			}
		}
		for (RuleResult ruleResult : allNotCheckedRulesObjects) {
			ruleResult.addAdditionalInformation(allFailingRules, allNotCheckedRules);
		}
	}

	private void createSummary() {
		final int numberOfRules = ruleResultsMap.size();
		int numberOfRulesFailed = 0;
		int numberOfRulesSucceeded = 0;
		int numberOfRulesNotChecked = 0;
		int numberOfRulesDisabled = 0;
		for (RuleResult ruleResult : ruleResultsMap.values()) {
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
				throw new AssertionError();
			}
		}
		this.summary = new ResultSummary(numberOfRules, numberOfRulesFailed, numberOfRulesSucceeded,
				numberOfRulesNotChecked, numberOfRulesDisabled);
	}

	public List<RuleResult> getRuleResultList() {
		return new ArrayList<>(ruleResultsMap.values());
	}

	public Map<String, RuleResult> getRuleResultMap() {
		return new HashMap<>(this.ruleResultsMap);
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (RuleResult result : this.ruleResultsMap.values()) {
			sb.append(result.toString()).append("\n");
		}
		return sb.toString();
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
