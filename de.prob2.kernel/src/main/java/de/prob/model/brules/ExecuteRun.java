package de.prob.model.brules;

import java.util.Map;


import de.prob.animator.command.ExecuteModelCommand;
import de.prob.model.representation.AbstractModel;
import de.prob.scripting.ExtractedModel;
import de.prob.scripting.StateSpaceProvider;
import de.prob.statespace.State;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.util.StopWatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class performs the following actions:
 * 
 * <pre>
 * 1) loads an ExtractedModel 
 * 2) run the execute command.
 * </pre>
 * 
 * The final state of the probcli execute run is stored. Moreover, all errors
 * which can occur while loading a model are stored. Note, that RULES projects
 * are not parsed and checked by this class. This is done before entering this
 * class.
 * 
 **/
public class ExecuteRun {
	private static StateSpace staticStateSpace;

	private StateSpace stateSpace;
	private int maxNumberOfStatesToBeExecuted = Integer.MAX_VALUE;
	private Integer timeout = null;
	private final boolean continueAfterErrors;
	private final ExtractedModel<? extends AbstractModel> extractedModel;
	private final Map<String, String> prefs;
	private ExecuteModelCommand executeModelCommand;
	private State rootState;
	private final boolean reuseStateSpaceOfPreviousRun;

	public ExecuteRun(final ExtractedModel<? extends AbstractModel> extractedModel, Map<String, String> prefs,
			boolean reuseStateSpaceOfPreviousRun, boolean continueAfterErrors) {
		this.extractedModel = extractedModel;
		this.continueAfterErrors = continueAfterErrors;
		this.prefs = prefs;
		this.reuseStateSpaceOfPreviousRun = reuseStateSpaceOfPreviousRun;
	}

	public void start() {
		final Logger logger = LoggerFactory.getLogger(getClass());
		final String loadStateSpaceTimer = "loadStateSpace";
		StopWatch.start(loadStateSpaceTimer);
		getOrCreateStateSpace();
		logger.info("Time to load statespace: {} ms", StopWatch.stop(loadStateSpaceTimer));

		final String executeTimer = "executeTimer";
		StopWatch.start(executeTimer);
		executeModel(this.stateSpace);
		logger.info("Time run execute command: {} ms", StopWatch.stop(executeTimer));
	}

	private void getOrCreateStateSpace() {
		if (staticStateSpace == null || staticStateSpace.isKilled() || !reuseStateSpaceOfPreviousRun) {
			/*
			 * create a new state space if there is no previous one or if the
			 * previous state space has been killed due to a ProBError
			 */
			this.stateSpace = this.extractedModel.load(this.prefs);
			setStaticStateSpace(stateSpace);
		} else {
			// reuse the previous state space
			StateSpaceProvider ssProvider = new StateSpaceProvider(()-> stateSpace);
			
			RulesModel model = (RulesModel) extractedModel.getModel();
			ssProvider.loadFromCommand(model, null, prefs, model.getLoadCommand());
			this.stateSpace = staticStateSpace;
		}
	}

	private static void setStaticStateSpace(StateSpace stateSpace2) {
		if (staticStateSpace != null) {
			staticStateSpace.kill();
		}
		staticStateSpace = stateSpace2;
	}

	private void executeModel(final StateSpace stateSpace) {
		final Trace t = new Trace(stateSpace);
		this.rootState = t.getCurrentState();
		executeModelCommand = new ExecuteModelCommand(stateSpace, rootState, maxNumberOfStatesToBeExecuted,
				continueAfterErrors, timeout);
		stateSpace.execute(executeModelCommand);
	}

	public ExecuteModelCommand getExecuteModelCommand() {
		return this.executeModelCommand;
	}

	public State getRootState() {
		return this.rootState;
	}

	public StateSpace getUsedStateSpace() {
		return this.stateSpace;
	}

}
