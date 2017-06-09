package de.prob.model.brules;

import static de.prob.util.DebugPrinter.debugPrint;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.be4.classicalb.core.parser.ParsingBehaviour;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.rules.RulesProject;
import de.prob.animator.domainobjects.StateError;
import de.prob.exception.ProBError;
import de.prob.statespace.State;
import de.prob.util.StopWatch;

public class RulesMachineRun {

	public enum ERROR_TYPES {
		PARSE_ERROR, PROB_ERROR, UNEXPECTED_ERROR
	}

	private final RulesMachineRunner rulesMachineRunner = RulesMachineRunner.getInstance();

	private RulesProject rulesProject;
	private ExecuteRun executeRun;

	private Error error;

	private final File runnerFile;
	private final Map<String, String> constantValuesToBeInjected;
	private RuleResults rulesResult;
	private final Map<String, String> proBCorePreferences;

	public RulesMachineRun(File runner, Map<String, String> prefs) {
		this(runner, prefs, new HashMap<String, String>());
	}

	public RulesMachineRun(File runner, Map<String, String> prefs, Map<String, String> constantValuesToBeInjected) {
		this.runnerFile = runner;

		this.proBCorePreferences = new HashMap<>();
		if (prefs != null) {
			this.proBCorePreferences.putAll(prefs);
		}
		// add mandatory preferences
		this.proBCorePreferences.put("TRY_FIND_ABORT", "TRUE");
		this.proBCorePreferences.put("CLPFD", "FALSE");
		// prefs.put("MAX_OPERATIONS", "0");
		// prefs.put("COMPRESSION", "TRUE");
		// prefs.put("IGNORE_HASH_COLLISIONS", "TRUE");
		// prefs.put("FORGET_STATE_SPACE", "TRUE");

		this.constantValuesToBeInjected = constantValuesToBeInjected;
	}

	public void setProBCorePreferences(Map<String, String> prefs) {
		proBCorePreferences.putAll(prefs);
	}

	public void start() {
		debugPrint("------- Starting RulesMachine Run: " + this.runnerFile.getAbsolutePath());
		StopWatch.start("parsing");
		boolean hasParseErrors = parseAndTranslateRulesProject();
		debugPrint(StopWatch.getRunTimeAsString("parsing"));
		if (hasParseErrors) {
			return;
		}

		this.executeRun = rulesMachineRunner.createRulesMachineExecuteRun(this.rulesProject, runnerFile,
				this.proBCorePreferences);

		try {
			StopWatch.start("prob2Run");
			debugPrint("Start execute ...");

			// start
			this.executeRun.start();

			debugPrint("End execute.");
			debugPrint(StopWatch.getRunTimeAsString("prob2Run"));
		} catch (ProBError e) {
			debugPrint("****ProBError: " + e.getMessage());
			if (executeRun.getExecuteModelCommand() != null) {
				State finalState = executeRun.getExecuteModelCommand().getFinalState();
				Collection<StateError> stateErrors = finalState.getStateErrors();
				for (StateError stateError : stateErrors) {

					this.error = new Error(ERROR_TYPES.PROB_ERROR, stateError.getShortDescription(), e);
					return;
				}
			}
			this.error = new Error(ERROR_TYPES.PROB_ERROR, e.getMessage(), e);
			return;
		} catch (Exception e) {
			// storing all error messages
			debugPrint("****Unkown error: " + e.getMessage());
			this.error = new Error(ERROR_TYPES.UNEXPECTED_ERROR, e.getMessage(), e);
			return;
		}

		this.rulesResult = new RuleResults(this.rulesProject, executeRun.getFinalState());

	}

	private boolean parseAndTranslateRulesProject() {
		this.rulesProject = new RulesProject();
		rulesProject.parseProject(runnerFile);
		rulesProject.setParsingBehaviour(new ParsingBehaviour());

		for (Entry<String, String> pair : constantValuesToBeInjected.entrySet()) {
			rulesProject.addConstantValue(pair.getKey(), pair.getValue());
		}

		/*
		 * parse errors and errors from semantic checks are stored in the
		 * rulesProject
		 */
		rulesProject.checkAndTranslateProject();
		if (rulesProject.hasErrors()) {
			BException bException = rulesProject.getBExceptionList().get(0);
			String message = bException.getMessage();
			debugPrint("****ParseError: " + message);
			this.error = new Error(ERROR_TYPES.PARSE_ERROR, message, bException);
		}
		return rulesProject.hasErrors();
	}

	public Error getError() {
		return this.error;
	}

	public RulesProject getRulesProject() {
		return this.rulesProject;
	}

	public RulesMachineRunner getRulesMachineRunner() {
		return this.rulesMachineRunner;
	}

	public RuleResults getRuleResults() {
		return this.rulesResult;
	}

	public ExecuteRun getExecuteRun() {
		return this.executeRun;
	}

	public File getMainMachineFile() {
		return runnerFile;
	}

	public Map<String, String> getInjectedConstantsValues() {
		return new HashMap<>(this.constantValuesToBeInjected);
	}

	public class Error {
		final ERROR_TYPES type;
		final String message;
		final Exception exception;

		public ERROR_TYPES getType() {
			return this.type;
		}

		public String getMessage() {
			return this.message;
		}

		Error(ERROR_TYPES type, String message, Exception exception) {
			this.type = type;
			this.message = message;
			this.exception = exception;
		}
	}

}
