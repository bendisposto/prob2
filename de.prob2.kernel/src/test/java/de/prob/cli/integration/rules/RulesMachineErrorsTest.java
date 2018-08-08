package de.prob.cli.integration.rules;

import java.io.File;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.model.brules.RuleStatus;
import de.prob.model.brules.RulesMachineRun;
import de.prob.model.brules.RulesMachineRun.ERROR_TYPES;

import org.junit.Test;

import static de.prob.cli.integration.rules.RulesTestUtil.createRulesMachineFile;
import static de.prob.cli.integration.rules.RulesTestUtil.startRulesMachineRun;
import static de.prob.cli.integration.rules.RulesTestUtil.startRulesMachineRunWithOperations;
import static org.junit.Assert.*;

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
				"RULE foo BODY VAR xx IN xx := 1; xx := TRUE END;RULE_FAIL COUNTEREXAMPLE \"fail\" END END");
		System.out.println(rulesMachineRun.getFirstError());
		assertEquals(ERROR_TYPES.PROB_ERROR, rulesMachineRun.getFirstError().getType());
	}

	@Test
	public void testContinueAfterError() {
		File runnerFile = createRulesMachineFile("OPERATIONS RULE Rule1 BODY VAR xx IN xx := {1|->2}(3) END;RULE_FAIL WHEN 1=2 COUNTEREXAMPLE \"fail\" END END;"
				+ "RULE Rule2 BODY VAR xx IN xx := {1|->2}(3) END;RULE_FAIL WHEN 1=2 COUNTEREXAMPLE \"fail\" END END;" + "RULE Rule3 BODY RULE_FAIL WHEN 1=2 COUNTEREXAMPLE \"fail\" END END");
		RulesMachineRun rulesMachineRun = new RulesMachineRun(runnerFile);
		rulesMachineRun.setContinueAfterErrors(true);
		rulesMachineRun.start();
		assertTrue(rulesMachineRun.getTotalNumberOfProBCliErrors().intValue() >= 2);
		assertEquals(RuleStatus.SUCCESS, rulesMachineRun.getRuleResults().getRuleResult("Rule3").getRuleState());
	}

	@Test
	public void testEnumerationError() {
		RulesMachineRun rulesMachineRun = startRulesMachineRunWithOperations(
				"RULE foo BODY RULE_FORALL x WHERE 1=1 EXPECT x = \"foo\" COUNTEREXAMPLE x END END");
		assertNotNull(rulesMachineRun.getFirstError());
		assertEquals(ERROR_TYPES.PROB_ERROR, rulesMachineRun.getFirstError().getType());
		assertTrue(rulesMachineRun.getTotalNumberOfProBCliErrors().intValue() > 0);
		assertFalse(rulesMachineRun.getErrorList().isEmpty());
	}

	@Test
	public void testRulesMachineWithWDError() {
		// @formatter:off
		RulesMachineRun rulesMachineRun = startRulesMachineRunWithOperations(
				"RULE Rule1 BODY VAR xx IN xx := {1|->2}(3) END;RULE_FAIL COUNTEREXAMPLE \"fail\" END END",
				"RULE Rule2 DEPENDS_ON_RULE Rule1 BODY RULE_FAIL COUNTEREXAMPLE \"fail\" END END"
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
		assertEquals(RuleStatus.NOT_CHECKED, rulesMachineRun.getRuleResults().getRuleResult("Rule1").getRuleState());

		// Rule2 is not checked because of the dependency to Rule1
		assertEquals(RuleStatus.NOT_CHECKED, rulesMachineRun.getRuleResults().getRuleResult("Rule2").getRuleState());
	}

	@Test
	public void testRulesMachineFileNotFound() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun("RulesMachineFileDoesNotExist123.rmch");
		assertEquals(ERROR_TYPES.PARSE_ERROR, rulesMachineRun.getFirstError().getType());
	}

}
