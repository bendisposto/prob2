package de.prob.statespace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.IAnimator;
import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.CheckInitialisationStatusCommand;
import de.prob.animator.command.CheckInvariantStatusCommand;
import de.prob.animator.command.ComposedCommand;
import de.prob.animator.command.EvaluateFormulasCommand;
import de.prob.animator.command.ExploreStateCommand;
import de.prob.animator.command.GetOperationByPredicateCommand;
import de.prob.animator.command.GetOpsFromIds;
import de.prob.animator.command.GetStatesFromPredicate;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.exception.ProBError;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.Machine;
import de.prob.model.representation.Variable;
import de.prob.scripting.CSPModel;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;

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

	private transient IAnimator animator;

	private AbstractCommand loadcmd;

	private final HashSet<StateId> explored = new HashSet<StateId>();
	private final HashSet<StateId> initializedStates = new HashSet<StateId>();
	private final HashSet<StateId> cannotBeEvaluated = new HashSet<StateId>();

	private final HashMap<IEvalElement, Set<Object>> formulaRegistry = new HashMap<IEvalElement, Set<Object>>();

	private final Set<IStatesCalculatedListener> stateSpaceListeners = new HashSet<IStatesCalculatedListener>();

	private final HashMap<String, OpInfo> ops = new HashMap<String, OpInfo>();
	private long lastCalculatedStateId;
	private AbstractModel model;
	private final Map<StateId, Map<IEvalElement, EvaluationResult>> values = new HashMap<StateId, Map<IEvalElement, EvaluationResult>>();

	private final HashSet<StateId> invariantOk = new HashSet<StateId>();
	private final HashSet<StateId> invariantKo = new HashSet<StateId>();
	private final HashSet<StateId> timeoutOccured = new HashSet<StateId>();
	private final HashMap<StateId, Set<String>> operationsWithTimeout = new HashMap<StateId, Set<String>>();

	@Inject
	public StateSpace(final IAnimator animator,
			final DirectedMultigraphProvider graphProvider) {
		super(graphProvider.get());
		this.animator = animator;
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

		final ExploreStateCommand command = new ExploreStateCommand(
				state.getId());
		try {
			animator.execute(command);
			extractInformation(state, command);

			explored.add(state);

			if (!state.getId().equals("root")) {
				updateLastCalculatedStateId(state.numericalId());
			}
			final List<OpInfo> enabledOperations = command
					.getEnabledOperations();

			List<OpInfo> newOps = new ArrayList<OpInfo>();
			for (final OpInfo op : enabledOperations) {
				if (!containsEdge(op)) {
					ops.put(op.id, op);
					newOps.add(op);

					final StateId newState = new StateId(op.dest, this);
					addVertex(newState);
					addEdge(op, getVertex(op.src), getVertex(op.dest));
				}
			}
			evaluateFormulas(state);
			notifyStateSpaceChange(newOps);
		} catch (ProBError e) {
			if (state == getRoot()) {
				explored.add(state);
				OpInfo op = new OpInfo("FAIL", "NO INITIALIZATION FOUND",
						state.getId(), state.getId(),
						Collections.<String> emptyList(), "");
				ops.put(op.id, op);
				addEdge(op, state, state);
			}
		}
		return toString();

	}

	private void extractInformation(final StateId state,
			final ExploreStateCommand command) {
		operationsWithTimeout.put(state, command.getOperationsWithTimeout());
		if (command.isInvariantOk()) {
			invariantOk.add(state);
		} else {
			invariantKo.add(state);
		}

		if (command.isTimeoutOccured()) {
			timeoutOccured.add(state);
		}
		if (command.isInitialised()) {
			initializedStates.add(state);
		} else {
			cannotBeEvaluated.add(state);
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
	 * added to the graph.
	 * 
	 * @param stateId
	 * @param name
	 * @param predicate
	 * @param nrOfSolutions
	 * @return list of operations
	 * @throws BException
	 */
	public List<OpInfo> opFromPredicate(final StateId stateId,
			final String name, final String predicate, final int nrOfSolutions)
			throws BException {
		final ClassicalB pred = new ClassicalB(predicate);
		final GetOperationByPredicateCommand command = new GetOperationByPredicateCommand(
				stateId.getId(), name, pred, nrOfSolutions);
		animator.execute(command);
		final List<OpInfo> newOps = command.getOperations();
		updateLastCalculatedStateId(stateId.numericalId());

		List<OpInfo> toNotify = new ArrayList<OpInfo>();
		// (id,name,src,dest,args)
		for (final OpInfo op : newOps) {

			StateId vertex = getVertex(op.dest);
			if (vertex == null) {
				vertex = new StateId(op.dest, this);
				addVertex(vertex);
			}
			if (!containsEdge(op)) {
				ops.put(op.id, op);
				toNotify.add(op);
				addEdge(op, getVertex(op.src), vertex);
			}
		}
		notifyStateSpaceChange(toNotify);
		return newOps;
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
		if (invariantKo.contains(state)) {
			return true;
		}
		if (invariantOk.contains(state)) {
			return false;
		}

		if (!isExplored(state)) {
			explore(state);
		}
		return !invariantOk.contains(state);
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
	 * @return list of {@link EvaluationResult} objects for the given stateId
	 *         and code
	 */
	public List<EvaluationResult> eval(final StateId stateId,
			final List<IEvalElement> code) {
		if (!containsVertex(stateId)) {
			throw new IllegalArgumentException("state does not exist");
		}
		if (code.isEmpty()) {
			return new ArrayList<EvaluationResult>();
		}

		// Check to see if there are any cached results for the given StateId
		Map<IEvalElement, EvaluationResult> map = values.get(stateId);
		if (map == null) {
			map = new HashMap<IEvalElement, EvaluationResult>();
		}

		// Filter out any EvalElements that have already been calculated
		Set<IEvalElement> calculated = map.keySet();
		List<IEvalElement> toEval = new ArrayList<IEvalElement>();
		for (IEvalElement iEvalElement : code) {
			if (!calculated.contains(iEvalElement)) {
				toEval.add(iEvalElement);
			}
		}

		// If there are formulas for which no value has been calculated, send
		// them to prolog to get the results
		List<EvaluationResult> fromProlog;
		if (!toEval.isEmpty()) {
			final EvaluateFormulasCommand command = new EvaluateFormulasCommand(
					toEval, stateId.getId());
			execute(command);

			fromProlog = command.getValues();
		} else {
			fromProlog = new ArrayList<EvaluationResult>();
		}

		// Merge the calculated results from Prolog with the cached results for
		// the desired list
		final List<EvaluationResult> values = new ArrayList<EvaluationResult>();
		for (IEvalElement iEvalElement : code) {
			if (calculated.contains(iEvalElement)) {
				values.add(map.get(iEvalElement));
			} else {
				values.add(fromProlog.get(toEval.indexOf(iEvalElement)));
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
		final Set<IEvalElement> formulas = formulaRegistry.keySet();
		final List<IEvalElement> toEvaluate = new ArrayList<IEvalElement>();
		Map<IEvalElement, EvaluationResult> valueMap = new HashMap<IEvalElement, EvaluationResult>();

		// Check to see which formulas have subscribers. These are the ones that
		// will be calculated
		for (final IEvalElement iEvalElement : formulas) {
			if (!formulaRegistry.get(iEvalElement).isEmpty()) {
				toEvaluate.add(iEvalElement);
			}
		}
		final List<EvaluationResult> results = eval(state, toEvaluate);

		assert results.size() == toEvaluate.size();
		if (results != null) {
			for (int i = 0; i < results.size(); i++) {
				valueMap.put(toEvaluate.get(i), results.get(i));
			}
		}
		values.put(state, valueMap);
	}

	/**
	 * Calculates the registered formulas at the given state and returns the
	 * cached values
	 * 
	 * @param stateId
	 * @return map from {@link IEvalElement} object to {@link EvaluationResult}
	 *         objects
	 */
	public Map<IEvalElement, EvaluationResult> valuesAt(final StateId stateId) {
		if (canBeEvaluated(stateId)) {
			evaluateFormulas(stateId);
		}
		if (values.containsKey(stateId)) {
			return values.get(stateId);
		}
		return new HashMap<IEvalElement, EvaluationResult>();
	}

	public boolean canBeEvaluated(final StateId stateId) {
		if (cannotBeEvaluated.contains(stateId)) {
			return false;
		}
		if (initializedStates.contains(stateId)) {
			return true;
		}
		CheckInitialisationStatusCommand cmd = new CheckInitialisationStatusCommand(
				stateId.getId());
		execute(cmd);
		boolean result = cmd.getResult();
		if (result) {
			initializedStates.add(stateId);
		}
		return result;
	}

	/**
	 * If a class is interested in having a particular formula calculated and
	 * cached whenever a new state is explored, then they "subscribe" to that
	 * formula with a reference to themselves.
	 * 
	 * @param subscriber
	 * @param formulaOfInterest
	 */
	public void subscribe(final Object subscriber,
			final IEvalElement formulaOfInterest) {
		if (formulaRegistry.containsKey(formulaOfInterest)) {
			formulaRegistry.get(formulaOfInterest).add(subscriber);
		} else {
			HashSet<Object> subscribers = new HashSet<Object>();
			subscribers.add(subscriber);
			formulaRegistry.put(formulaOfInterest, subscribers);
		}
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
			final Set<Object> subscribers = formulaRegistry
					.get(formulaOfInterest);
			subscribers.remove(subscriber);
		}
	}

	// ANIMATOR

	@Override
	public void execute(final AbstractCommand command) {
		animator.execute(command);
	}

	@Override
	public void execute(final AbstractCommand... commands) {
		animator.execute(commands);
	}

	@Override
	public void sendInterrupt() {
		animator.sendInterrupt();
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
	public void notifyStateSpaceChange(final List<? extends OpInfo> newOps) {
		for (final IStatesCalculatedListener listener : stateSpaceListeners) {
			listener.newTransitions(newOps);
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
	 * @return Returns a string representation of the formulas, invariants, and
	 *         timeouts for the StateSpace. This is mainly useful for console
	 *         output and debugging.
	 */
	public String printInfo() {
		String result = "";
		result += "Formulas: \n" + values.toString() + "\n";
		result += "Invariants Ok: \n  " + invariantOk.toString() + "\n";
		result += "Timeout Occured: \n  " + timeoutOccured.toString() + "\n";
		result += "Operations With Timeout: \n  "
				+ operationsWithTimeout.toString() + "\n";
		return result;
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

	public OpInfo getOp(final String id) {
		return ops.get(id).ensureEvaluated(this);
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
		Set<String> withTO = operationsWithTimeout.get(state);

		sb.append("Operations: \n");
		for (final OpInfo opId : opIds) {
			sb.append("  " + opId.id + ": " + opId.getRep(model));
			if (withTO.contains(opId.id)) {
				sb.append(" (WITH TIMEOUT)");
			}
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
		Map<IEvalElement, EvaluationResult> currentState = values.get(state);
		if (currentState != null) {
			final Set<Entry<IEvalElement, EvaluationResult>> entrySet = currentState
					.entrySet();
			for (final Entry<IEvalElement, EvaluationResult> entry : entrySet) {
				sb.append("  " + entry.getKey().getCode() + " -> "
						+ entry.getValue().toString() + "\n");
			}
		}
		sb.append("\nINVARIANT: ");
		if (invariantOk.contains(state)) {
			sb.append(" OK\n");
		} else {
			sb.append(" KO\n");
		}
		if (timeoutOccured.contains(state)) {
			sb.append("\nTIMEOUT OCCURED\n");
		}
		return sb.toString();
	}

	/**
	 * This calculated the shortest path from root to the specified state
	 * (specified with an integer id value).
	 * 
	 * @param state
	 * @return trace in the form of a {@link Trace} object
	 */
	public Trace getTrace(final String state) {
		final StateId id = getVertex(state);
		StateId root = this.getRoot();

		DijkstraShortestPath<StateId, OpInfo> dijkstra = new DijkstraShortestPath<StateId, OpInfo>(
				this.getGraph());
		List<OpInfo> path = dijkstra.getPath(root, id);
		Trace h = new Trace(this);
		for (final OpInfo opInfo : path) {
			h = h.add(opInfo.getId());
		}
		return h;
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

		Set<Machine> machines = model.getChildrenOfType(Machine.class);
		for (Machine machine : machines) {
			for (Variable variable : machine.getChildrenOfType(Variable.class)) {
				subscribe(this, variable.getExpression());
			}
		}
	}

	/**
	 * Returns the specified model for the given StateSpace
	 * 
	 * @return the {@link AbstractModel} that represents the model for the given
	 *         StateSpace instance
	 */
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
	 * objects specified by either a String or an integer identifier. This maps
	 * to a groovy operator so that in the console users can type
	 * variableOfTypeStateSpace[stateId] and receive the corresponding StateId
	 * back. An IllegalArgumentException is thrown if the specified id is
	 * unknown.
	 * 
	 * @throws IllegalArgumentException
	 * @param that
	 * @return {@link StateId} for the specified id
	 */
	public Object getAt(final Object that) {
		StateId id = null;
		if (that instanceof String) {
			id = getVertex((String) that);
		}
		if (that instanceof Integer) {
			id = getVertex(String.valueOf(that));
		}
		if (id != null) {
			return id;
		}
		throw new IllegalArgumentException(
				"StateSpace does not contain vertex " + that);
	}

	@Override
	public Set<OpInfo> getOutEdges(final StateId arg0) {
		Collection<OpInfo> outgoingEdgesOf = super.getOutEdges(arg0);
		for (OpInfo opInfo : outgoingEdgesOf) {
			opInfo.ensureEvaluated(this);
		}
		return new HashSet<OpInfo>(outgoingEdgesOf);
	}

	public long getLastCalculatedStateId() {
		return lastCalculatedStateId;
	}

	public void updateLastCalculatedStateId(final long lastCalculatedId) {
		lastCalculatedStateId = Math.max(lastCalculatedStateId,
				lastCalculatedId);
	}

	@Override
	public StateSpaceGraph getSSGraph() {
		return this;
	}

	public Set<StateId> getInvariantOk() {
		return invariantOk;
	}

	public HashSet<StateId> getInvariantKo() {
		return invariantKo;
	}

	public Set<StateId> checkInvariants() {
		Collection<StateId> vertices = getVertices();
		List<CheckInvariantStatusCommand> cmds = new ArrayList<CheckInvariantStatusCommand>();
		for (StateId stateId : vertices) {
			if (!invariantOk.contains(stateId)
					&& !invariantKo.contains(stateId)) {
				cmds.add(new CheckInvariantStatusCommand(stateId.getId()));
			}
		}
		execute(new ComposedCommand(cmds));
		for (CheckInvariantStatusCommand cmd : cmds) {
			if (!cmd.isInvariantViolated()) {
				invariantOk.add(states.get(cmd.getStateId()));
			}
		}
		return invariantOk;
	}

	public Set<StateId> checkInitialized() {
		Collection<StateId> vertices = getVertices();
		List<CheckInitialisationStatusCommand> cmds = new ArrayList<CheckInitialisationStatusCommand>();
		for (StateId stateId : vertices) {
			if (!initializedStates.contains(stateId)
					&& !cannotBeEvaluated.contains(stateId)) {
				cmds.add(new CheckInitialisationStatusCommand(stateId.getId()));
			}
		}
		execute(new ComposedCommand(cmds));
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

	public Map<StateId, Map<IEvalElement, EvaluationResult>> calculateVariables() {
		checkInitialized();
		List<IEvalElement> toEval = new ArrayList<IEvalElement>();
		Set<Machine> machines = model.getChildrenOfType(Machine.class);
		for (Machine machine : machines) {
			for (Variable variable : machine.getChildrenOfType(Variable.class)) {
				toEval.add(variable.getExpression());
			}
		}

		Collection<StateId> vertices = getVertices();
		List<EvaluateFormulasCommand> cmds = new ArrayList<EvaluateFormulasCommand>();
		for (StateId stateId : vertices) {
			if (initializedStates.contains(stateId)
					&& !values.containsKey(stateId)) {
				cmds.add(new EvaluateFormulasCommand(toEval, stateId.getId()));
			}
		}

		execute(new ComposedCommand(cmds));
		for (EvaluateFormulasCommand cmd : cmds) {
			Map<IEvalElement, EvaluationResult> map = new HashMap<IEvalElement, EvaluationResult>();
			List<EvaluationResult> vs = cmd.getValues();
			for (EvaluationResult eR : vs) {
				map.put(toEval.get(vs.indexOf(eR)), eR);
			}
			values.put(states.get(cmd.getStateId()), map);
		}

		return values;
	}

	public Map<StateId, Map<IEvalElement, EvaluationResult>> getValues() {
		return values;
	}

}
