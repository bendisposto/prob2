package de.prob.cli.integration.rules;

import static de.prob.cli.integration.rules.RulesTestUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.prob.model.brules.RulesMachineRun;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.model.brules.RuleResult.RESULT_ENUM;
import de.prob.model.brules.RulesMachineRun.ERROR_TYPES;

public class RulesMachineErrorsTest {

	@Test
	public void testRulesMachineWithParseError() {
		RulesMachineRun rulesMachineRun = startRulesMachineRunWithOperations("RULE foo BODY ;; END");
		System.out.println(rulesMachineRun.getFirstError().getMessage());
		assertEquals(ERROR_TYPES.PARSE_ERROR, rulesMachineRun.getFirstError().getType());
		assertTrue(rulesMachineRun.getFirstError().getException() instanceof BException);
	}

	@Test
	public void testRulesMachineWithTypeError() {
		RulesMachineRun rulesMachineRun = startRulesMachineRunWithOperations(
				"RULE foo BODY VAR xx IN xx := 1; xx := TRUE END END");
		System.out.println(rulesMachineRun.getFirstError());
		assertEquals(ERROR_TYPES.PROB_ERROR, rulesMachineRun.getFirstError().getType());
	}

	@Test
	public void testContinueAfterError() {
		File runnerFile = createRulesMachineFile("OPERATIONS RULE Rule1 BODY VAR xx IN xx := {1|->2}(3) END END;"
				+ "RULE Rule2 BODY VAR xx IN xx := {1|->2}(3) END END;"
				+ "RULE Rule3 BODY skip END"
				);
		RulesMachineRun rulesMachineRun = new RulesMachineRun(runnerFile);
		rulesMachineRun.setContinueAfterErrors(true);
		rulesMachineRun.start();
		assertTrue(rulesMachineRun.getTotalNumberOfProBCliErrors().intValue() >= 2);
		assertEquals(RESULT_ENUM.SUCCESS, rulesMachineRun.getRuleResults().getRuleResult("Rule3").getResultEnum());
	}

	@Test
	public void testEnumerationError() {
		RulesMachineRun rulesMachineRun = startRulesMachineRunWithOperations(
				"RULE foo BODY RULE_FORALL x WHERE 1=1 EXPECT x = \"foo\" COUNTEREXAMPLE x END END");
		assertTrue(null != rulesMachineRun.getFirstError());
		assertEquals(ERROR_TYPES.PROB_ERROR, rulesMachineRun.getFirstError().getType());
		assertTrue(rulesMachineRun.getTotalNumberOfProBCliErrors().intValue() > 0);
		assertTrue(rulesMachineRun.getErrorList().size() > 0);
	}

	@Test
	public void testRulesMachineWithWDError() {
		// @formatter:off
		RulesMachineRun rulesMachineRun = startRulesMachineRunWithOperations(
				"RULE Rule1 BODY VAR xx IN xx := {1|->2}(3) END END",
				"RULE Rule2 DEPENDS_ON_RULE Rule1 BODY skip END"
		);
		System.out.println(rulesMachineRun.getFirstError());
		// @formatter:on
		assertTrue(rulesMachineRun.hasError());
		int numberofStatesExecuted = rulesMachineRun.getExecuteRun().getExecuteModelCommand()
				.getNumberofStatesExecuted();
		assertEquals(1, numberofStatesExecuted);
		assertEquals(ERROR_TYPES.PROB_ERROR, rulesMachineRun.getFirstError().getType());
		assertTrue(rulesMachineRun.getTotalNumberOfProBCliErrors().intValue() > 0);

		System.out.println(rulesMachineRun.getRuleResults());

		// Rule1 is not checked because of the WD Error
		assertEquals(RESULT_ENUM.NOT_CHECKED, rulesMachineRun.getRuleResults().getRuleResult("Rule1").getResultEnum());

		// Rule2 is not checked because of the dependency to Rule1
		assertEquals(RESULT_ENUM.NOT_CHECKED, rulesMachineRun.getRuleResults().getRuleResult("Rule2").getResultEnum());
	}

	@Test
	public void testRulesMachineFileNotFound() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun("RulesMachineFileDoesNotExist123.rmch");
		assertEquals(ERROR_TYPES.PARSE_ERROR, rulesMachineRun.getFirstError().getType());
	}

}
