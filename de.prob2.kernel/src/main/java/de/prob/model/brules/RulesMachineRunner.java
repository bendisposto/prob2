package de.prob.model.brules;

import java.io.File;
import java.util.Map;

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.rules.RulesProject;
import de.prob.Main;
import de.prob.cli.CliVersionNumber;
import de.prob.scripting.Api;
import de.prob.scripting.ExtractedModel;
import de.prob.statespace.StateSpace;

public class RulesMachineRunner {

	private static RulesMachineRunner rulesMachineRunner; // singleton
	private final CliVersionNumber cliVersion;
	private final RulesModelFactory rulesFactory;

	@Inject
	public RulesMachineRunner(Api api) {
		this.cliVersion = api.getVersion();
		this.rulesFactory = Main.getInjector().getInstance(RulesModelFactory.class);
	}

	public static RulesMachineRunner getInstance() {
		if (rulesMachineRunner == null) {
			rulesMachineRunner = Main.getInjector().getInstance(RulesMachineRunner.class);
		}
		return rulesMachineRunner;
	}

	public CliVersionNumber getVersion() {
		return this.cliVersion;
	}

	public ExecuteRun createRulesMachineExecuteRun(RulesProject rulesProject, File mainMachineFile,
			Map<String, String> proBCorePreferences, boolean continueAfterErrors, StateSpace stateSpace) {
		ExtractedModel<RulesModel> extract = this.rulesFactory.extract(mainMachineFile, rulesProject);
		return new ExecuteRun(extract, proBCorePreferences, continueAfterErrors, stateSpace);
	}

}