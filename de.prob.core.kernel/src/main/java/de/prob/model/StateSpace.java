package de.prob.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.ProBException;
import de.prob.animator.IAnimator;
import de.prob.animator.command.ExploreStateCommand;
import de.prob.animator.command.ICommand;
import de.prob.animator.command.OpInfo;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.MultiGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class StateSpace extends StateSpaceGraph implements IAnimator,
		DirectedGraph<String, String>, MultiGraph<String, String> {

	Logger logger = LoggerFactory.getLogger(StateSpace.class);

	private IAnimator animator;
	private HashSet<String> explored = new HashSet<String>();
	private History history = new History();
	private HashMap<String, Operation> ops = new HashMap<String, Operation>();
	private HashMap<String, HashMap<String, String>> variables = new HashMap<String, HashMap<String, String>>();
	private HashMap<String, Boolean> invariantOk = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> timeoutOccured = new HashMap<String, Boolean>();
	private HashMap<String, Set<String>> operationsWithTimeout = new HashMap<String, Set<String>>();

	private List<IAnimationListener> animationListeners = new ArrayList<IAnimationListener>();
	private List<IStateSpaceChangeListener> stateSpaceListeners = new ArrayList<IStateSpaceChangeListener>();

	@Inject
	public StateSpace(final IAnimator animator,
			final DirectedSparseMultigraph<String, String> graph) {
		super(graph);
		this.animator = animator;
		addVertex("root");
	}

	/**
	 * Takes a state id and calculates the successor states, the invariant,
	 * timeout, etc.
	 * 
	 * @param stateId
	 * @throws ProBException
	 */
	public void explore(final String stateId) throws ProBException {
		ExploreStateCommand command = new ExploreStateCommand(stateId);

		animator.execute(command);
		explored.add(stateId);
		List<OpInfo> enabledOperations = command.getEnabledOperations();
		// (id,name,src,dest,args)
		for (OpInfo operations : enabledOperations) {
			Operation op = new Operation(operations.id, operations.name,
					operations.params);
			if (!containsEdge(op.getId())) {
				ops.put(operations.id, op);
				notifyStateSpaceChange(operations.id,
						containsVertex(operations.dest));
				addEdge(op.getId(), operations.src, operations.dest);
			}
		}

		variables.put(stateId, command.getVariables());
		invariantOk.put(stateId, command.isInvariantOk());
		timeoutOccured.put(stateId, command.isTimeoutOccured());
		operationsWithTimeout.put(stateId, command.getOperationsWithTimeout());

		System.out.println("State: " + stateId);
		System.out.println(variables.toString());
		System.out.println(invariantOk.toString());
		System.out.println(timeoutOccured.toString());
		System.out.println(operationsWithTimeout.toString());

		System.out
				.println("======================================================");
	}


	public void goToState(final String stateId) {
		if(!containsVertex(stateId))
			throw new IllegalArgumentException("state does not exist");
		notifyAnimationChange(getCurrentState(), stateId, null);
		history.add(stateId, null);
	}

	public void step(final String opId) throws ProBException {
		if (history.isLastTransition(opId)) {
			back(opId);
		} else if (history.isNextTransition(opId)) {
			forward(opId);
		} else if (getOutEdges(getCurrentState()).contains(opId)) {
			String newState = getDest(opId);
			if (!isExplored(newState)) {
				try {
					explore(newState);
				} catch (ProBException e) {
					logger.error("Could not explore state with StateId "
							+ newState);
					throw new ProBException();
				}
			}
			history.add(newState, opId);
			notifyAnimationChange(getSource(opId), getDest(opId), opId);
		}

		System.out.println(history.toString());

	}

	public void back(final String opId) {
		notifyAnimationChange(getDest(opId), getSource(opId), null);
		history.back();
	}

	public void forward(final String opId) {
		notifyAnimationChange(getSource(opId), getDest(opId), opId);
		history.forward();
	}

	public void step(final int i) throws ProBException {
		String opId = String.valueOf(i);
		step(opId);
	}

	public void explore(final int i) throws ProBException {
		String si = String.valueOf(i);
		explore(si);
	}

	@Override
	public void execute(final ICommand command) throws ProBException {
		animator.execute(command);
	}

	@Override
	public void execute(final ICommand... commands) throws ProBException {
		animator.execute(commands);
	}

	public String getCurrentState() {
		return history.getCurrentState();
	}

	public boolean isDeadlock(final String stateid) throws ProBException {
		if (!isExplored(stateid)) {
			explore(stateid);
		}
		return getOutEdges(stateid).isEmpty();
	}

	private boolean isExplored(final String stateid) {
		if (!containsVertex(stateid))
			throw new IllegalArgumentException("Unknown State id");
		return explored.contains(stateid);
	}

	@Override
	public boolean addEdge(final String opId, final String src,
			final String dest) {
		return addEdge(opId, src, dest, EdgeType.DIRECTED);
	}

	public boolean canGoBack() {
		return history.canGoBack();
	}

	public boolean canGoForward() {
		return history.canGoForward();
	}

	public HashMap<String, String> getState(final String stateId) {
		return variables.get(stateId);
	}

	public HashMap<String, String> getState(final int stateId) {
		String id = String.valueOf(stateId);
		return getState(id);
	}

	public HashMap<String, Boolean> getInvariantOk() {
		return invariantOk;
	}

	public HashMap<String, Boolean> getTimeoutOccured() {
		return timeoutOccured;
	}

	public HashMap<String, Set<String>> getOperationsWithTimeout() {
		return operationsWithTimeout;
	}

	public void registerAnimationListener(final IAnimationListener l) {
		animationListeners.add(l);
	}

	public void registerStateSpaceListener(final IStateSpaceChangeListener l) {
		stateSpaceListeners.add(l);
	}

	private void notifyAnimationChange(final String fromState,
			final String toState, final String withOp) {
		for (IAnimationListener listener : animationListeners) {
			listener.currentStateChanged(fromState, toState, withOp);
		}
	}

	private void notifyStateSpaceChange(final String opName,
			final boolean isDestStateNew) {
		for (IStateSpaceChangeListener listener : stateSpaceListeners) {
			listener.newTransition(opName, isDestStateNew);
		}
	}

}
