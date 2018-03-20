package de.prob.statespace;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import com.google.common.base.Joiner;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.animator.IAnimator;
import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.CheckIfStateIdValidCommand;
import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.EvaluationCommand;
import de.prob.animator.command.FindStateCommand;
import de.prob.animator.command.FindTraceBetweenNodesCommand;
import de.prob.animator.command.FormulaTypecheckCommand;
import de.prob.animator.command.GetCurrentPreferencesCommand;
import de.prob.animator.command.GetMachineOperationInfos.OperationInfo;
import de.prob.animator.command.GetOperationByPredicateCommand;
import de.prob.animator.command.GetOpsFromIds;
import de.prob.animator.command.GetShortestTraceCommand;
import de.prob.animator.command.GetStatesFromPredicate;
import de.prob.animator.command.RegisterFormulaCommand;
import de.prob.animator.command.UnregisterFormulaCommand;
import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.TypeCheckResult;
import de.prob.annotations.MaxCacheSize;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.CSPModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * The StateSpace is where the animation of a given model is carried out. The
 * methods in the StateSpace allow the user to:
 *
 * 1) Find new states and operations
 *
 * 2) Inspect different states within the StateSpace
 *
 * 3) Evaluate custom predicates and expressions
 *
 * 4) Register listeners that are notified of new states and operations
 *
 * The implementation of the StateSpace is as a {@link StateSpace} with
 * {@link State}s as vertices and {@link Transition}s as edges. Therefore, some
 * basic graph functionalities are provided.
 *
 * @author joy
 *
 */
public class StateSpace implements IAnimator {

	Logger logger = LoggerFactory.getLogger(StateSpace.class);
	private IAnimator animator;

	private final HashMap<IEvalElement, WeakHashMap<Object, Object>> formulaRegistry = new HashMap<>();

	private LoadedMachine loadedMachine;

	private final LoadingCache<String, State> states;

	/**
	 * An implementation of a {@link CacheLoader} that tries to load a state
	 * with the specified id into the {@link StateSpace#states} cache.
	 *
	 * ProB prolog is queried to see if the specified state exists in the state
	 * space on the prolog side, and if so, the state is loaded into the states
	 * cache.
	 *
	 * Otherwise, an {@link IllegalArgumentException} is thrown.
	 *
	 * @author joy
	 *
	 */
	private class StateCacheLoader extends CacheLoader<String, State> {

		private final StateSpace stateSpace;

		public StateCacheLoader(final StateSpace stateSpace) {
			this.stateSpace = stateSpace;
		}

		@Override
		public State load(final String key) throws Exception {
			CheckIfStateIdValidCommand cmd = new CheckIfStateIdValidCommand(key);
			stateSpace.execute(cmd);
			if (cmd.isValidState()) {
				return new State(key, stateSpace);
			}
			throw new IllegalArgumentException(key + " does not represent a valid state in the StateSpace");
		}

	}

	private AbstractModel model;
	private AbstractElement mainComponent;
	private volatile boolean killed;

	@Inject
	public StateSpace(final Provider<IAnimator> panimator, @MaxCacheSize final int maxSize) {
		animator = panimator.get();
		states = CacheBuilder.newBuilder().maximumSize(maxSize).build(new StateCacheLoader(this));
	}

	/**
	 * Retrieve the root state from the state space.
	 *
	 * @return the root state from the state space (it will be added to the
	 *         states cache if it doesn't yet exist)
	 */
	public State getRoot() {
		return addState("root");
	}

