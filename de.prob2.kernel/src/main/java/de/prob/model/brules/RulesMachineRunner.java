package de.prob.model.brules;

import java.io.File;
import java.util.Map;

//import com.google.inject.Guice;
import com.google.inject.Inject;
//import com.google.inject.Injector;

import de.be4.classicalb.core.parser.rules.RulesProject;
import de.prob.Main;
import de.prob.cli.CliVersionNumber;
import de.prob.scripting.Api;
import de.prob.scripting.ExtractedModel;

public class RulesMachineRunner {

	private static RulesMachineRunner rulesMachineRunner; // singleton
	private final CliVersionNumber cliVersion;
	private final RulesModelFactory rulesFactory;
	private boolean reuseStateSpaceOfPreviousRun = false;

	@Inject
	public RulesMachineRunner(Api api) {
		this.cliVersion = api.getVersion();
		this.rulesFactory = Main.getInjector().getInstance(RulesModelFactory.class);
	}

	public static RulesMachineRunner getInstance() {
		if (rulesMachineRunner == null) {
			rulesMachineRunner = Main.getInjector().getInstance(RulesMachineRunner.class);
			return rulesMachineRunner;
		} else {
			return rulesMachineRunner;
		}
	}

	public CliVersionNumber getVersion() {
		return this.cliVersion;
	}

	public ExecuteRun createRulesMachineExecuteRun(RulesProject rulesProject, File mainMachineFile,
			Map<String, String> proBCorePreferences) {
		ExtractedModel<RulesModel> extract;
		extract = this.rulesFactory.extract(mainMachineFile, rulesProject);
		return new ExecuteRun(extract, proBCorePreferences, this.reuseStateSpaceOfPreviousRun);
	}

	public void setReuseStateSpace(boolean b) {
		this.reuseStateSpaceOfPreviousRun = b;
	}

}