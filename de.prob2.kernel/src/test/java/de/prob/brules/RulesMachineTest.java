package de.prob.brules;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static de.prob.model.brules.RuleResult.RESULT_ENUM.*;

import de.prob.Main;
import de.prob.cli.ProBInstanceProvider;
import de.prob.model.brules.*;
import de.prob.model.brules.RulesMachineRun.ERROR_TYPES;

@Ignore
public class RulesMachineTest {

	@BeforeClass
	public static void setup() {
		Main.getInjector().getInstance(ProBInstanceProvider.class);
	}

	@Test
	public void testSimpleRulesMachine() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun("src/test/resources/brules/SimpleRulesMachine.rmch");

		RuleResults ruleResults = rulesMachineRun.getRuleResults();
		assertEquals(3, ruleResults.getRuleResultList().size());

		assertEquals(SUCCESS, ruleResults.getRuleResult("Rule1").getResultEnum());

		RuleResult result2 = ruleResults.getRuleResult("Rule2");
		assertEquals(FAIL, result2.getResultEnum());
		String message = result2.getCounterExamples().get(0).getMessage();
		assertEquals("ERROR2", message);

		assertEquals(NOT_CHECKED, ruleResults.getRuleResult("Rule3").getResultEnum());
		assertEquals("Rule2", ruleResults.getRuleResult("Rule3").getNotCheckedCauses().get(0));
	}

	@Test
	public void testRulesMachineWithParseError() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun(
				"src/test/resources/brules/RulesMachineWithParseError.rmch");
		System.out.println(rulesMachineRun.getError().getMessage());
		assertEquals(ERROR_TYPES.PARSE_ERROR, rulesMachineRun.getError().getType());
	}

	@Test
	public void testRulesMachineWithTypeError() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun(
				"src/test/resources/brules/RulesMachineWithTypeError.rmch");
		System.out.println(rulesMachineRun.getError().getMessage());
		assertEquals(ERROR_TYPES.PROB_ERROR, rulesMachineRun.getError().getType());
	}

	@Test
	public void testRulesMachineWithWDError() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun(
				"src/test/resources/brules/RulesMachineWithWDError.rmch");
		assertEquals(ERROR_TYPES.PROB_ERROR, rulesMachineRun.getError().getType());
	}

	@Test
	public void testRulesMachineFileNotFound() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun(
				"src/test/resources/brules/RulesMachineFileDoesNotExist123.rmch");
		assertEquals(ERROR_TYPES.PARSE_ERROR, rulesMachineRun.getError().getType());
	}

	private RulesMachineRun startRulesMachineRun(String string) {
		File f = new File(string);
		RulesMachineRun rulesMachineRun = new RulesMachineRun(f);
		rulesMachineRun.start();
		return rulesMachineRun;
	}
}