	/**
	 * Retrieve a state from the state space that has the specified state id.
	 *
	 * @param id
	 *            of the state to be retrieved
	 * @return the state object associated with the given id. This is added to
	 *         the cache via the loading mechanism in {@link StateCacheLoader}
	 * @throws IllegalArgumentException
	 *             if a state with the specified id doesn't exist
	 */
	public State getState(final String id) {
		try {
			return states.get(id);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * Adds a state with the specified id to the StateSpace (if it isn't already
	 * in the state space), and returns the state to the user.
	 *
	 * @param id
	 *            of the state to be retrieved
	 * @return a state object associated with the given id.
	 */
	State addState(final String id) {
		State sId = states.getIfPresent(id);
		if (sId != null) {
			return sId;
		}
		// This avoids the prolog query because this can only be called by
		// objects that know that this state id actually works.
		sId = new State(id, this);
		states.put(id, sId);
		return sId;
	}

	/**
	 * Most states in the state space use numeric ids. This method exists to
	 * allow the user to access a given state via integer id instead of string
	 * id. The integer value -1 maps to the root state (the only state id that
	 * is not a number)
	 *
	 * @param id
	 *            integer value of the state id to be retrieved
	 * @return a state associated with the id if one exists.
	 */
	public State getState(final int id) {
		if (id == -1) {
			return getRoot();
		}
		return getState(String.valueOf(id));
	}

	/**
	 * This method is implemented to provide access to the {@link State} objects
	 * specified by an integer identifier. This maps to a groovy operator so
	 * that in the console users can type variableOfTypeStateSpace[stateId] and
	 * receive the corresponding State back. An IllegalArgumentException is
	 * thrown if the specified id is unknown.
	 *
	 * @param stateId
	 *            of the state thate is to be found.
	 * @return {@link State} for the specified id
	 * @throws IllegalArgumentException
	 *             if a state with the specified id doesn't exist
	 */
	public Object getAt(final int stateId) {
		return getState(stateId);
	}

	/**
	 * Whenever a {@link StateSpace} instance is created, it is assigned a
	 * unique identifier to help external parties differentiate between two
	 * instances. This getter method returns this id.
	 *
	 * @return the unique {@link String} id associated with this
	 *         {@link StateSpace} instance
	 */
	@Override
	public String getId() {
		return animator.getId();
	}

	/**
	 * <p>
	 * Find states for which a given predicate is true.
	 * </p>
	 *
	 * <p>
	 * <b>NOTE:</b> The returned list of states will also include states which
	 * are not initialised. The semantics of the method could therefore be
	 * better described as finding:
	 * </p>
	 * <p>
	 * <code>{states matching predicate} &cup; {noninitialised states}</code>
	 * </p>
	 *
	 *
	 * @param predicate
	 *            for which states will be found
	 * @return a {@link List} of any states found
	 */
	public List<State> getStatesFromPredicate(final IEvalElement predicate) {
		GetStatesFromPredicate cmd = new GetStatesFromPredicate(predicate);
		execute(cmd);
		List<String> ids = cmd.getIds();
		List<State> sIds = new ArrayList<>();
		for (String s : ids) {
			sIds.add(addState(s));
		}
		return sIds;
	}

	/**
	 * Takes the name of an operation and a predicate and finds Operations that
	 * satisfy the name and predicate at the given stateId. New Operations are
	 * added to the graph. This is only valid for ClassicalB predicates.
	 *
	 * @param stateId
	 *            {@link State} from which the operation should be found
	 * @param opName
	 *            name of the operation that should be executed
	 * @param predicate
	 *            an additional guard for the operation. This usually describes
	 *            the parameters
	 * @param nrOfSolutions
	 *            int number of solutions that should be found for the given
	 *            predicate
	 * @return list of operations calculated by ProB
	 */
	public List<Transition> transitionFromPredicate(final State stateId, final String opName, final String predicate,
			final int nrOfSolutions) {
		final IEvalElement pred = model.parseFormula(predicate);
		final GetOperationByPredicateCommand command = new GetOperationByPredicateCommand(this, stateId.getId(), opName,
				pred, nrOfSolutions);
		execute(command);
		if (command.hasErrors()) {
			throw new IllegalArgumentException("Executing operation " + opName + " with predicate " + predicate
					+ " produced errors: " + Joiner.on(", ").join(command.getErrors()));
		}
		return command.getNewTransitions();
	}

	public List<Transition> getTransitionsBasedOnParameterValues(final State stateId, final String opName,
			final List<String> parameterValues, final int nrOfSolutions) {
		String predicate = "1 = 1";// default value
		if (!opName.equals("$initialise_machine") && !opName.equals("$setup_constants")) {
			if (!getLoadedMachine().containsOperations(opName)) {
				throw new IllegalArgumentException("Unknown operation '" + opName + "'");
			}
			OperationInfo machineOperationInfo = getLoadedMachine().getMachineOperationInfo(opName);
			List<String> parameterNames = machineOperationInfo.getParameterNames();
			StringBuilder sb = new StringBuilder();
			if (!parameterNames.isEmpty()) {
				if (parameterNames.size() != parameterValues.size()) {
					throw new IllegalArgumentException("Cannot execute operation " + opName
							+ " because the number of parameters does not match the number of provied values: "
							+ parameterNames.size() + " vs " + parameterValues.size());
				}
				for (int i = 0; i < parameterNames.size(); i++) {
					sb.append(parameterNames.get(i)).append(" = ").append(parameterValues.get(i));
					if (i < parameterNames.size() - 1) {
						sb.append(" & ");
					}
				}
				predicate = sb.toString();
			}
		}

		final IEvalElement pred = model.parseFormula(predicate);
		final GetOperationByPredicateCommand command = new GetOperationByPredicateCommand(this, stateId.getId(), opName,
				pred, nrOfSolutions);
		execute(command);
		if (command.hasErrors()) {
			throw new IllegalArgumentException("Executing operation " + opName + " with predicate " + predicate
					+ " produced errors: " + Joiner.on(", ").join(command.getErrors()));
		}
		return command.getNewTransitions();
	}

	/**
	 * Tests to see if a combination of an operation name and a predicate is
	 * valid from a given state.
	 *
	 * @param stateId
	 *            {@link State} id for state to test
	 * @param name
	 *            {@link String} name of operation
	 * @param predicate
	 *            {@link String} predicate to test
	 * @return true, if the operation is valid from the given state. False
	 *         otherwise.
	 */
	public boolean isValidOperation(final State stateId, final String name, final String predicate) {
		final ClassicalB pred = new ClassicalB(predicate);
		GetOperationByPredicateCommand command = new GetOperationByPredicateCommand(this, stateId.getId(), name, pred,
				1);
		execute(command);
		return !command.hasErrors();
	}

	/**
	 * Attempts to type check a specified formula in the scope of the model
	 * loaded in this state space.
	 *
	 * @param formula
	 *            to be type checked
	 * @return a {@link TypeCheckResult} representing the result of the parser
	 *         (containing the type of the formula and any errors that occurred)
	 */
	public TypeCheckResult typeCheck(final IEvalElement formula) {
		FormulaTypecheckCommand cmd = new FormulaTypecheckCommand(formula);
		execute(cmd);
		return cmd.getResult();
	}

	/**
	 * Evaluates a list of formulas in a given state. Uses the implementation in
	 * {@link State#eval(List)}
	 *
	 * @param state
	 *            for which the list of formulas should be evaluated
	 * @param formulas
	 *            to be evaluated
	 * @return a list of {@link AbstractEvalResult}s
	 */
	public List<AbstractEvalResult> eval(final State state, final List<IEvalElement> formulas) {
		return state.eval(formulas);
	}

	/**
	 * Calculates the registered formulas at the given state and returns the
	 * cached values. Calls the {@link State#explore()} method, and uses the
	 * {@link State#getValues()} method.
	 *
	 * @param state
	 *            for which the values are to be retrieved
	 * @return map from {@link IEvalElement} object to
	 *         {@link AbstractEvalResult} objects
	 */
	public Map<IEvalElement, AbstractEvalResult> valuesAt(final State state) {
		state.explore();
		return state.getValues();
	}

	/**
	 * This checks if the {@link State#isInitialised()} property is set. If so,
	 * it is safe to evaluate formulas for the given state.
	 *
	 * @param state
	 *            which is to be tested
	 * @return whether or not formulas should be evaluated in this state
	 */
	public boolean canBeEvaluated(final State state) {
		return state.isInitialised();
	}

	/**
	 * This method lets ProB know that the subscriber is interested in the
	 * specified formulas. ProB will then evaluate the formulas for every state
	 * (after which the values can be retrieved from the
	 * {@link State#getValues()} method).
	 *
	 * @param subscriber
	 *            who is interested in the given formulas
	 * @param formulas
	 *            that are of interest
	 * @return whether or not the subscription was successful (will return true
	 *         if at least one of the formulas was successfully subscribed)
	 */
	public boolean subscribe(final Object subscriber, final Collection<? extends IEvalElement> formulas) {
		boolean success = false;
		List<AbstractCommand> subscribeCmds = new ArrayList<>();
		for (IEvalElement formulaOfInterest : formulas) {
			if (formulaOfInterest instanceof CSP) {
				logger.info(
						"CSP formula {} not subscribed because CSP evaluation is not state based. Use eval method instead",
						formulaOfInterest.getCode());
			} else {
				if (formulaRegistry.containsKey(formulaOfInterest)) {
					formulaRegistry.get(formulaOfInterest).put(subscriber,
							new WeakReference<Object>(formulaOfInterest));
					success = true;
				} else {
					WeakHashMap<Object, Object> subscribers = new WeakHashMap<>();
					subscribers.put(subscriber, new WeakReference<Object>(subscriber));
					formulaRegistry.put(formulaOfInterest, subscribers);
					subscribeCmds.add(new RegisterFormulaCommand(formulaOfInterest));
					success = true;
				}
			}
		}
		if (!subscribeCmds.isEmpty()) {
			execute(new ComposedCommand(subscribeCmds));
		}
		return success;
	}

	/**
	 * If a class is interested in having a particular formula calculated and
	 * cached whenever a new state is explored, then they "subscribe" to that
	 * formula with a reference to themselves. This should only be used for
	 * B-Type formulas ({@code EventB} or {@code ClassicalB}). {@code CSP}
	 * formulas will not be subscribed, because CSP evaluation is not state
	 * based.
	 *
	 * @param subscriber
	 *            who is interested in the formula
	 * @param formulaOfInterest
	 *            that is to be subscribed
	 * @return will return true if the subscription was not successful, false
	 *         otherwise
	 */
	public boolean subscribe(final Object subscriber, final IEvalElement formulaOfInterest) {
		return this.subscribe(subscriber, Collections.singletonList(formulaOfInterest));
	}

	/**
	 * @param formula
	 *            to be checked
	 * @return whether or not a subscriber is interested in this formula
	 */
	public boolean isSubscribed(final IEvalElement formula) {
		return formulaRegistry.containsKey(formula) && !formulaRegistry.get(formula).isEmpty();
	}

	/**
	 * If a subscribed class is no longer interested in the value of a
	 * particular formula, then they can unsubscribe to that formula
	 *
	 * @param subscriber
	 *            who is no longer interested in the formulas
	 * @param formulas
	 *            that are to be unsubscribed
	 * @return whether or not the unsubscription was successful (will return
	 *         false if none of the formulas were subscribed to begin with)
	 */

	public boolean unsubscribe(final Object subscriber, final Collection<? extends IEvalElement> formulas) {
		return this.unsubscribe(subscriber, formulas, false);
	}

	public boolean unsubscribe(final Object subscriber, final Collection<? extends IEvalElement> formulas, boolean unregister) {
		boolean success = false;
		final List<AbstractCommand> unsubscribeCmds = new ArrayList<>();
		for (IEvalElement formula : formulas) {
			if (formulaRegistry.containsKey(formula)) {
				final WeakHashMap<Object, Object> subscribers = formulaRegistry.get(formula);
				subscribers.remove(subscriber);
				if (subscribers.isEmpty() && unregister) {
					unsubscribeCmds.add(new UnregisterFormulaCommand(formula));
				}
				success = true;
			}
		}
		if (!unsubscribeCmds.isEmpty()) {
			execute(new ComposedCommand(unsubscribeCmds));
		}
		return success;
	}

	/**
	 * If a subscribed class is no longer interested in the value of a
	 * particular formula, then they can unsubscribe to that formula
	 *
	 * @param subscriber
	 *            who is no longer interested in the formula
	 * @param formula
	 *            which is to be unsubscribed
	 * @return whether or not the unsubscription was successful (will return
	 *         false if the formula was never subscribed to begin with)
	 */
	public boolean unsubscribe(final Object subscriber, final IEvalElement formula) {
		return this.unsubscribe(subscriber, Collections.singletonList(formula));
	}

	/**
	 * @return a {@link Set} containing the formulas for which there are
	 *         currently interested subscribers.
	 */
	public Set<IEvalElement> getSubscribedFormulas() {
		HashSet<IEvalElement> result = new HashSet<>();
		for (Entry<IEvalElement, WeakHashMap<Object, Object>> entry : formulaRegistry.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				result.add(entry.getKey());
			}
		}
		return result;
	}

	/**
	 * @return a {@link Map} of the preferences in the current animation
	 */
	public Map<String, String> getCurrentPreferences() {
		GetCurrentPreferencesCommand cmd = new GetCurrentPreferencesCommand();
		execute(cmd);
		return cmd.getPreferences();
	}

	// ANIMATOR
	@Override
	public void sendInterrupt() {
		animator.sendInterrupt();
	}

	@Override
	public void execute(final AbstractCommand command) {
		animator.execute(command);
	}

	@Override
	public void execute(final AbstractCommand... commands) {
		animator.execute(commands);
	}

	@Override
	public void startTransaction() {
		animator.startTransaction();
	}

	@Override
	public void endTransaction() {
		animator.endTransaction();
	}

	@Override
	public boolean isBusy() {
		return animator.isBusy();
	}

	@Override
	public String toString() {
		return animator.getId();
	}

	public LoadedMachine getLoadedMachine() {
		if (this.loadedMachine == null) {
			loadedMachine = new LoadedMachine(this);
		}
		return this.loadedMachine;
	}

	/**
	 * @param state
	 *            whose operations are to be printed
	 * @return Returns a String representation of the operations available from
	 *         the specified {@link State}. This is mainly useful for console
	 *         output.
	 */
	public String printOps(final State state) {
		final StringBuilder sb = new StringBuilder();
		final Collection<Transition> opIds = state.getTransitions();

		sb.append("Operations: \n");
		for (final Transition opId : opIds) {
			sb.append("  " + opId.getId() + ": " + opId.getRep());
			sb.append("\n");
		}

		if (!state.isExplored()) {
			sb.append("\n Possibly not all transitions shown. ProB does not explore states by default");
		}
		return sb.toString();
	}

	/**
	 * @param state
	 *            which is to be printed
	 * @return Returns a String representation of the information about the
	 *         state with the specified {@link State}. This includes the id for
	 *         the state, the cached calculated values, and if an invariant
	 *         violation or a timeout has occured for the given state. This is
	 *         mainly useful for console output.
	 */
	public String printState(final State state) {
		final StringBuilder sb = new StringBuilder();

		state.explore();

		sb.append("STATE: " + state + "\n\n");
		sb.append("VALUES:\n");
		Map<IEvalElement, AbstractEvalResult> currentState = state.getValues();
		final Set<Entry<IEvalElement, AbstractEvalResult>> entrySet = currentState.entrySet();
		for (final Entry<IEvalElement, AbstractEvalResult> entry : entrySet) {
			sb.append("  " + entry.getKey().getCode() + " -> " + entry.getValue().toString() + "\n");
		}

		return sb.toString();
	}

	/**
	 * This calculated the shortest path from root to the specified state. This
	 * contacts the ProB kernel via the {@link GetShortestTraceCommand} and then
	 * uses the generated of operations to generate a Trace via the
	 * {@link StateSpace#getTrace(ITraceDescription)} method.
	 *
	 * @param stateId
	 *            state id for which the trace through the state space should be
	 *            found.
	 * @return trace in the form of a {@link Trace} object
	 */
	public Trace getTrace(final String stateId) {
		GetShortestTraceCommand cmd = new GetShortestTraceCommand(this, stateId);
		execute(cmd);
		return getTrace(cmd);
	}

	/**
	 * Calculates a trace between the specified states.
	 *
	 * @param sourceId
	 *            of source node
	 * @param destId
	 *            of destination node
	 * @return shortest Trace between the two specified ids (in form of
	 *         {@link Trace} object)
	 */
	public Trace getTrace(final String sourceId, final String destId) {
		FindTraceBetweenNodesCommand cmd = new FindTraceBetweenNodesCommand(this, sourceId, destId);
		execute(cmd);
		return getTrace(cmd);
	}

	/**
	 * Takes a list of {@link String} operation id names and generates a
	 * {@link Trace} by executing each one in order. This calls the
	 * {@link Trace#add(String)} method which can throw an
	 * {@link IllegalArgumentException} if executing the operations in the
	 * specified order is not possible. It assumes that the Trace begins from
	 * the root state.
	 *
	 * @param transitionIds
	 *            List of transition ids in the order that they should be
	 *            executed.
	 * @return {@link Trace} generated by executing the ids.
	 */
	public Trace getTrace(final List<String> transitionIds) {
		Trace t = new Trace(this);
		for (String id : transitionIds) {
			t = t.add(id);
		}
		return t;
	}

	/**
	 * This allows developers to programmatically describe a Trace that should
	 * be created. {@link ITraceDescription#getTrace(StateSpace)} will then be
	 * called in order to generate the correct Trace.
	 *
	 * @param description
	 *            of the trace to be created
	 * @return Trace that is generated from the Trace Description
	 */
	public Trace getTrace(final ITraceDescription description) {
		return description.getTrace(this);
	}

	/**
	 * Takes an {@link IEvalElement} containing a predicate and returns a
	 * {@link Trace} containing only a magic operation that leads to valid state
	 * where the predicate holds.
	 *
	 * @param predicate
	 *            predicate that should hold in the valid state
	 * @return {@link Trace} containing a magic operation leading to the state.
	 */
	public Trace getTraceToState(final IEvalElement predicate) {
		FindStateCommand cmd = new FindStateCommand(this, predicate, true);
		execute(cmd);
		return getTrace(cmd);
	}

	/**
	 * Set the model that is being animated. This should only be set at the
	 * beginning of an animation. The currently supported model types are
	 * {@link ClassicalBModel}, {@link EventBModel}, or {@link CSPModel}. A
	 * StateSpace object always corresponds with exactly one model.
	 *
	 * @param model
	 *            the new model
	 * @param mainComponent
	 *            the new main component
	 */
	public void setModel(final AbstractModel model, final AbstractElement mainComponent) {
		this.model = model;
		this.mainComponent = mainComponent;
	}

	/**
	 * Returns the specified model for the given StateSpace.
	 *
	 * @return the {@link AbstractModel} that represents the model for the given
	 *         StateSpace instance
	 */
	public AbstractModel getModel() {
		return model;
	}

	public AbstractElement getMainComponent() {
		return mainComponent;
	}

	/**
	 * This method allows the conversion of the StateSpace to a Model or a
	 * Trace. This corresponds to the Groovy operator "as". The user convert a
	 * StateSpace to an {@link AbstractModel}, {@link EventBModel},
	 * {@link ClassicalBModel}, or {@link CSPModel}. If they specify the class
	 * {@link Trace}, a new Trace object will be created and returned.
	 *
	 * @param clazz
	 *            the class to convert to
	 * @return the Model or Trace corresponding to the StateSpace instance
	 */
	public Object asType(final Class<?> clazz) {
		if (clazz == AbstractModel.class) {
			return model;
		}
		if (clazz.equals(model.getClass())) {
			return model;
		}
		if (clazz == Trace.class) {
			return new Trace(this);
		}
		throw new ClassCastException("An element of class " + clazz + " was not found");
	}

	/**
	 * Takes a collection of transitions and retrieves any information that
	 * needs to be retrieved (i.e. parameters, return values, etc.) if the
	 * transitions have not yet been evaluated
	 * ({@link Transition#isEvaluated()}).
	 *
	 * @param transitions
	 *            the transitions to be evaluated
	 * @param expansion
	 *            how formulas should be expanded
	 * @return a set containing all of the evaluated transitions
	 */
	public Set<Transition> evaluateTransitions(final Collection<Transition> transitions,
			final FormulaExpand expansion) {
		GetOpsFromIds cmd = new GetOpsFromIds(transitions, expansion);
		execute(cmd);
		return new LinkedHashSet<>(transitions);
	}

	/**
	 * Evaluates all of the formulas for every specified state (if they can be
	 * evaluated). Internally calls {@link #canBeEvaluated(State)}. If the
	 * formulas are of interest to a class (i.e. the an object has subscribed to
	 * the formula) the formula is cached.
	 *
	 * @param states
	 *            for which the formula is to be evaluated
	 * @param formulas
	 *            which are to be evaluated
	 * @return a map of the formulas and their results for all of the specified
	 *         states
	 */
	public Map<State, Map<IEvalElement, AbstractEvalResult>> evaluateForGivenStates(final Collection<State> states,
			final List<IEvalElement> formulas) {
		Map<State, Map<IEvalElement, AbstractEvalResult>> result = new HashMap<>();
		List<EvaluationCommand> cmds = new ArrayList<>();

		for (State stateId : states) {
			if (stateId.isInitialised()) {
				Map<IEvalElement, AbstractEvalResult> res = new HashMap<>();
				result.put(stateId, res);

				// Check for cached values
				Map<IEvalElement, AbstractEvalResult> map = stateId.getValues();
				for (IEvalElement f : formulas) {
					if (map.containsKey(f)) {
						res.put(f, map.get(f));
					} else {
						cmds.add(f.getCommand(stateId));
					}
				}

			}
		}

		execute(new ComposedCommand(cmds));

		for (EvaluationCommand efCmd : cmds) {
			IEvalElement formula = efCmd.getEvalElement();
			AbstractEvalResult value = efCmd.getValue();
			State id = addState(efCmd.getStateId());
			result.get(id).put(formula, value);
		}
		return result;
	}

	@Override
	public void kill() {
		killed = true;
		animator.kill();
	}

	public boolean isKilled() {
		return killed;
	}

	@Override
	public long getTotalNumberOfErrors() {
		return animator.getTotalNumberOfErrors();
	}

}
