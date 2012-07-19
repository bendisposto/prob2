package de.prob.statespace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.exceptions.BException;
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
	private final StateSpaceInfo info;

	private final HashMap<String, IEvalElement> formulas = new HashMap<String, IEvalElement>();

	private final List<IAnimationListener> animationListeners = new ArrayList<IAnimationListener>();
	private final List<IStateSpaceChangeListener> stateSpaceListeners = new ArrayList<IStateSpaceChangeListener>();

	private final HashMap<String, StateId> states = new HashMap<String, StateId>();

	public final StateId __root;

	@Inject
	public StateSpace(final IAnimator animator,
			final DirectedMultigraphProvider graphProvider,
			final StateSpaceInfo info) {
		super(graphProvider.get());
		this.animator = animator;
		this.info = info;
		__root = new StateId("root", "1", this);
		addVertex(__root);
		states.put(__root.getId(), __root);
	}

	public StateId getRoot() {
		this.explore(__root);
		return __root;
	}

	// MAKE CHANGES TO THE STATESPACE GRAPH
	/**
	 * Takes a state id and calculates the successor states, the invariant,
	 * timeout, etc.
	 * 
	 * @param state
	 */
	public void explore(final StateId state) {
		if (!containsVertex(state))
			throw new IllegalArgumentException("state " + state
					+ " does not exist");

		ExploreStateCommand command = new ExploreStateCommand(state.getId());
		animator.execute(command);
		info.add(state, command);

		explored.add(state);
		List<OpInfo> enabledOperations = command.getEnabledOperations();

		for (OpInfo operations : enabledOperations) {
			Operation op = new Operation(operations.id, operations.name,
					operations.params);
			if (!containsEdge(new OperationId(op.getId()))) {
				getInfo().add(operations.id, op);
				notifyStateSpaceChange(operations.id,
						containsVertex(getVertex(operations.dest)));
				StateId newState = new StateId(operations.dest,
						operations.targetState, this);
				addVertex(newState);
				states.put(newState.getId(), newState);
				addEdge(states.get(operations.src),
						states.get(operations.dest),
						new OperationId(op.getId()));
			}
		}

		getInfo().add(state, command);
	}

	public StateId getVertex(String key) {
		return states.get(key);
	}

	public void explore(final String state) {
		explore(states.get(state));
	}

	public void explore(final int i) {
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
	 */
	public List<Operation> opFromPredicate(final StateId stateId,
			final String name, final String predicate, final int nrOfSolutions)
			throws BException {
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
				getInfo().add(operations.id, op);
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
	 * Checks if the state with stateId is a deadlock
	 * 
	 * @param state
	 * @return returns if a specific state is deadlocked
	 */
	public boolean isDeadlock(final StateId state) {
		if (!isExplored(state)) {
			explore(state);
		}
		return outDegreeOf(state) == 0;
	}

	public boolean isDeadlock(final String state) {
		return isDeadlock(states.get(state));
	}

	/**
	 * Checks if the state with stateId has been explored yet
	 * 
	 * @param state
	 * @return returns if a specific state is explored
	 */
	public boolean isExplored(final StateId state) {
		if (!containsVertex(state))
			throw new IllegalArgumentException("Unknown State id");
		return explored.contains(state);
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
	public String addUserFormula(final String formula) throws BException {
		final ClassicalBEvalElement evalElement = new ClassicalBEvalElement(
				formula);
		int i = 0;
		do {
			i++;
		} while (formulas.keySet().contains("f" + i));
		formulas.put("f" + i, evalElement);
		return "f" + i;
	}

	/**
	 * Adds an expression or predicate to the list of user formulas. This
	 * expression or predicate is evaluated and the result is added to the map
	 * of variables in the info object.
	 * 
	 * @param formula
	 * @throws BException
	 */
	public String addUserFormula(final String name, final String formula)
			throws BException {
		final ClassicalBEvalElement evalElement = new ClassicalBEvalElement(
				formula);
		formulas.put(name, evalElement);
		return name;
	}

	/**
	 * Evaluates a single formula or an array of formulas (represented as
	 * strings) for the given state. Returns as list of EvaluationResults.
	 * 
	 * @param state
	 * @param code
	 * @return returns a list of evaluation results
	 * @throws BException
	 */
	public List<EvaluationResult> eval(final String state,
			final List<IEvalElement> code) throws BException {
		final StateId stateId = getVertex(state);
		if (!containsVertex(stateId))
			throw new IllegalArgumentException("state does not exist");

		if (code.isEmpty())
			return new ArrayList<EvaluationResult>();

		final EvaluateFormulasCommand command = new EvaluateFormulasCommand(
				code, state);
		execute(command);

		List<EvaluationResult> values = command.getValues();

		return values;
	}

	public void evaluateFormulas(final StateId state) {
		Set<Entry<String, IEvalElement>> entrySet = formulas.entrySet();
		for (Entry<String, IEvalElement> entry : entrySet) {
			state.getProperty(entry.getKey());
		}
	}

	public List<EvaluationResult> eval(final String state, final String... code)
			throws BException {
		List<IEvalElement> list = new ArrayList<IEvalElement>();
		for (String c : code) {
			list.add(new ClassicalBEvalElement(c));
		}
		return eval(state, list);
	}

	@Override
	public void execute(final ICommand command) {
		animator.execute(command);
	}

	@Override
	public void execute(final ICommand... commands) {
		animator.execute(commands);
	}

	@Override
	public void sendInterrupt() {
		animator.sendInterrupt();
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

	public void notifyAnimationChange(final StateId stateId,
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
		result += super.toString();
		return result;
	}

	public String printInfo() {
		return getInfo().toString();
	}

	// assert !s.getOutEdges(s.getCurrentState()).contains(new OperationId("1"))
	public boolean isOutEdge(final StateId sId, final OperationId oId) {
		return outgoingEdgesOf(sId).contains(oId);
	}

	public boolean isOutEdge(final String stateId, final String opId) {
		return isOutEdge(getVertex(stateId), new OperationId(opId));
	}

	public HashMap<String, StateId> getStates() {
		return states;
	}

	public StateSpaceInfo getInfo() {
		return info;
	}

	public StateId getState(final StateId state) {
		if (!isExplored(state)) {
			explore(state);
		}
		return state;
	}

	public StateId getState(final OperationId op) {
		final StateId edgeTarget = getEdgeTarget(op);
		if (!isExplored(edgeTarget)) {
			explore(edgeTarget);
		}
		return edgeTarget;
	}

	public String printOps(final StateId state) {
		StringBuilder sb = new StringBuilder();
		Collection<OperationId> opIds = outgoingEdgesOf(state);
		sb.append("Operations: \n");
		for (OperationId opId : opIds) {
			Operation op = getInfo().getOp(opId);
			sb.append("  " + op.getId() + ": " + op.toString() + "\n");
		}
		return sb.toString();
	}

	public String printState(final StateId state) {
		StringBuilder sb = new StringBuilder();
		sb.append("Current State Id: " + state + "\n");
		HashMap<String, String> currentState = getInfo().getState(state);
		if (currentState != null) {
			Set<Entry<String, String>> entrySet = currentState.entrySet();
			for (Entry<String, String> entry : entrySet) {
				sb.append("  " + entry.getKey() + " -> " + entry.getValue()
						+ "\n");
			}
		}
		return sb.toString();
	}

	public HashMap<String, IEvalElement> getForms() {
		return formulas;
	}

}
