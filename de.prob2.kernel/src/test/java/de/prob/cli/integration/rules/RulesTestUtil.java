package de.prob.cli.integration.rules;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.be4.classicalb.core.parser.rules.AbstractOperation;
import de.be4.classicalb.core.parser.rules.RuleOperation;
import de.be4.classicalb.core.parser.rules.RulesProject;
import de.prob.model.brules.RuleResult;
import de.prob.model.brules.RuleResults;
import de.prob.model.brules.RuleStatus;
import de.prob.model.brules.RulesMachineRun;

import static org.junit.Assert.*;

public final class RulesTestUtil {
	private RulesTestUtil() {
		throw new AssertionError("Utility class");
	}

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
		return createRulesMachineFile(Arrays.stream(operations)
			.collect(Collectors.joining(";", "OPERATIONS\n", "")));
	}

	public static File createRulesMachineFile(String machineBody) {
		try {
			File tempFile = File.createTempFile("TestMachine", ".rmch");
			String filename = tempFile.getName();
			StringBuilder sb = new StringBuilder();
			sb.append("RULES_MACHINE ");
			sb.append(filename, 0, filename.length() - 5);
			sb.append('\n');
			sb.append(machineBody);
			sb.append("\nEND");
			System.out.println(sb);
			try (
				FileOutputStream fos = new FileOutputStream(tempFile);
				OutputStreamWriter osw = new OutputStreamWriter(fos);
			) {
				osw.write(sb.toString());
			}
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
				if (ruleResult.getRuleState() == RuleStatus.FAIL) {
					assertTrue(String.format("No violation found but rule failed: '%s'", ruleName),
							ruleResult.getNumberOfViolations() > 0);
				}

				if (ruleResult.getRuleState() == RuleStatus.NOT_CHECKED) {
					List<String> notCheckedCauses = ruleResult.getFailedDependencies();
					assertTrue(String.format("There is no cause why rule '%s' is not checked.", ruleName),
							!notCheckedCauses.isEmpty());
				}
			}

		}
	}
}
