package de.prob.cli.integration.rules;

import static de.prob.model.brules.RuleResult.RESULT_ENUM.FAIL;
import static de.prob.model.brules.RuleResult.RESULT_ENUM.NOT_CHECKED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.be4.classicalb.core.parser.rules.AbstractOperation;
import de.be4.classicalb.core.parser.rules.RuleOperation;
import de.be4.classicalb.core.parser.rules.RulesProject;
import de.prob.model.brules.RuleResult;
import de.prob.model.brules.RuleResults;
import de.prob.model.brules.RulesMachineRun;

public class RulesTestUtil {

	public static RulesMachineRun startRulesMachineRun(String file) {
		return startRulesMachineRun(new File(file));
	}

	public static RulesMachineRun startRulesMachineRun(File file) {
		RulesMachineRun rulesMachineRun = new RulesMachineRun(file);
		rulesMachineRun.start();
		if (!rulesMachineRun.hasError()) {
			checkRulesMachineRunForConsistency(rulesMachineRun);
		}
		return rulesMachineRun;
	}

	public static RulesMachineRun startRulesMachineRunWithOperations(String... operations) {
		File machineFile = createRulesMachineFileContainingOperations(operations);
		RulesMachineRun rulesMachineRun = startRulesMachineRun(machineFile.getAbsolutePath());
		assertEquals(machineFile, rulesMachineRun.getRunnerFile());
		return rulesMachineRun;
	}

	public static File createRulesMachineFileContainingOperations(String... operations) {
		StringBuilder sb = new StringBuilder();
		sb.append("OPERATIONS\n");
		for (int i = 0; i < operations.length; i++) {
			sb.append(operations[i]);
			if (i < operations.length - 1) {
				sb.append(";\n");
			}
		}
		return createRulesMachineFile(sb.toString());
	}

	public static File createRulesMachineFile(String machineBody) {
		try {
			File tempFile = File.createTempFile("TestMachine", ".rmch");
			String filename = tempFile.getName();
			StringBuilder sb = new StringBuilder();
			sb.append("RULES_MACHINE ").append(filename.substring(0, filename.length() - 5)).append("\n");
			sb.append(machineBody);
			sb.append("\nEND");
			FileWriter fw = new FileWriter(tempFile);
			System.out.println(sb.toString());
			fw.write(sb.toString());
			fw.flush();
			fw.close();
			return tempFile;
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
