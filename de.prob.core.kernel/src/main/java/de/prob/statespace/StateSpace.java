package de.prob.statespace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.Main;
import de.prob.ProBException;
import de.prob.SignalHandlerImpl;
import de.prob.animator.IAnimator;
import de.prob.animator.command.EvaluateFormulasCommand;
import de.prob.animator.command.ExploreStateCommand;
import de.prob.animator.command.GetOperationByPredicateCommand;
import de.prob.animator.command.ICommand;
import de.prob.animator.domainobjects.ClassicalBEvalElement;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.OpInfo;

/**
 * 
 * The StateSpace is where the animation of a given model is carried out. The
 * methods in the StateSpace allow the user to:
 * 
 * 1) Find new states and operations
 * 
 * 2) Move between states within the StateSpace to inspect them
 * 
 * 3) Perform random animation steps
 * 
 * 4) Evaluate custom predicates and expressions -
 * 
 * 5) Register listeners that are notified of animation steps/new states and
 * operations.
 * 
 * 6) View information about the current state
 * 
 * @author joy
 * 
 */
public class StateSpace extends StateSpaceGraph implements IAnimator {

	Logger logger = LoggerFactory.getLogger(StateSpace.class);

	private transient final IAnimator animator;
	private final HashSet<StateId> explored = new HashSet<StateId>();
	private final History history;
	private final StateSpaceInfo info;

	private final List<IEvalElement> formulas = new ArrayList<IEvalElement>();
	private final List<IAnimationListener> animationListeners = new ArrayList<IAnimationListener>();
	private final List<IStateSpaceChangeListener> stateSpaceListeners = new ArrayList<IStateSpaceChangeListener>();

	private final HashMap<String, StateId> states = new HashMap<String, StateId>();

	private final Random randomGenerator;

	@Inject
	public StateSpace(final IAnimator animator,
			final DirectedMultigraphProvider graphProvider,
			final Random randomGenerator, final History history,
			final StateSpaceInfo info) {
		super(graphProvider.get());
		this.animator = animator;
		this.randomGenerator = randomGenerator;
		this.history = history;
		this.info = info;
		StateId root = new StateId("root", "1");
		addVertex(root);
		states.put(root.getId(), root);
		if (Main.isShellMode()) {
			// setUpSignalHandler();
		}
	}

	// MAKE CHANGES TO THE STATESPACE GRAPH
	/**
	 * Takes a state id and calculates the successor states, the invariant,
	 * timeout, etc.
	 * 
	 * @param state
	 * @throws ProBException
	 */
	public void explore(final StateId state) throws ProBException {
		if (!containsVertex(state))
			throw new IllegalArgumentException("state " + state
					+ " does not exist");

		ExploreStateCommand command = new ExploreStateCommand(state.getId());
		animator.execute(command);
		explored.add(state);
		List<OpInfo> enabledOperations = command.getEnabledOperations();

		for (OpInfo operations : enabledOperations) {
			Operation op = new Operation(operations.id, operations.name,
					operations.params);
			if (!containsEdge(new OperationId(op.getId()))) {
				info.add(operations.id, op);
				notifyStateSpaceChange(operations.id,
						containsVertex(getVertex(operations.dest)));
				StateId newState = new StateId(operations.dest,
						operations.state);
				addVertex(newState);
				states.put(newState.getId(), newState);
				addEdge(states.get(operations.src),
						states.get(operations.dest),
						new OperationId(op.getId()));
			}
		}

		info.add(state, command);
	}

	public void explore(final String state) throws ProBException {
		explore(states.get(state));
	}

