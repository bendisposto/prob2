package de.prob.model.brules;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import de.be4.classicalb.core.parser.rules.project.RulesProject;
import de.prob.cli.CliVersionNumber;
import de.prob.scripting.Api;
import de.prob.scripting.ExtractedModel;

public class RulesMachineRunner {

	public static Injector INJECTOR = Guice.createInjector(com.google.inject.Stage.PRODUCTION, new RulesMachineGuiceConfig());
	private static RulesMachineRunner prob2Runner; // singleton
	private final CliVersionNumber cliVersion;
	private final RulesModelFactory rulesFactory;

	@Inject
	public RulesMachineRunner(Api api) {
		this.cliVersion = api.getVersion();
		this.rulesFactory = INJECTOR.getInstance(RulesModelFactory.class);
	}

	public static RulesMachineRunner getInstance() {
		if (prob2Runner == null) {
			prob2Runner = RulesMachineRunner.INJECTOR.getInstance(RulesMachineRunner.class);
			return prob2Runner;
		} else {
			return prob2Runner;
		}
	}

	public CliVersionNumber getVersion() {
		return this.cliVersion;
	}

	public ExecuteRun createRulesMachineExecuteRun(RulesProject rulesProject, File mainMachineFile) {
		String probHome = System.getProperty("prob.home");
		if (probHome != null) {
			//debugPrint("using prob.home: " + System.getProperty("prob.home"));
		}
		//debugPrint("ProB version: " + cliVersion);

		final Map<String, String> prefs = new HashMap<>();
		prefs.put("TIME_OUT", "500000");
		prefs.put("TRY_FIND_ABORT", "TRUE");
		prefs.put("CLPFD", "FALSE");
		// prefs.put("MAX_OPERATIONS", "0");
		// prefs.put("COMPRESSION", "TRUE");
		// prefs.put("IGNORE_HASH_COLLISIONS", "TRUE");
		// prefs.put("FORGET_STATE_SPACE", "TRUE");

		ExtractedModel<RulesModel> extract;
		extract = this.rulesFactory.extract(mainMachineFile, rulesProject);
		return new ExecuteRun(extract, prefs);

	}

}