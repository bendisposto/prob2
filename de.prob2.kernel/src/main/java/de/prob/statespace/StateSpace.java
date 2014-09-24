package de.prob.statespace;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.Provider;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.IAnimator;
import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.CheckInitialisationStatusCommand;
import de.prob.animator.command.CheckInvariantStatusCommand;
import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.EvaluateRegisteredFormulasCommand;
import de.prob.animator.command.EvaluationCommand;
import de.prob.animator.command.ExploreStateCommand;
import de.prob.animator.command.FindValidStateCommand;
import de.prob.animator.command.GetOperationByPredicateCommand;
import de.prob.animator.command.GetOpsFromIds;
import de.prob.animator.command.GetShortestTraceCommand;
import de.prob.animator.command.GetStatesFromPredicate;
import de.prob.animator.command.IStateSpaceModifier;
import de.prob.animator.command.RegisterFormulaCommand;
import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.exception.ProBError;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.CSPModel;

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
 * The implementation of the StateSpace is as a {@link StateSpaceGraph} with
 * {@link StateId}s as vertices and {@link OpInfo}s as edges. Therefore, some
 * basic graph functionalities are provided.
 * 
 * @author joy
 * 
 */
public class StateSpace extends StateSpaceGraph implements IStateSpace {

	Logger logger = LoggerFactory.getLogger(StateSpace.class);
	private transient IAnimator animator;

	private AbstractCommand loadcmd;

	private final HashSet<StateId> explored = new HashSet<StateId>();
	private final HashSet<StateId> cannotBeEvaluated = new HashSet<StateId>();

	private final HashMap<IEvalElement, WeakHashMap<Object, Object>> formulaRegistry = new HashMap<IEvalElement, WeakHashMap<Object, Object>>();
	private final Set<IEvalElement> subscribedFormulas = new HashSet<IEvalElement>();

	private final Set<IStatesCalculatedListener> stateSpaceListeners = new HashSet<IStatesCalculatedListener>();

	private long lastCalculatedStateId;
	private AbstractModel model;
	private final Map<StateId, Map<IEvalElement, IEvalResult>> values = new HashMap<StateId, Map<IEvalElement, IEvalResult>>();

	@Inject
	public StateSpace(final Provider<IAnimator> panimator,
			final DirectedMultigraphProvider graphProvider) {
		super(graphProvider.get());
		animator = panimator.get();
		lastCalculatedStateId = -1;
	}

	public StateId getRoot() {
		return getState(__root);
	}

	// MAKE CHANGES TO THE STATESPACE GRAPH
	/**
	 * Takes a {@link StateId} and calculates the successor states, the
	 * invariant, timeout, and the operations with a timeout and caches these
	 * for the given stateId.
	 * 
	 * @param state
	 */
	public String explore(final StateId state) {
		if (!containsVertex(state)) {
			throw new IllegalArgumentException("state " + state
					+ " does not exist");
		}
		final ExploreStateCommand command = new ExploreStateCommand(this,
				state.getId(), subscribedFormulas);

		try {
			execute(command);
			extractInformation(state, command);
			explored.add(state);
		} catch (ProBError e) {
			if (state.equals(__root)) {
				explored.add(state);
				OpInfo op = OpInfo.generateArtificialTransition(this, "FAIL",
						"NO INITIALIZATION OR VALID CONSTANTS FOUND",
						state.getId(), state.getId());
				ops.put(op.getId(), op);
				addEdge(op, state, state);
			} else {
				throw e;
			}
		}
		return "";
	}

	private void extractInformation(final StateId state,
			final ExploreStateCommand command) {
		if (!command.isInitialised()) {
			cannotBeEvaluated.add(state);
		}

		Map<IEvalElement, IEvalResult> res = command.getFormulaResults();
		if (values.containsKey(state)) {
			Map<IEvalElement, IEvalResult> map = values.get(state);
			map.putAll(res);
		} else {
			values.put(state, res);
		}
	}

	public String explore(final String state) {
		return explore(getVertex(state));
	}

