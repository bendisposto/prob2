package de.prob.cli.integration;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static de.prob.model.brules.RuleResult.RESULT_ENUM.*;

import de.be4.classicalb.core.parser.rules.AbstractOperation;
import de.be4.classicalb.core.parser.rules.RuleOperation;
import de.be4.classicalb.core.parser.rules.RulesProject;
import de.prob.model.brules.RuleResult;
import de.prob.model.brules.RuleResult.CounterExampleResult;
import de.prob.model.brules.RuleResults;
import de.prob.model.brules.RuleResults.ResultSummary;
import de.prob.model.brules.RulesMachineRun;
import de.prob.model.brules.RulesMachineRun.ERROR_TYPES;
import de.prob.model.brules.RulesMachineRunner;

public class RulesMachineTest {

	static final String dir = "src/test/resources/brules/";

	@Test
	public void testSimpleRulesMachine() throws IOException {
		RulesMachineRun rulesMachineRun = startRulesMachineRun(dir + "SimpleRulesMachine.rmch");
		assertEquals(false, rulesMachineRun.hasError());
		assertTrue(rulesMachineRun.getErrorList().isEmpty());
		assertEquals(null, rulesMachineRun.getFirstError());

		RuleResults ruleResults = rulesMachineRun.getRuleResults();
		ResultSummary summary = ruleResults.getSummary();
		// summary is created only once
		assertEquals(summary, ruleResults.getSummary());

		assertEquals(4, ruleResults.getRuleResultList().size());
		RuleResult rule1Result = ruleResults.getRuleResult("Rule1");
		assertEquals(SUCCESS, rule1Result.getResultEnum());
		RuleOperation rule1Operation = rule1Result.getRuleOperation();
		assertEquals("Rule1", rule1Operation.getName());
		assertTrue("Should be empty", rule1Result.getNotCheckedDependencies().isEmpty());

		RuleResult result2 = ruleResults.getRuleResult("Rule2");
		assertEquals(FAIL, result2.getResultEnum());
		String message = result2.getCounterExamples().get(0).getMessage();
		assertEquals("ERROR2", message);

		assertEquals(NOT_CHECKED, ruleResults.getRuleResult("Rule3").getResultEnum());
		assertEquals("Rule2", ruleResults.getRuleResult("Rule3").getFailedDependencies().get(0));
	}

	@Test
	public void testCounterExample() {
		RulesMachineRun rulesMachineRun = startRulesMachineRunWithOperations(
				"RULE foo BODY RULE_FAIL ERROR_TYPE 1 COUNTEREXAMPLE \"error\"END END");
		assertEquals(null, rulesMachineRun.getFirstError());
		RuleResult ruleResult = rulesMachineRun.getRuleResults().getRuleResult("foo");
		List<CounterExampleResult> counterExamples = ruleResult.getCounterExamples();
		assertEquals(1, counterExamples.size());
		CounterExampleResult counterExample = counterExamples.get(0);
		assertEquals(1, counterExample.getErrorType());
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
	public void testReuseStateSpace() throws IOException {
		RulesMachineRunner.getInstance().setReuseStateSpace(true);
		RulesMachineRun rulesMachineRun = startRulesMachineRun(dir + "RulesMachineWithWDError.rmch");
		BigInteger numberAfterFirstRun = rulesMachineRun.getTotalNumberOfProBCliErrors();
		RulesMachineRun rulesMachineRun2 = startRulesMachineRun(dir + "RulesMachineWithWDError.rmch");
		BigInteger numberAfterSecondRun = rulesMachineRun2.getTotalNumberOfProBCliErrors();
		assertTrue(numberAfterSecondRun.intValue() > numberAfterFirstRun.intValue());
		RulesMachineRunner.getInstance().setReuseStateSpace(false);
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
		// assertEquals(1, rulesMachineRun.getErrorList().size());
		System.out.println(rulesMachineRun.getRuleResults());
		System.out.println(rulesMachineRun.getTotalNumberOfProBCliErrors());
		assertTrue(rulesMachineRun.getTotalNumberOfProBCliErrors().intValue() > 0);
	}

	@Test
	public void testRulesMachineFileNotFound() {
		RulesMachineRun rulesMachineRun = startRulesMachineRun(dir + "RulesMachineFileDoesNotExist123.rmch");
		assertEquals(ERROR_TYPES.PARSE_ERROR, rulesMachineRun.getFirstError().getType());
	}

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

	public static RulesMachineRun startRulesMachineRunWithOperations(String... operations) {
		try {
			File tempFile = File.createTempFile("TestMachine", ".rmch");
			String filename = tempFile.getName();
			StringBuilder sb = new StringBuilder();
			sb.append("RULES_MACHINE ").append(filename.substring(0, filename.length() - 5)).append("\n");
			sb.append("OPERATIONS\n");
			for (int i = 0; i < operations.length; i++) {
				sb.append(operations[i]);
				if (i < operations.length - 1) {
					sb.append(",\n");
				}
			}
			sb.append("\nEND");
			FileWriter fw = new FileWriter(tempFile);
			fw.write(sb.toString());
			fw.flush();
			fw.close();
			return startRulesMachineRun(tempFile.getCanonicalPath());
		} catch (IOException e) {
			throw new AssertionError(e);
		}
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
