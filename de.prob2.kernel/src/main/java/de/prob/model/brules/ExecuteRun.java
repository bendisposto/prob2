package de.prob.model.brules;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;

import de.prob.animator.command.ExecuteModelCommand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractModel;
import de.prob.scripting.ExtractedModel;
import de.prob.scripting.StateSpaceProvider;
import de.prob.statespace.State;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.util.StopWatch;

/**
 * 
 * This class performs the following actions: 
 * 1) loads an ExtractedModel 
 * 2) unsubscribes all formulas (reduces evaluation efforts) 
 * 3) run the execute command.
 * 
 * The final state of the probcli execute run is stored. Moreover, all errors
 * which can occur while loading a model are stored. Note, that RULES projects
 * are not parsed and checked by this class. This is done before entering this
 * class.
 * 
 **/
public class ExecuteRun {
	private static StateSpace stateSpace;

	private int maxNumberOfStatesToBeExecuted = Integer.MAX_VALUE;
	private Integer timeout = null;
	private boolean continueAfterErrors = false;
	private final ExtractedModel<? extends AbstractModel> extractedModel;
	private final Map<String, String> prefs;
	private ExecuteModelCommand executeModelCommand;
	private State rootState;
	private final boolean reuseStateSpaceOfPreviousRun;

	public ExecuteRun(final ExtractedModel<? extends AbstractModel> extractedModel, Map<String, String> prefs,
			boolean reuseStateSpaceOfPreviousRun) {
		this.extractedModel = extractedModel;
		this.prefs = prefs;
		this.reuseStateSpaceOfPreviousRun = reuseStateSpaceOfPreviousRun;
	}

	public void start() {
		final Logger logger = LoggerFactory.getLogger(getClass());
		final String LOAD_STATESPACE_TIMER = "loadStateSpace";
		StopWatch.start(LOAD_STATESPACE_TIMER);
		StateSpace stateSpace = this.getOrCreateStateSpace();
		logger.info("Time to load statespace: {} ms", StopWatch.stop(LOAD_STATESPACE_TIMER));

		unsubscribeAllFormulas(stateSpace);

		final String EXECUTE_TIMER = "executeTimer";
		StopWatch.start(EXECUTE_TIMER);
		executeModel(stateSpace);
		logger.info("Time run execute command: {} ms", StopWatch.stop(EXECUTE_TIMER));
	}

	private static void storeStateSpace(StateSpace stateSpace2) {
		stateSpace = stateSpace2;
	}

	private StateSpace getOrCreateStateSpace() {
		if (stateSpace == null || stateSpace.isKilled() || !reuseStateSpaceOfPreviousRun) {
			/*
			 * create a new state space if there is no previous one or if the
			 * previous state space has been killed due to a ProBError
			 */
			storeStateSpace(this.extractedModel.load(this.prefs));
		} else {
			// reuse the previous state space
			StateSpaceProvider ssProvider = new StateSpaceProvider(new Provider<StateSpace>() {
				@Override
				public StateSpace get() {
					return stateSpace;
				}
			});
			RulesModel model = (RulesModel) extractedModel.getModel();
			ssProvider.loadFromCommand(model, null, prefs, model.getLoadCommand());
		}
		return stateSpace;
	}

	private void unsubscribeAllFormulas(StateSpace stateSpace) {
		Set<IEvalElement> subscribedFormulas = stateSpace.getSubscribedFormulas();
		for (IEvalElement iEvalElement : subscribedFormulas) {
			stateSpace.unsubscribe(this, iEvalElement);
		}
	}

	private void executeModel(final StateSpace stateSpace) {
		final Trace t = new Trace(stateSpace);
		this.rootState = t.getCurrentState();
		executeModelCommand = new ExecuteModelCommand(stateSpace, rootState, maxNumberOfStatesToBeExecuted,
				continueAfterErrors, timeout);

		stateSpace.execute(executeModelCommand);

	}

	public ExtractedModel<? extends AbstractModel> getExtractedModel() {
		return this.extractedModel;
	}

	public Map<String, String> getPrefs() {
		return prefs;
	}

	public int getNumberOfStatesExecuted() {
		return executeModelCommand.getNumberofStatesExecuted();
	}

	public ExecuteModelCommand getExecuteModelCommand() {
		return this.executeModelCommand;
	}

	public State getRootState() {
		return this.rootState;
	}

	public StateSpace getUsedStateSpace() {
		return stateSpace;
	}

}
