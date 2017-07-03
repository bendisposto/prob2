package de.prob.model.classicalb;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import static de.prob.model.brules.RuleResult.RESULT_ENUM.*;

import de.prob.Main;
import de.prob.cli.ProBInstanceProvider;
import de.prob.cli.OsSpecificInfo;
import de.prob.model.brules.RuleResult;
import de.prob.model.brules.RuleResults;
import de.prob.model.brules.RulesMachineRun;
import de.prob.model.brules.RulesMachineRun.ERROR_TYPES;
import de.prob.scripting.Installer;

public class RulesMachineTest {

	@BeforeClass
	public static void setup() {
		ProBInstanceProvider provider = Main.getInjector().getInstance(ProBInstanceProvider.class);
		OsSpecificInfo osInfo = provider.getOsInfo();
		new Installer(osInfo).ensureCLIsInstalled();
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
