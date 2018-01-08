package de.prob.cli.integration.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import de.be4.classicalb.core.parser.rules.AbstractOperation;
import de.prob.Main;
import de.prob.model.brules.OperationState;
import de.prob.model.brules.RuleState;
import de.prob.model.brules.RulesChecker;
import de.prob.scripting.Api;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class RulesCheckerTest {

	private Api api;

	@Before
	public void setupClass() {
		api = Main.getInjector().getInstance(Api.class);
	}

	static final String dir = "src/test/resources/brules/";

	@Test
	public void testExecuteAllOperations() throws IOException {
		StateSpace s = api.brules_load(dir + "RulesMachineExample.rmch");
		Trace trace = new Trace(s);
		RulesChecker rulesChecker = new RulesChecker(trace);
		rulesChecker.init();
		rulesChecker.executeAllOperations();
		for (Entry<AbstractOperation, OperationState> entry : rulesChecker.getOperationStates().entrySet()) {
			OperationState state = entry.getValue();
			if (entry.getKey().getName().equals("RULE_BasedOnRuleWithViolations")) {
				assertEquals(RuleState.NOT_CHECKED, state);
			} else {
				assertTrue(state.isExecuted());
			}
		}
	}

	@Test
	public void testExecuteOperation() throws IOException {
		StateSpace s = api.brules_load(dir + "RulesMachineExample.rmch");
		RulesChecker checker = new RulesChecker(new Trace(s));
		boolean possible = checker.executeOperationAndDependencies("RULE_BasedOnValue1");
		assertEquals(true, possible);
		assertEquals(RuleState.FAIL, checker.getOperationState("RULE_BasedOnValue1"));
		assertEquals(RuleState.NOT_CHECKED, checker.getOperationState("RULE_WithViolations"));
		checker.executeOperationAndDependencies("RULE_WithViolations");
		assertEquals(RuleState.FAIL, checker.getOperationState("RULE_WithViolations"));
		boolean possible2 = checker.executeOperationAndDependencies("RULE_BasedOnRuleWithViolations");
		assertEquals(false, possible2);
		assertEquals(RuleState.NOT_CHECKED, checker.getOperationState("RULE_BasedOnRuleWithViolations"));
	}
}