	public void explore(final int i) throws ProBException {
		String si = String.valueOf(i);
		explore(si);
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
	 * @throws ProBException
	 */
	public List<Operation> opFromPredicate(final StateId stateId,
			final String name, final String predicate, final int nrOfSolutions)
			throws BException, ProBException {
		ClassicalBEvalElement pred = new ClassicalBEvalElement(predicate);
		GetOperationByPredicateCommand command = new GetOperationByPredicateCommand(
				stateId.getId(), name, pred, nrOfSolutions);
		animator.execute(command);
		List<OpInfo> newOps = command.getOperations();
		List<Operation> ops = new ArrayList<Operation>();
		// (id,name,src,dest,args)
		for (OpInfo operations : newOps) {
			Operation op = new Operation(operations.id, operations.name,
					operations.params);
			if (!containsEdge(new OperationId(op.getId()))) {
				info.add(operations.id, op);
				notifyStateSpaceChange(operations.id,
						containsVertex(getVertex(operations.dest)));
				addEdge(getVertex(operations.src), getVertex(operations.dest),
						new OperationId(op.getId()));
			}
			ops.add(op);
		}
		return ops;
	}

	/**
	 * Finds one Operation that satisfies the operation name and predicate at
	 * the current state
	 * 
	 * @param opName
	 * @param predicate
	 * @return one operations that meets the specifications
	 * @throws ProBException
	 * @throws BException
	 */
	public Operation findOneOp(final String opName, final String predicate)
			throws ProBException, BException {
		return opFromPredicate(getCurrentState(), opName, predicate, 1).get(0);
	}

	/**
	 * Checks if the state with stateId is a deadlock
	 * 
	 * @param state
	 * @return returns if a specific state is deadlocked
	 * @throws ProBException
	 */
	public boolean isDeadlock(final StateId state) throws ProBException {
		if (!isExplored(state)) {
			explore(state);
		}
		return outDegreeOf(state) == 0;
	}

	public boolean isDeadlock(final String state) throws ProBException {
		return isDeadlock(states.get(state));
	}

	/**
	 * Checks if the state with stateId has been explored yet
	 * 
	 * @param state
	 * @return returns if a specific state is explored
	 */
	private boolean isExplored(final StateId state) {
		if (!containsVertex(state))
			throw new IllegalArgumentException("Unknown State id");
		return explored.contains(state);
	}

	// MOVE WITHIN STATESPACE

	/**
	 * Finds an Operation with opName and predicate and carries out one
	 * animation step with that Operation
	 * 
	 * @param opName
	 * @param predicate
	 * @throws ProBException
	 * @throws BException
	 */
	public void stepWithOp(final String opName, final String predicate)
			throws ProBException, BException {
		Operation op = findOneOp(opName, predicate);
		step(op.getId());
	}

	public void goToState(final String id) throws ProBException {
		goToState(states.get(id));
	}

	public void goToState(final int id) throws ProBException {
		goToState(String.valueOf(id));
	}

	/**
	 * Moves from the current state to the state at stateId.
	 * 
	 * @param stateId
	 * @throws ProBException
	 */
	public void goToState(final StateId stateId) throws ProBException {
		if (!containsVertex(stateId))
			throw new IllegalArgumentException("state does not exist");
		if (!isExplored(stateId)) {
			try {
				explore(stateId);
			} catch (ProBException e) {
				logger.error("Could not explore state with StateId " + stateId);
				throw new ProBException();
			}
		}

		history.add(stateId.getId(), null);
		evaluateFormulas();
		notifyAnimationChange(getCurrentState(), stateId, null);
	}

	/**
	 * Carries out one step in the animation with the id from an Operation. If
	 * the opId is contained in the outgoing edges (it is enabled) explore it
	 * (if not explored) and add state to history
	 * 
	 * @param opId
	 * @throws ProBException
	 */
	public void step(final String opId) throws ProBException {
		OperationId op = new OperationId(opId);
		if (!outgoingEdgesOf(getCurrentState()).contains(op))
			throw new IllegalArgumentException(opId
					+ " is not a valid operation on this state");

		StateId newState = getEdgeTarget(op);
		if (!isExplored(newState)) {
			try {
				explore(newState.getId());
			} catch (ProBException e) {
				logger.error("Could not explore state with StateId " + newState);
				throw new ProBException();
			}
		}
		history.add(newState.getId(), opId);
		evaluateFormulas();
		notifyAnimationChange(getEdgeSource(op), getEdgeTarget(op), op);
	}

	public void step(final int i) throws ProBException {
		String opId = String.valueOf(i);
		step(opId);
	}

	/**
	 * Moves one step back in the animation if this is possible.
	 */
	public void back() {
		if (history.canGoBack()) {
			StateId oldState = getCurrentState();
			String opId = history.getCurrentTransition();

			history.back();
			evaluateFormulas();

			if (opId != null) {
				OperationId op = new OperationId(opId);
				notifyAnimationChange(getEdgeSource(op), getEdgeTarget(op), op);
			} else {
				notifyAnimationChange(oldState, getCurrentState(), null);
			}
		}
	}

	/**
	 * Moves one step forward in the animation if this is possible
	 */
	public void forward() {
		if (history.canGoForward()) {
			StateId oldState = getCurrentState();

			history.forward();
			evaluateFormulas();

			String opId = history.getCurrentTransition();
			if (opId != null) {
				OperationId op = new OperationId(opId);
				notifyAnimationChange(getEdgeSource(op), getEdgeTarget(op), op);
			} else {
				notifyAnimationChange(oldState, getCurrentState(), null);
			}
		}
	}

	/**
	 * returns the state id of the current state in the animation
	 * 
	 * @return returns the current state from the animation
	 */

	public StateId getCurrentState() {
		String currentState = history.getCurrentState();
		StateId stateId = states.get(currentState);
		return stateId;
	}

	// AUTOMATED ANIMATION IN STATESPACE
	/**
	 * Performs int number of animation steps randomly
	 * 
	 * @param steps
	 * @throws ProBException
	 */
	public void randomAnim(final int steps) throws ProBException {
		if (steps <= 0)
			return;

		final StateId state = getCurrentState();

		boolean deadlock = true;
		try {
			deadlock = isDeadlock(state);
		} catch (ProBException e) {
			logger.error("Could not explore state with StateId " + state);
		}

		if (deadlock)
			return;

		final Set<OperationId> operations = outgoingEdgesOf(state);
		int size = operations.size();
		OperationId[] op = operations.toArray(new OperationId[size]);
		int thresh = randomGenerator.nextInt(size);
		OperationId nextOp = op[thresh];

		final boolean invariantPreserved = info.getInvariantOk().get(state);

		if (!invariantPreserved)
			return;

		step(nextOp.getId());

		randomAnim(steps - 1);
	}

	// EVALUATE PART OF STATESPACE

	/**
	 * Adds an expression or predicate to the list of user formulas. This
	 * expression or predicate is evaluated and the result is added to the map
	 * of variables in the info object.
	 * 
	 * @param formula
	 * @throws BException
	 */
	public void addUserFormula(final String formula) throws BException {
		formulas.add(new ClassicalBEvalElement(formula));
		try {
			List<EvaluationResult> result = evaluate(formulas);
			HashMap<String, String> varsAtState = info
					.getState(getCurrentState());
			for (EvaluationResult evaluationResult : result) {
				varsAtState.put(evaluationResult.code, evaluationResult.value);
			}
		} catch (ProBException e) {
			logger.error("Formula not added successfully", e);
		}
	}

	/**
	 * Evaluates all the user formulas for the current state and updates the map
	 * of variables in the info object accordingly
	 */
	public void evaluateFormulas() {
		try {
			List<EvaluationResult> evaluate = evaluate(formulas);
			HashMap<String, String> varsAtCurrentState = info
					.getState(getCurrentState());
			for (EvaluationResult result : evaluate) {
				varsAtCurrentState.put(result.code, result.value);
			}
		} catch (ProBException e) {
			logger.error("Could not evaluate user formulas for state "
					+ getCurrentState());
		} catch (BException e) {
			logger.error("Parse Exception in formula");
		}
	}

	/**
	 * Evaluates a single formula or an array of formulas (represented as
	 * strings) and returns a list of EvaluationResults for the current state.
	 * 
	 * @param code
	 * @return returns a list of evalutation results
	 * @throws ProBException
	 * @throws BException
	 */
	public List<EvaluationResult> evaluate(final List<IEvalElement> code)
			throws ProBException, BException {
		return eval(getCurrentState().getId(), code);
	}

	public List<EvaluationResult> evaluate(final IEvalElement code)
			throws ProBException, BException {
		List<IEvalElement> list = new ArrayList<IEvalElement>();
		list.add(code);
		return evaluate(list);
	}

	public List<EvaluationResult> evaluate(final String... code)
			throws ProBException, BException {
		List<IEvalElement> list = new ArrayList<IEvalElement>();
		for (String c : code) {
			list.add(new ClassicalBEvalElement(c));
		}
		return evaluate(list);
	}

	/**
	 * Evaluates a single formula or an array of formulas (represented as
	 * strings) for the given state. Returns as list of EvaluationResults.
	 * 
	 * @param state
	 * @param code
	 * @return returns a list of evaluation results
	 * @throws ProBException
	 * @throws BException
	 */
	public List<EvaluationResult> eval(final String state,
			final List<IEvalElement> code) throws ProBException, BException {
		if (!containsVertex(getVertex(state)))
			throw new IllegalArgumentException("state does not exist");

		EvaluateFormulasCommand command = new EvaluateFormulasCommand(code,
				state);
		execute(command);

		return command.getValues();
	}

	public List<EvaluationResult> eval(final String state, final String... code)
			throws BException, ProBException {
		List<IEvalElement> list = new ArrayList<IEvalElement>();
		for (String c : code) {
			list.add(new ClassicalBEvalElement(c));
		}
		return eval(state, list);
	}

	@Override
	public void execute(final ICommand command) throws ProBException {
		animator.execute(command);
	}

	@Override
	public void execute(final ICommand... commands) throws ProBException {
		animator.execute(commands);
	}

	@Override
	public void sendInterrupt() {
		animator.sendInterrupt();
	}

	public void setUpSignalHandler() {
		SignalHandlerImpl.install("TERM", this);
		SignalHandlerImpl.install("INT", this);
		SignalHandlerImpl.install("ABRT", this);
	}

	// NOTIFICATION SYSTEM
	/**
	 * Adds an IAnimationListener to the list of animationListeners. This
	 * listener will be notified whenever an animation step is performed
	 * (whenever the current state changes).
	 * 
	 * @param l
	 */
	public void registerAnimationListener(final IAnimationListener l) {
		animationListeners.add(l);
	}

	/**
	 * Adds an IStateSpaceChangeListener to the list of StateSpaceListeners.
	 * This listener will be notified whenever a new operation or a new state is
	 * added to the graph.
	 * 
	 * @param l
	 */
	public void registerStateSpaceListener(final IStateSpaceChangeListener l) {
		stateSpaceListeners.add(l);
	}

	private void notifyAnimationChange(final StateId stateId,
			final StateId fromState, final OperationId withOp) {
		for (IAnimationListener listener : animationListeners) {
			listener.currentStateChanged(fromState, stateId, withOp);
		}
	}

	private void notifyStateSpaceChange(final String opName,
			final boolean isDestStateNew) {
		for (IStateSpaceChangeListener listener : stateSpaceListeners) {
			listener.newTransition(opName, isDestStateNew);
		}
	}

	// INFORMATION ABOUT THE STATE
	@Override
	public String toString() {
		String result = "";
		result += printState();
		result += printOps();
		result += super.toString();
		return result;
	}

	public String printState() {
		StringBuilder sb = new StringBuilder();
		sb.append("Current State Id: " + getCurrentState() + "\n");
		HashMap<String, String> currentState = info.getState(getCurrentState());
		// FIXME: Find a way to get the names of the variables so that they can
		// be retrieved from the map
		if (currentState != null) {
			Set<Entry<String, String>> entrySet = currentState.entrySet();
			for (Entry<String, String> entry : entrySet) {
				sb.append("  " + entry.getKey() + " -> " + entry.getValue()
						+ "\n");
			}
		}
		return sb.toString();
	}

	public String printOps() {
		StringBuilder sb = new StringBuilder();
		StateId current = getCurrentState();
		Collection<OperationId> opIds = outgoingEdgesOf(current);
		sb.append("Operations: \n");
		for (OperationId opId : opIds) {
			Operation op = info.getOp(opId);
			sb.append("  " + op.getId() + ": " + op.toString() + "\n");
		}
		return sb.toString();
	}

	public String printInfo() {
		return info.toString();
	}

	// assert !s.getOutEdges(s.getCurrentState()).contains(new OperationId("1"))
	public boolean isOutEdge(final StateId sId, final OperationId oId) {
		return outgoingEdgesOf(sId).contains(oId);
	}

	public boolean isOutEdge(final String stateId, final String opId) {
		return isOutEdge(getVertex(stateId), new OperationId(opId));
	}

}