	public String explore(final int i) {
		if (i == -1) {
			return explore("root");
		}
		final String si = String.valueOf(i);
		return explore(si);
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
	 * Checks to see if the specified state has already been explored. If not,
	 * the state is explored and in either case, the corresponding StateId is
	 * returned
	 * 
	 * @param state
	 * @return explored StateId
	 */
	public StateId getState(final StateId state) {
		if (!isExplored(state)) {
			explore(state);
		}
		return state;
	}

	/**
	 * Get the target State for given operation, explore it, and return it.
	 * 
	 * @param op
	 * @return explored target StateId for op
	 */
	public StateId getState(final OpInfo op) {
		final StateId edgeTarget = getDest(op);
		if (!isExplored(edgeTarget)) {
			explore(edgeTarget);
		}
		return edgeTarget;
	}

	public List<StateId> getStatesFromPredicate(final IEvalElement predicate) {
		GetStatesFromPredicate cmd = new GetStatesFromPredicate(predicate);
		execute(cmd);
		List<String> ids = cmd.getIds();
		List<StateId> sIds = new ArrayList<StateId>();
		for (String s : ids) {
			sIds.add(getVertex(s));
		}
		return sIds;
	}

	/**
	 * Takes the name of an operation and a predicate and finds Operations that
	 * satisfy the name and predicate at the given stateId. New Operations are
	 * added to the graph. This is only valid for ClassicalB predicates.
	 * 
	 * @param stateId
	 *            {@link StateId} from which the operation should be found
	 * @param name
	 *            name of the operation that should be executed
	 * @param predicate
	 *            an additional guard for the operation. This usually describes
	 *            the parameters
	 * @param nrOfSolutions
	 *            int number of solutions that should be found for the given
	 *            predicate
	 * @return list of operations calculated by ProB
	 * @throws BException
	 */
	public List<OpInfo> opFromPredicate(final StateId stateId,
			final String name, final String predicate, final int nrOfSolutions)
			throws IllegalArgumentException {
		final ClassicalB pred = new ClassicalB(predicate);
		final GetOperationByPredicateCommand command = new GetOperationByPredicateCommand(
				this, stateId.getId(), name, pred, nrOfSolutions);
		execute(command);
		if (command.hasErrors()) {
			throw new IllegalArgumentException("Executing operation " + name
					+ " with predicate " + predicate + " produced errors: "
					+ Joiner.on(", ").join(command.getErrors()));
		}
		return command.getNewTransitions();
	}

	/**
	 * Tests to see if a combination of an operation name and a predicate is
	 * valid from a given state.
	 * 
	 * @param stateId
	 *            {@link StateId} id for state to test
	 * @param name
	 *            {@link String} name of operation
	 * @param predicate
	 *            {@link String} predicate to test
	 * @return true, if the operation is valid from the given state. False
	 *         otherwise.
	 */
	public boolean isValidOperation(final StateId stateId, final String name,
			final String predicate) {
		final ClassicalB pred = new ClassicalB(predicate);
		GetOperationByPredicateCommand command = new GetOperationByPredicateCommand(
				this, stateId.getId(), name, pred, 1);
		execute(command);
		return !command.hasErrors()
				&& (command.getNewTransitions().size() == 1);
	}

	/**
	 * Checks if the state with stateId is a deadlock
	 * 
	 * @param state
	 * @return returns if a specific state is deadlocked
	 */
	public boolean isDeadlock(final StateId state) {
		if (!isExplored(state)) {
			explore(state);
		}
		return outDegree(state) == 0;
	}

	/**
	 * Checks to see if the specified state has violated the invariant
	 * 
	 * @param state
	 * @return true if state has an invariant violation. False otherwise.
	 */
	public boolean hasInvariantViolation(final StateId state) {
		if (!isExplored(state)) {
			explore(state);
		}
		CheckInvariantStatusCommand cmd = new CheckInvariantStatusCommand(
				state.getId());
		execute(cmd);
		return cmd.isInvariantViolated();
	}

	/**
	 * Checks if the state with stateId has been explored yet
	 * 
	 * @param state
	 * @return returns if a specific state is explored
	 */
	public boolean isExplored(final StateId state) {
		if (!containsVertex(state)) {
			throw new IllegalArgumentException("Unknown State id");
		}
		return explored.contains(state);
	}

	public HashSet<StateId> getExplored() {
		return explored;
	}

	// EVALUATE FORMULAS

	/**
	 * The method eval takes a stateId and a list of formulas (
	 * {@link IEvalElement}) and returns a list of EvaluationResults for the
	 * given formulas. It first checks to see if any of the formulas have cached
	 * values for the given state and then, if there are formulas that have not
	 * yet been calculated, it contacts Prolog to get the remaining values.
	 * 
	 * @param stateId
	 * @param code
	 * @return list of {@link IEvalResult} objects for the given stateId and
	 *         code
	 */
	public List<IEvalResult> eval(final StateId stateId,
			final List<IEvalElement> code) {
		if (!containsVertex(stateId)) {
			throw new IllegalArgumentException("state does not exist");
		}
		if (code.isEmpty()) {
			return new ArrayList<IEvalResult>();
		}

		// Check to see if there are any cached results for the given StateId
		Map<IEvalElement, IEvalResult> map = values.get(stateId);
		if (map == null) {
			map = new HashMap<IEvalElement, IEvalResult>();
		}

		// Filter out any EvalElements that have already been calculated
		Set<IEvalElement> calculated = map.keySet();
		List<EvaluationCommand> toEval = new ArrayList<EvaluationCommand>();
		for (IEvalElement iEvalElement : code) {
			if (!calculated.contains(iEvalElement)) {
				toEval.add(iEvalElement.getCommand(stateId));
			}
		}

		// If there are formulas for which no value has been calculated, send
		// them to prolog to get the results
		List<IEvalResult> fromProlog = new ArrayList<IEvalResult>();
		if (!toEval.isEmpty()) {
			final ComposedCommand command = new ComposedCommand(toEval);
			execute(command);

			for (EvaluationCommand acmd : toEval) {
				fromProlog.add(acmd.getValue());
			}
		}

		// Merge the calculated results from Prolog with the cached results for
		// the desired list
		final List<IEvalResult> values = new ArrayList<IEvalResult>();
		for (IEvalElement iEvalElement : code) {
			if (calculated.contains(iEvalElement)) {
				values.add(map.get(iEvalElement));
			} else {
				values.add(fromProlog.get(code.indexOf(iEvalElement)));
			}
		}

		return values;

	}

	/**
	 * The method evaluateFormulas calculates all of the subscribed formulas for
	 * the given state and caches them.
	 * 
	 * @param state
	 */
	private void evaluateFormulas(final StateId state) {
		if (!canBeEvaluated(state)) {
			return;
		}

		EvaluateRegisteredFormulasCommand cmd = new EvaluateRegisteredFormulasCommand(
				state.getId(), subscribedFormulas);

		execute(cmd);
		Map<IEvalElement, IEvalResult> results = cmd.getResults();

		if (values.containsKey(state)) {
			values.get(state).putAll(results);
		} else {
			values.put(state, results);
		}
	}

	/**
	 * Calculates the registered formulas at the given state and returns the
	 * cached values
	 * 
	 * @param stateId
	 * @return map from {@link IEvalElement} object to {@link IEvalResult}
	 *         objects
	 */
	public Map<IEvalElement, IEvalResult> valuesAt(final StateId stateId) {
		if (values.containsKey(stateId)
				&& values.get(stateId).keySet().size() == subscribedFormulas
						.size()) {
			return values.get(stateId);
		}
		if (canBeEvaluated(stateId)) {
			evaluateFormulas(stateId);
			return values.get(stateId);
		}
		return new HashMap<IEvalElement, IEvalResult>();
	}

	public boolean canBeEvaluated(final StateId stateId) {
		if (cannotBeEvaluated.contains(stateId)) {
			return false;
		}
		if (explored.contains(stateId)) {
			return true;
		}
		CheckInitialisationStatusCommand cmd = new CheckInitialisationStatusCommand(
				stateId.getId());
		execute(cmd);
		boolean initialized = cmd.isInitialized();
		if (!initialized) {
			cannotBeEvaluated.add(stateId);
		}
		return initialized;
	}

	public void subscribe(final Object subscriber,
			final List<IEvalElement> formulasOfInterest) {
		List<AbstractCommand> subscribeCmds = new ArrayList<AbstractCommand>();
		for (IEvalElement formulaOfInterest : formulasOfInterest) {
			if (formulaOfInterest instanceof CSP) {
				logger.info(
						"CSP formula {} not subscribed because CSP evaluation is not state based. Use eval method instead",
						formulaOfInterest.getCode());
			} else {
				if (formulaRegistry.containsKey(formulaOfInterest)) {
					formulaRegistry.get(formulaOfInterest).put(subscriber,
							new WeakReference<Object>(formulaOfInterest));
					subscribedFormulas.add(formulaOfInterest);
				} else {
					WeakHashMap<Object, Object> subscribers = new WeakHashMap<Object, Object>();
					subscribers.put(subscriber, new WeakReference<Object>(
							subscriber));
					formulaRegistry.put(formulaOfInterest, subscribers);
					subscribeCmds.add(new RegisterFormulaCommand(
							formulaOfInterest));
					subscribedFormulas.add(formulaOfInterest);
				}
			}
		}
		execute(new ComposedCommand(subscribeCmds));
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
	 * @param formulaOfInterest
	 */
	public void subscribe(final Object subscriber,
			final IEvalElement formulaOfInterest) {
		if (formulaOfInterest instanceof CSP) {
			logger.info(
					"CSP formula {} not subscribed because CSP evaluation is not state based. Use eval method instead",
					formulaOfInterest.getCode());
			return;
		}

		if (formulaRegistry.containsKey(formulaOfInterest)) {
			formulaRegistry.get(formulaOfInterest).put(subscriber,
					new WeakReference<Object>(subscriber));
		} else {
			execute(new RegisterFormulaCommand(formulaOfInterest));
			WeakHashMap<Object, Object> subscribers = new WeakHashMap<Object, Object>();
			subscribers.put(subscriber, new WeakReference<Object>(subscriber));
			formulaRegistry.put(formulaOfInterest, subscribers);
		}
		if (!subscribedFormulas.contains(formulaOfInterest)) {
			subscribedFormulas.add(formulaOfInterest);
		}
	}

	public boolean isSubscribed(final IEvalElement formula) {
		return formulaRegistry.containsKey(formula)
				&& !formulaRegistry.get(formula).isEmpty();
	}

	/**
	 * If a subscribed class is no longer interested in the value of a
	 * particular formula, then they can unsubscribe to that formula
	 * 
	 * @param subscriber
	 * @param formulaOfInterest
	 */
	public void unsubscribe(final Object subscriber,
			final IEvalElement formulaOfInterest) {
		if (formulaRegistry.containsKey(formulaOfInterest)) {
			final WeakHashMap<Object, Object> subscribers = formulaRegistry
					.get(formulaOfInterest);
			subscribers.remove(subscriber);
			if (subscribers.isEmpty()) {
				subscribedFormulas.remove(formulaOfInterest);
			}
		}
	}

	// ANIMATOR
	@Override
	public void sendInterrupt() {
		animator.sendInterrupt();
	}

	@Override
	public void execute(final AbstractCommand command) {
		animator.execute(command);
		addTransitions(new AbstractCommand[] { command });
	}

	@Override
	public void execute(final AbstractCommand... commands) {
		animator.execute(commands);
		addTransitions(commands);
	}

	private void addTransitions(final AbstractCommand[] commands) {

		List<OpInfo> toNotify = new ArrayList<OpInfo>();
		long last = lastCalculatedStateId;

		for (AbstractCommand cmd : commands) {
			if (cmd instanceof ComposedCommand) {
				List<AbstractCommand> subcommands = ((ComposedCommand) cmd)
						.getSubcommands();
				addTransitions(subcommands
						.toArray(new AbstractCommand[subcommands.size()]));
			} else if (cmd instanceof IStateSpaceModifier) {
				List<OpInfo> newOps = ((IStateSpaceModifier) cmd)
						.getNewTransitions();
				for (final OpInfo op : newOps) {
					if (!containsEdge(op)) {
						StateId src = op.getSrcId();
						if (!containsVertex(src)) {
							addVertex(src);
						}

						last = Math.max(last, src.numericalId());

						StateId dest = op.getDestId();
						if (!containsVertex(dest)) {
							addVertex(dest);
						}

						toNotify.add(op);
						addEdge(op, src, dest);
					}
				}
			}
		}
		updateLastCalculatedStateId(last);

		if (!toNotify.isEmpty()) {
			notifyStateSpaceChange(toNotify);
		}
	}

	// NOTIFICATION SYSTEM

	/**
	 * Adds an IStateSpaceChangeListener to the list of StateSpaceListeners.
	 * This listener will be notified whenever a new operation or a new state is
	 * added to the graph.
	 * 
	 * @param l
	 */
	@Override
	public void registerStateSpaceListener(final IStatesCalculatedListener l) {
		stateSpaceListeners.add(l);
	}

	@Override
	public void deregisterStateSpaceListener(final IStatesCalculatedListener l) {
		stateSpaceListeners.remove(l);
	}

	@Override
	public void notifyStateSpaceChange(final List<OpInfo> newOps) {
		for (final IStatesCalculatedListener listener : stateSpaceListeners) {
			if (!animator.isBusy()) {
				listener.newTransitions(newOps);
			}
		}
	}

	// METHODS TO MAKE THE INTERACTION WITH THE GROOVY SHELL EASIER
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.statespace.StateSpaceGraph#toString()
	 */
	@Override
	public String toString() {
		return super.toString();
	}

	/**
	 * Get the the map of String ids to their corresponding {@link StateId}
	 * 
	 * @return Map from String to {@link StateId}
	 */
	public HashMap<String, StateId> getStates() {
		return states;
	}

	/**
	 * Get the map of String ids to their corresponding {@link OpInfo}
	 * 
	 * @return Map from String to {@link OpInfo}
	 */
	public HashMap<String, OpInfo> getOps() {
		return ops;
	}

	/**
	 * This method finds the specified {@link OpInfo} AND ensures that the
	 * {@link OpInfo} has been evaluated as a side effect.
	 * 
	 * @param id
	 *            String operation id
	 * @return {@link OpInfo} specified by the id parameter
	 */
	public OpInfo getEvaluatedOpInfo(final String id) {
		return ops.get(id).evaluate();
	}

	/**
	 * @param state
	 * @return Returns a String representation of the operations available from
	 *         the specified {@link StateId}. This is mainly useful for console
	 *         output.
	 */
	public String printOps(final StateId state) {
		final StringBuilder sb = new StringBuilder();
		final Collection<OpInfo> opIds = getOutEdges(state);

		sb.append("Operations: \n");
		for (final OpInfo opId : opIds) {
			sb.append("  " + opId.getId() + ": " + opId.getRep());
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * @param state
	 * @return Returns a String representation of the information about the
	 *         state with the specified {@link StateId}. This includes the id
	 *         for the state, the cached calculated values, and if an invariant
	 *         violation or a timeout has occured for the given state. This is
	 *         mainly useful for console output.
	 */
	public String printState(final StateId state) {
		final StringBuilder sb = new StringBuilder();

		explore(state);

		sb.append("STATE: " + state + "\n\n");
		sb.append("VALUES:\n");
		Map<IEvalElement, IEvalResult> currentState = values.get(state);
		if (currentState != null) {
			final Set<Entry<IEvalElement, IEvalResult>> entrySet = currentState
					.entrySet();
			for (final Entry<IEvalElement, IEvalResult> entry : entrySet) {
				sb.append("  " + entry.getKey().getCode() + " -> "
						+ entry.getValue().toString() + "\n");
			}
		}
		sb.append("\nINVARIANT: ");
		return sb.toString();
	}

	/**
	 * This calculated the shortest path from root to the specified state. This
	 * contacts the ProB kernel via the {@link GetShortestTraceCommand} and then
	 * uses the generated of operations to generate a Trace via the
	 * {@link StateSpace#getTrace(ITraceDescription)} method.
	 * 
	 * @param stateId
	 *            StateId for which the trace through the state space should be
	 *            found.
	 * @return trace in the form of a {@link Trace} object
	 */
	public Trace getTrace(final StateId stateId) {
		GetShortestTraceCommand cmd = new GetShortestTraceCommand(this, stateId);
		execute(cmd);
		return getTrace(cmd);
	}

	/**
	 * Takes a list of {@link String} operation id names and generates a
	 * {@link Trace} by executing each one in order. This calls the
	 * {@link Trace#add(String)} method which can throw an
	 * {@link IllegalArgumentException} if executing the operations in the
	 * specified order is not possible.
	 * 
	 * @param opIds
	 *            List of operation ids in the order that they should be
	 *            executed.
	 * @return {@link Trace} generated by executing the ids.
	 */
	public Trace getTrace(final List<String> opIds) {
		Trace t = new Trace(this);
		for (String id : opIds) {
			t = t.add(id);
		}
		return t;
	}

	public Trace getTrace(final ITraceDescription description) {
		return description.getTrace(this);
	}

	/**
	 * Takes an {@link IEvalElement} containing a predicate and returns a
	 * {@link Trace} containing only a magic operation that leads to valid state
	 * where the preciate holds.
	 * 
	 * @param predicate
	 *            predicate that should hold in the valid state
	 * @return {@link Trace} containing a magic operation leading to the state.
	 */
	public Trace getTraceToState(final IEvalElement predicate) {
		FindValidStateCommand cmd = new FindValidStateCommand(this, predicate);
		execute(cmd);
		return getTrace(cmd);
	}

	public void setAnimator(final IAnimator animator) {
		this.animator = animator;
	}

	public AbstractCommand getLoadcmd() {
		return loadcmd;
	}

	public void setLoadcmd(final AbstractCommand loadcmd) {
		this.loadcmd = loadcmd;
	}

	/**
	 * Set the model that is being animated. This should only be set at the
	 * beginning of an animation. The currently supported model types are
	 * {@link ClassicalBModel}, {@link EventBModel}, or {@link CSPModel}. A
	 * StateSpace object always corresponds with exactly one model.
	 * 
	 * @param model
	 */
	public void setModel(final AbstractModel model) {
		this.model = model;
	}

	/**
	 * Returns the specified model for the given StateSpace
	 * 
	 * @return the {@link AbstractModel} that represents the model for the given
	 *         StateSpace instance
	 */
	@Override
	public AbstractModel getModel() {
		return model;
	}

	/**
	 * This method allows the conversion of the StateSpace to a Model or a
	 * Trace. This corresponds to the Groovy operator "as". The user convert a
	 * StateSpace to an {@link AbstractModel}, {@link EventBModel},
	 * {@link ClassicalBModel}, or {@link CSPModel}. If they specify the class
	 * {@link Trace}, a new Trace object will be created and returned.
	 * 
	 * @param className
	 * @return the Model or Trace corresponding to the StateSpace instance
	 */
	public Object asType(final Class<?> className) {
		if (className.getSimpleName().equals("AbstractModel")) {
			return model;
		}
		if (className.getSimpleName().equals("EventBModel")) {
			if (model instanceof EventBModel) {
				return model;
			}
		}
		if (className.getSimpleName().equals("ClassicalBModel")) {
			if (model instanceof ClassicalBModel) {
				return model;
			}
		}
		if (className.getSimpleName().equals("CSPModel")) {
			if (model instanceof CSPModel) {
				return model;
			}
		}
		if (className.getSimpleName().equals("Trace")) {
			return new Trace(this);
		}
		throw new ClassCastException("An element of class " + className
				+ " was not found");
	}

	/**
	 * This method is implemented to provide access to the {@link StateId}
	 * objects specified by an integer identifier. This maps to a groovy
	 * operator so that in the console users can type
	 * variableOfTypeStateSpace[stateId] and receive the corresponding StateId
	 * back. An IllegalArgumentException is thrown if the specified id is
	 * unknown.
	 * 
	 * @throws IllegalArgumentException
	 * @param that
	 * @return {@link StateId} for the specified id
	 */
	public Object getAt(final int sId) {
		StateId id = getVertex(String.valueOf(sId));
		if (id != null) {
			return id;
		}
		throw new IllegalArgumentException(
				"StateSpace does not contain vertex " + sId);
	}

	@Override
	public Set<OpInfo> getOutEdges(final StateId arg0) {
		Collection<OpInfo> outgoingEdgesOf = super.getOutEdges(arg0);
		return new LinkedHashSet<OpInfo>(outgoingEdgesOf);
	}

	public long getLastCalculatedStateId() {
		return lastCalculatedStateId;
	}

	public void updateLastCalculatedStateId(final long lastCalculatedId) {
		lastCalculatedStateId = Math.max(lastCalculatedStateId,
				lastCalculatedId);
	}

	public Set<StateId> checkInvariants() {
		Collection<StateId> vertices = getVertices();
		List<CheckInvariantStatusCommand> cmds = new ArrayList<CheckInvariantStatusCommand>();
		for (StateId stateId : vertices) {
			cmds.add(new CheckInvariantStatusCommand(stateId.getId()));
		}
		execute(new ComposedCommand(cmds));
		Set<StateId> invariantOk = new HashSet<StateId>();
		for (CheckInvariantStatusCommand cmd : cmds) {
			if (!cmd.isInvariantViolated()) {
				invariantOk.add(states.get(cmd.getStateId()));
			}
		}
		return invariantOk;
	}

	private Set<StateId> checkInitialized() {
		Collection<StateId> vertices = getVertices();
		List<CheckInitialisationStatusCommand> cmds = new ArrayList<CheckInitialisationStatusCommand>();
		for (StateId stateId : vertices) {
			if (!cannotBeEvaluated.contains(stateId)) {
				cmds.add(new CheckInitialisationStatusCommand(stateId.getId()));
			}
		}
		execute(new ComposedCommand(cmds));
		Set<StateId> initializedStates = new HashSet<StateId>();
		for (CheckInitialisationStatusCommand cmd : cmds) {
			if (cmd.isInitialized()) {
				initializedStates.add(states.get(cmd.getStateId()));
			} else {
				cannotBeEvaluated.add(states.get(cmd.getStateId()));
			}
		}

		return initializedStates;
	}

	public Collection<OpInfo> getEvaluatedOps() {
		Collection<OpInfo> edges = getEdges();
		GetOpsFromIds cmd = new GetOpsFromIds(edges);
		execute(cmd);
		return edges;
	}

	public Set<OpInfo> evaluateOps(final Collection<OpInfo> ops) {
		GetOpsFromIds cmd = new GetOpsFromIds(ops);
		execute(cmd);
		return new LinkedHashSet<OpInfo>(ops);
	}

	/*
	 * What this method should do: 1) Extract all states from state space 2) For
	 * each state For each formula Check to see if formula is cached. IF so,
	 * transfer this value to the result ELSE, add command to be evaluated 3)
	 * Execute all commands 4) For each command Add EvaluationResult to result
	 * IF the formula is of interest to the user (the subscribers is not empty),
	 * then cache it in values 5) return the result
	 */
	/**
	 * Evaluates all of the formulas for every given state in the state space
	 * (if they can be evaluated). Internally calls
	 * {@link #evaluateForGivenStates(Collection, List)} with
	 * {@link #getVertices()} as the parameter. If the formulas are of interest
	 * to a class (i.e. the an object has subscribed to the formula) the formula
	 * is cached.
	 * 
	 * @param formulas
	 * @return a map of the formulas and their result for every state in the
	 *         state space
	 */
	public Map<StateId, Map<IEvalElement, IEvalResult>> evaluateForEveryState(
			final List<IEvalElement> formulas) {
		return evaluateForGivenStates(getVertices(), formulas);
	}

	/**
	 * Evaluates all of the formulas for every specified state (if they can be
	 * evaluated). Internally calls {@link #canBeEvaluated(StateId)}. If the
	 * formulas are of interest to a class (i.e. the an object has subscribed to
	 * the formula) the formula is cached.
	 * 
	 * @param states
	 * @param formulas
	 * @return a map of the formulas and their results for all of the specified
	 *         states
	 */
	public Map<StateId, Map<IEvalElement, IEvalResult>> evaluateForGivenStates(
			final Collection<StateId> states, final List<IEvalElement> formulas) {
		Map<StateId, Map<IEvalElement, IEvalResult>> result = new HashMap<StateId, Map<IEvalElement, IEvalResult>>();
		List<EvaluationCommand> cmds = new ArrayList<EvaluationCommand>();
		Set<StateId> initializedStates = checkInitialized();

		for (StateId stateId : states) {
			if (initializedStates.contains(stateId)) {
				Map<IEvalElement, IEvalResult> res = new HashMap<IEvalElement, IEvalResult>();
				result.put(stateId, res);

				// Check for cached values
				Map<IEvalElement, IEvalResult> map = values.get(stateId);
				if (map == null) {
					for (IEvalElement f : formulas) {
						cmds.add(f.getCommand(stateId));
					}
				} else {
					for (IEvalElement f : formulas) {
						if (map.containsKey(f)) {
							res.put(f, map.get(f));
						} else {
							cmds.add(f.getCommand(stateId));
						}
					}
				}
			}
		}

		execute(new ComposedCommand(cmds));

		for (EvaluationCommand efCmd : cmds) {
			IEvalElement formula = efCmd.getEvalElement();
			IEvalResult value = efCmd.getValue();
			StateId id = getVertex(efCmd.getStateId());

			if (formulaRegistry.containsKey(formula)
					&& !formulaRegistry.get(formula).isEmpty()) {
				if (!values.containsKey(id)) {
					values.put(id, new HashMap<IEvalElement, IEvalResult>());
				}
				values.get(id).put(formula, value);
			}
			result.get(id).put(formula, value);
		}

		return result;
	}

	public Map<StateId, Map<IEvalElement, IEvalResult>> getValues() {
		return values;
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

}
