package de.prob.model.brules;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.ExecuteModelCommand;
import de.prob.animator.command.SetPreferenceCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.animator.command.ExecuteModelCommand.ExecuteModelResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractModel;
import de.prob.scripting.ExtractedModel;
import de.prob.statespace.State;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.util.StopWatch;

/**
 * 
 * This class performs the following actions and is not specific to a RULES
 * machine: 1) loads an ExtractedModel 2) unsubscribes all formulas (reduces
 * evaluation efforts) 3) run the execute command.
 * 
 * The final state of a Prob2 run is stored. Moreover, all errors which can
 * occur while loading a model are stored. Note, that RULES projects are not
 * parsed and checked by this class. This is done before entering this class.
 * 
 **/
public class ExecuteRun {
	private int maxNumberOfStatesToBeExecuted = 10000;

	private final ExtractedModel<? extends AbstractModel> extractedModel;
	private final Map<String, String> prefs;
	private static StateSpace stateSpace;

	private ExecuteModelCommand executeModelCommand;
	private ExecuteModelResult executeModelResult;
	private int numberofStatesExecuted;
	private State rootState;
	private State finalState;

	public ExecuteRun(final ExtractedModel<? extends AbstractModel> extractedModel, Map<String, String> prefs) {
		this.extractedModel = extractedModel;
		this.prefs = prefs;
	}

	public void start() {
		StopWatch.start("loadStateSpace");
		StateSpace stateSpace = this.getStateSpace();
		//debugPrint(StopWatch.getRunTimeAsString("loadStateSpace"));

		unsubscribeAllFormulas(stateSpace);

		StopWatch.start("execute");
		executeUntilEnd(stateSpace);
		//debugPrint(StopWatch.getRunTimeAsString("execute"));
	}

	private StateSpace getStateSpace() {
		if (stateSpace == null) {
			stateSpace = this.extractedModel.load(this.prefs);
		} else {
			RulesModel model = (RulesModel) extractedModel.getModel();
			stateSpace.setModel(model, null);
			List<AbstractCommand> cmds = new ArrayList<AbstractCommand>();

			for (Entry<String, String> pref : this.prefs.entrySet()) {
				cmds.add(new SetPreferenceCommand(pref.getKey(), pref.getValue()));
			}
			stateSpace.execute(new ComposedCommand(cmds));
			stateSpace.execute(model.getLoadCommand());
			stateSpace.execute(new StartAnimationCommand());
		}
		return stateSpace;
	}

	private void unsubscribeAllFormulas(StateSpace stateSpace) {
		Set<IEvalElement> subscribedFormulas = stateSpace.getSubscribedFormulas();
		for (IEvalElement iEvalElement : subscribedFormulas) {
			stateSpace.unsubscribe(this, iEvalElement);
		}
	}

	private void executeUntilEnd(final StateSpace stateSpace) {
		final Trace t = new Trace(stateSpace);
		this.rootState = t.getCurrentState();
		executeModelCommand = new ExecuteModelCommand(stateSpace, rootState, maxNumberOfStatesToBeExecuted);
		stateSpace.execute(executeModelCommand);
		this.numberofStatesExecuted = executeModelCommand.getNumberofStatesExecuted();
		this.executeModelResult = executeModelCommand.getResult();
		
		this.finalState = executeModelCommand.getFinalState();
//		List<Transition> newTransitions = executeModelCommand.getNewTransitions();
//		Transition transition = newTransitions.get(0);
//		finalState = transition.getDestination();
	}

	public ExtractedModel<? extends AbstractModel> getExtractedModel() {
		return this.extractedModel;
	}

	public Map<String, String> getPrefs() {
		return prefs;
	}

	public ExecuteModelResult getExecuteModelResult() {
		return this.executeModelResult;
	}

	public int getNumberOfStatesExecuted() {
		return this.numberofStatesExecuted;
	}

	public ExecuteModelCommand getExecuteModelCommand() {
		return this.executeModelCommand;
	}

	public State getRootState() {
		return this.rootState;
	}

	public State getFinalState() {
		return this.finalState;
	}

}
