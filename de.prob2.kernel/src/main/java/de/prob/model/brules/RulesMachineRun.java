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
import de.be4.classicalb.core.parser.rules.AbstractOperation;
import de.be4.classicalb.core.parser.rules.RulesProject;
import de.prob.animator.command.GetTotalNumberOfErrorsCommand;
import de.prob.animator.domainobjects.StateError;
import de.prob.exception.ProBError;
import de.prob.statespace.State;
import de.prob.statespace.StateSpace;
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

	private RuleResults ruleResults;
	private int maxNumberOfReportedCounterExamples = 50;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	enum Timer {
		PARSING, EXECUTE_RUN, EXTRACT_RESULTS
	}

	private final StopWatch<Timer> stopWatch = new StopWatch<>();

	private BigInteger totalNumberOfProBCliErrors;

	private boolean continueAfterErrors = false;

	private StateSpace stateSpace;

	public RulesMachineRun(File runner) {
		this(runner, new HashMap<String, String>(), new HashMap<String, String>());
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
		//this.proBCorePreferences.put("DATA_VALIDATION", "TRUE");
		

		this.constantValuesToBeInjected = constantValuesToBeInjected;
	}

	public void setMaxNumberOfReportedCounterExamples(int i) {
		this.maxNumberOfReportedCounterExamples = i;
	}

	public void setContinueAfterErrors(boolean continueAfterErrors) {
		this.continueAfterErrors = continueAfterErrors;
	}

	public void start() {
		logger.info("Starting rules machine run: {}", this.runnerFile.getAbsolutePath());
		stopWatch.start(Timer.PARSING);
		boolean hasParseErrors = parseAndTranslateRulesProject();
		logger.info("Time to parse rules project: {} ms", stopWatch.stop(Timer.PARSING));
		if (hasParseErrors) {
			logger.error("RULES_MACHINE has errors!");
			return;
		}
		this.executeRun = rulesMachineRunner.createRulesMachineExecuteRun(this.rulesProject, runnerFile,
				this.proBCorePreferences, continueAfterErrors, this.getStateSpace());
		try {
			stopWatch.start(Timer.EXECUTE_RUN);
			logger.info("Start execute ...");
			this.executeRun.start();
			logger.info("Execute run finished. Time: {} ms", stopWatch.stop(Timer.EXECUTE_RUN));
		} catch (ProBError e) {
			logger.error("ProBError: {}", e.getMessage());
			if (executeRun.getExecuteModelCommand() != null) {
				try {
					State finalState = executeRun.getExecuteModelCommand().getFinalState();
					// explores the final state and can throw a ProBError
					Collection<StateError> stateErrors = finalState.getStateErrors();
					for (StateError stateError : stateErrors) {
						this.errors.add(new Error(ERROR_TYPES.PROB_ERROR, stateError.getLongDescription(), e));
					}
				} catch (ProBError e2) {
					// Enumeration errors
					this.errors.add(new Error(ERROR_TYPES.PROB_ERROR, e2.getMessage(), e2));
					return;
				}
			} else {
				/*- static errors such as type errors or errors while loading the  state space */
				this.errors.add(new Error(ERROR_TYPES.PROB_ERROR, e.getMessage(), e));
				/*- no final state is available and thus we can not create RuleResults */
				return;
			}
		} catch (Exception e) {
			logger.error("Unexpected error occured: {}", e.getMessage(), e);
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
		
		
		this.stateSpace = this.executeRun.getUsedStateSpace();
		stopWatch.start(Timer.EXTRACT_RESULTS);
		this.ruleResults = new RuleResults(this.rulesProject, executeRun.getExecuteModelCommand().getFinalState(),
				maxNumberOfReportedCounterExamples);
		logger.info("Time to extract results form final state: {}", stopWatch.stop(Timer.EXTRACT_RESULTS));

	}

	private boolean parseAndTranslateRulesProject() {
		this.rulesProject = new RulesProject();
		ParsingBehaviour parsingBehaviour = new ParsingBehaviour();
		parsingBehaviour.setAddLineNumbers(true);
		rulesProject.setParsingBehaviour(parsingBehaviour);
		rulesProject.parseProject(runnerFile);

		for (Entry<String, String> pair : constantValuesToBeInjected.entrySet()) {
			rulesProject.addConstantValue(pair.getKey(), pair.getValue());
		}

		/*
		 * parse errors and errors from semantic checks are stored in the rulesProject
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

	/**
	 * 
	 * @return the first error found or {@code null} if no error has occurred
	 */
	public Error getFirstError() {
		if (this.errors.isEmpty()) {
			return null;
		} else {
			return this.errors.get(0);
		}

	}

	public RulesProject getRulesProject() {
		return this.rulesProject;
	}

	public RuleResults getRuleResults() {
		return this.ruleResults;
	}

	public ExecuteRun getExecuteRun() {
		return this.executeRun;
	}

	public File getRunnerFile() {
		return runnerFile;
	}

	/**
	 * Returns the total number of errors recorded by a concrete ProB cli instance.
	 * Note, if the ProB cli instance is reused for further RulesMachineRuns, this
	 * number is NOT reset. Can be {@code null} if there is no state space
	 * available. Moreover, this number does not match the size of the
	 * {@link RulesMachineRun#errors} list.
	 * 
	 * @return total number of ProB cli errors
	 */
	public BigInteger getTotalNumberOfProBCliErrors() {
		return this.totalNumberOfProBCliErrors;
	}

	public StateSpace getStateSpace() {
		return stateSpace;
	}

	public void setStateSpace(StateSpace stateSpace) {
		this.stateSpace = stateSpace;
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
