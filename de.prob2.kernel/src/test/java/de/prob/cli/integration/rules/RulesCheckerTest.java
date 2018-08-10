package de.prob.cli.integration.rules;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.prob.Main;
import de.prob.model.brules.RuleStatus;
import de.prob.model.brules.RulesChecker;
import de.prob.scripting.Api;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RulesCheckerTest {

	private Api api;

	@Before
	public void setupClass() {
		api = Main.getInjector().getInstance(Api.class);
	}

	private static final Path DIR = Paths.get("src", "test", "resources", "brules");

	@Test
	public void testExecuteAllOperations() throws IOException {
		StateSpace s = api.brules_load(DIR.resolve("RulesMachineExample.rmch").toString());
		Trace trace = new Trace(s);
		RulesChecker rulesChecker = new RulesChecker(trace);
		rulesChecker.init();
		rulesChecker.executeAllOperations();
		rulesChecker.getOperationStates().forEach((key, state) -> {
			if ("RULE_BasedOnRuleWithViolations".equals(key.getName())) {
				assertEquals(RuleStatus.NOT_CHECKED, state);
			} else {
				assertTrue(state.isExecuted());
			}
		});
	}

	@Test
	public void testExecuteOperation() throws IOException {
		StateSpace s = api.brules_load(DIR.resolve("RulesMachineExample.rmch").toString());
		RulesChecker checker = new RulesChecker(new Trace(s));
		boolean possible = checker.executeOperationAndDependencies("RULE_BasedOnValue1");
		assertTrue(possible);
		assertEquals(RuleStatus.FAIL, checker.getOperationState("RULE_BasedOnValue1"));
		assertEquals(RuleStatus.NOT_CHECKED, checker.getOperationState("RULE_WithViolations"));
		checker.executeOperationAndDependencies("RULE_WithViolations");
		assertEquals(RuleStatus.FAIL, checker.getOperationState("RULE_WithViolations"));
		boolean possible2 = checker.executeOperationAndDependencies("RULE_BasedOnRuleWithViolations");
		assertFalse(possible2);
		assertEquals(RuleStatus.NOT_CHECKED, checker.getOperationState("RULE_BasedOnRuleWithViolations"));
	}
}
