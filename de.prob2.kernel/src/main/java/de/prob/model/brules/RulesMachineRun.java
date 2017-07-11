package de.prob.model.brules;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.be4.classicalb.core.parser.ParsingBehaviour;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.rules.RulesProject;
import de.prob.animator.command.GetTotalNumberOfErrorsCommand;
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

	private final List<Error> errors;

	private final File runnerFile;
	private final Map<String, String> proBCorePreferences;
	private final Map<String, String> constantValuesToBeInjected;

	private RuleResults rulesResult;
	private int maxNumberOfReportedCounterExamples = 50;

	final Logger logger = LoggerFactory.getLogger(getClass());

	private BigInteger totalNumberOfProBCliErrors;

	public RulesMachineRun(File runner) {
		this(runner, new HashMap<String, String>(), new HashMap<String, String>());
	}

	public RulesMachineRun(File runner, Map<String, String> prefs) {
		this(runner, prefs, new HashMap<String, String>());
	}

	public RulesMachineRun(File runner, Map<String, String> prefs, Map<String, String> constantValuesToBeInjected) {
		this.runnerFile = runner;
		this.errors = new ArrayList<>();
		this.proBCorePreferences = new HashMap<>();
		if (prefs != null) {
			this.proBCorePreferences.putAll(prefs);
		}
		// add mandatory preferences
		this.proBCorePreferences.put("TRY_FIND_ABORT", "TRUE");
		this.proBCorePreferences.put("CLPFD", "FALSE");
		this.proBCorePreferences.put("MAX_DISPLAY_SET", "-1");

		this.constantValuesToBeInjected = constantValuesToBeInjected;
	}

	public void setMaxNumberOfReportedCounterExamples(int i) {
		this.maxNumberOfReportedCounterExamples = i;
	}

	public void setProBCorePreferences(Map<String, String> prefs) {
		proBCorePreferences.putAll(prefs);
	}

	public void start() {
		logger.info("Starting rules machine run: {}", this.runnerFile.getAbsolutePath());
		final String PARSER_TIMER = "parsing";
		StopWatch.start(PARSER_TIMER);
		boolean hasParseErrors = parseAndTranslateRulesProject();
		logger.info("Time to parse rules project: {} ms", StopWatch.stop(PARSER_TIMER));
		if (hasParseErrors) {
			logger.error("RULES_MACHINE has errors!");
			return;
		}

		this.executeRun = rulesMachineRunner.createRulesMachineExecuteRun(this.rulesProject, runnerFile,
				this.proBCorePreferences);

		try {
			final String PROB2_RUN_TIMER = "prob2Run";
			StopWatch.start(PROB2_RUN_TIMER);
			logger.info("Start execute ...");

			// start
			this.executeRun.start();

			logger.info("Execute run finished. Time: {} ms", StopWatch.stop(PROB2_RUN_TIMER));
		} catch (ProBError e) {
			logger.error("ProBError: {}", e.getMessage());
			if (executeRun.getExecuteModelCommand() != null) {
				State finalState = executeRun.getExecuteModelCommand().getFinalState();
				Collection<StateError> stateErrors = finalState.getStateErrors();
				for (StateError stateError : stateErrors) {
					this.errors.add(new Error(ERROR_TYPES.PROB_ERROR, stateError.getShortDescription(), e));
				}
			} else {
				// static errors such as type errors or error while loading the
				// state space
				this.errors.add(new Error(ERROR_TYPES.PROB_ERROR, e.getMessage(), e));

				// no final state is available and thus we can not create
				// RuleResults
				return;
			}
		} catch (Exception e) {
			logger.error("Unexpected error occured: {}", e.getMessage());
			// storing all error messages
			this.errors.add(new Error(ERROR_TYPES.PROB_ERROR, e.getMessage(), e));
			return;
		} finally {
			if (executeRun.getUsedStateSpace() != null) {
				GetTotalNumberOfErrorsCommand totalNumberOfErrorsCommand = new GetTotalNumberOfErrorsCommand();
				executeRun.getUsedStateSpace().execute(totalNumberOfErrorsCommand);
				totalNumberOfProBCliErrors = totalNumberOfErrorsCommand.getTotalNumberOfErrors();
			}

		}
		final String EXTRACT_RESULTS_TIMER = "extractResults";
		StopWatch.start(EXTRACT_RESULTS_TIMER);
		this.rulesResult = new RuleResults(this.rulesProject, executeRun.getExecuteModelCommand().getFinalState(),
				maxNumberOfReportedCounterExamples);
		logger.info("Time to extract results form final state: {}", StopWatch.stop(EXTRACT_RESULTS_TIMER));
	}

	private boolean parseAndTranslateRulesProject() {
		this.rulesProject = new RulesProject();
		rulesProject.parseProject(runnerFile);
		ParsingBehaviour parsingBehaviour = new ParsingBehaviour();
		parsingBehaviour.setAddLineNumbers(true);
		rulesProject.setParsingBehaviour(parsingBehaviour);

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
			logger.error("Parse error:  {}", message);

			this.errors.add(new Error(ERROR_TYPES.PARSE_ERROR, message, bException));
		}
		return rulesProject.hasErrors();
	}

	public boolean hasError() {
		return !this.errors.isEmpty();
	}

	public List<Error> getErrorList() {
		return new ArrayList<>(this.errors);
	}

	public Error getFirstError() {
		return this.errors.get(0);
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

	/**
	 * Returns the total number of errors recorded by a concrete ProB cli
	 * instance. Note, if the ProB cli instance is reused for further
	 * RulesMachineRuns, this number is NOT reseted. Can be {@code null} if
	 * there is no state space available. Moreover, this number does not match
	 * the size of the {@link RulesMachineRun#errors} list.
	 * 
	 * @return total number of ProB cli errors
	 */
	public BigInteger getTotalNumberOfProBCliErrors() {
		return this.totalNumberOfProBCliErrors;
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

		public Exception getException() {
			return this.exception;
		}

		@Override
		public String toString() {
			return type + ": " + message;
		}

		Error(ERROR_TYPES type, String message, Exception exception) {
			this.type = type;
			this.message = message;
			this.exception = exception;
		}
	}

}
