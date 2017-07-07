package de.prob.cli.integration;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import static de.prob.model.brules.RuleResult.RESULT_ENUM.*;

import de.be4.classicalb.core.parser.rules.AbstractOperation;
import de.be4.classicalb.core.parser.rules.RuleOperation;
import de.be4.classicalb.core.parser.rules.RulesProject;
import de.prob.model.brules.RuleResult;
import de.prob.model.brules.RuleResults;
import de.prob.model.brules.RulesMachineRun;
import de.prob.model.brules.RulesMachineRun.ERROR_TYPES;

public class RulesMachineTest {

	static final String dir = "src/test/resources/brules/";

	@Test
	public void testSimpleRulesMachine() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun(dir + "SimpleRulesMachine.rmch");

		RuleResults ruleResults = rulesMachineRun.getRuleResults();
		assertEquals(3, ruleResults.getRuleResultList().size());

		assertEquals(SUCCESS, ruleResults.getRuleResult("Rule1").getResultEnum());

		RuleResult result2 = ruleResults.getRuleResult("Rule2");
		assertEquals(FAIL, result2.getResultEnum());
		String message = result2.getCounterExamples().get(0).getMessage();
		assertEquals("ERROR2", message);

		assertEquals(NOT_CHECKED, ruleResults.getRuleResult("Rule3").getResultEnum());
		assertEquals("Rule2", ruleResults.getRuleResult("Rule3").getFailedDependencies().get(0));
	}

	@Test
	public void testRulesMachineExample() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun(dir + "RulesMachineExample.rmch");
		assertEquals(false, rulesMachineRun.hasError());
		System.out.println(rulesMachineRun.getRuleResults());
	}

	@Test
	public void testRulesMachineWithParseError() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun(dir + "RulesMachineWithParseError.rmch");
		System.out.println(rulesMachineRun.getFirstError().getMessage());
		assertEquals(ERROR_TYPES.PARSE_ERROR, rulesMachineRun.getFirstError().getType());
	}

	@Test
	public void testRulesMachineWithTypeError() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun(dir + "RulesMachineWithTypeError.rmch");
		// System.out.println(rulesMachineRun.getError().getMessage());
		assertEquals(ERROR_TYPES.PROB_ERROR, rulesMachineRun.getFirstError().getType());
	}

	@Test
	public void testRulesMachineWithWDError() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun(dir + "RulesMachineWithWDError.rmch");
		assertEquals(ERROR_TYPES.PROB_ERROR, rulesMachineRun.getFirstError().getType());
		System.out.println(rulesMachineRun.getRuleResults());
	}

	@Test
	public void testRulesMachineFileNotFound() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun(dir + "RulesMachineFileDoesNotExist123.rmch");
		assertEquals(ERROR_TYPES.PARSE_ERROR, rulesMachineRun.getFirstError().getType());
	}

	@Ignore // requires that ProB core can handle timeouts defined in the
			// machine for execute_all command
	@Test
	public void testRulesMachineWithTimeout() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun(dir + "RulesMachineWithTimeout.rmch");
		System.out.println(rulesMachineRun.hasError());
	}

	@Ignore // requires new Parser version
	@Test
	public void testReplaces() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun(dir + "Replaces.rmch");
		RuleResult ruleResult = rulesMachineRun.getRuleResults().getRuleResult("RULE_BasedOnValue1");
		assertTrue(ruleResult.hasFailed());
	}

	public static RulesMachineRun startRulesMachineRun(String file) {
		File f = new File(file);
		RulesMachineRun rulesMachineRun = new RulesMachineRun(f);
		rulesMachineRun.start();
		if (!rulesMachineRun.hasError()) {
			checkRulesMachineRunForConsistency(rulesMachineRun);
		}
		return rulesMachineRun;
	}

	public static void checkRulesMachineRunForConsistency(RulesMachineRun rulesMachineRun) {
		RulesProject rulesProject = rulesMachineRun.getRulesProject();
		RuleResults ruleResults = rulesMachineRun.getRuleResults();
		Map<String, RuleResult> ruleResultMap = ruleResults.getRuleResultMap();
		for (AbstractOperation abstractOperation : rulesProject.getOperationsMap().values()) {
			if (abstractOperation instanceof RuleOperation) {
				String ruleName = abstractOperation.getName();
				assertTrue(String.format("Rule operation '%s' is not contained in the result map.", ruleName),
						ruleResultMap.containsKey(ruleName));
				RuleResult ruleResult = ruleResultMap.get(ruleName);
				if (ruleResult.getResultEnum() == FAIL) {
					assertTrue(String.format("No violation found but rule failed: '%s'", ruleName),
							ruleResult.getNumberOfViolations() > 0);
				}

				if (ruleResult.getResultEnum() == NOT_CHECKED) {
					List<String> notCheckedCauses = ruleResult.getFailedDependencies();
					assertTrue(String.format("There is no cause why rule '%s' is not checked.", ruleName),
							!notCheckedCauses.isEmpty());
				}
			}

		}

	}
}
