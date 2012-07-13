package de.prob.statespace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import de.be4.classicalb.core.parser.exceptions.BException;

public class History {

	private final Random randomGenerator;
	private final List<HistoryElement> history;
	private int current;

	private final StateSpace s;

	// TODO: worry about injection
	public History(final StateSpace s, final Random random) {
		this.s = s;
		this.current = -1;
		this.history = new ArrayList<HistoryElement>();
		this.randomGenerator = random;
	}

	public History(final StateSpace s, final List<HistoryElement> history,
			final Random random) {
		this.s = s;
		this.history = history;
		this.randomGenerator = random;
		current = history.size() - 1;
	}

	/**
	 * Adds a new transition (History Element) to the History object. Takes the
	 * operation id and adds it to the list. If the current position in the
	 * history is the end of the list, it simply appends the new element to the
	 * list. If it is not at the end of the list, the new History Element
	 * overwrites the element at the current position.
	 * 
	 * @param dest
	 * @param op
	 */
	public void add(final String dest, final String op) {
		if (op == null && isLastTransition(null)) {
			// if current state is "root", we can't go back anyway
			// hence we will not add another case for this
			back();
		}

		if (current == -1) {
			String src = "root";
			final HistoryElement elem = new HistoryElement(src, dest, op);
			history.clear();
			history.add(elem);
			current++;
		} else {
			if (history.size() != current) {
				while (history.size() > current + 1) {
					history.remove(current + 1);
				}
			}
			String src = getCurrentState();
			final HistoryElement elem = new HistoryElement(src, dest, op);

			history.add(elem);
			current++;
		}
	}

	public void goToPos(final int pos) {
		if (pos >= -1 && pos < history.size()) {
			current = pos;
		}
	}

	/**
	 * Moves one step back in the animation if this is possible.
	 */
	public void back() {
		if (canGoBack()) {
			current--;

			StateId oldState = getCurrentStateId();
			String opId = getCurrentTransition();

			s.evaluateFormulas(getCurrentStateId());

			if (opId != null) {
				OperationId op = new OperationId(opId);
				s.notifyAnimationChange(s.getEdgeSource(op),
						s.getEdgeTarget(op), op);
			} else {
				s.notifyAnimationChange(oldState, getCurrentStateId(), null);
			}
		}
	}

	/**
	 * Moves one step forward in the animation if this is possible
	 */
	public void forward() {
		if (canGoForward()) {
			current++;
			StateId oldState = getCurrentStateId();
			s.evaluateFormulas(getCurrentStateId());

			String opId = getCurrentTransition();
			if (opId != null) {
				OperationId op = new OperationId(opId);
				s.notifyAnimationChange(s.getEdgeSource(op),
						s.getEdgeTarget(op), op);
			} else {
				s.notifyAnimationChange(oldState, getCurrentStateId(), null);
			}
		}
	}

	public String getCurrentTransition() {
		if (current == -1)
			return null;
		return history.get(current).getOp();
	}

	public boolean isLastTransition(final String id) {
		if (current <= 0)
			return false;

		String currentOp = getCurrentTransition();
		if (id == null)
			return currentOp == null;
		if (currentOp == null)
			return id == null;

		return currentOp.equals(id);
	}

	public boolean isNextTransition(final String id) {
		if (!canGoForward())
			return false;

		String nextOp = history.get(current + 1).getOp();
		if (id == null)
			return nextOp == null;
		if (nextOp == null)
			return id == null;

		return nextOp.equals(id);
	}

	public boolean canGoForward() {
		return current < history.size() - 1;
	}

	public boolean canGoBack() {
		return current >= 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < history.size() - 1; i++) {
			sb.append((history.get(i).getOp()) + ", ");
		}
		if (history.size() != 0) {
			sb.append(history.get(history.size() - 1).getOp());
		}
		String content = sb.toString();
		return "[" + content + "] " + "current Transition: "
				+ getCurrentTransition();
	}

	public String getCurrentState() {
		if (current == -1)
			return "root";
		return history.get(current).getDest();
	}

	public StateId getCurrentStateId() {
		final Map<String, StateId> states = s.getStates();
		return states.get(getCurrentState());
	}

	/**
	 * Carries out one step in the animation with the id from an Operation. If
	 * the opId is contained in the outgoing edges (it is enabled) explore it
	 * (if not explored) and add state to history
	 * 
	 * @param opId
	 */
	public void step(final String opId) {

		OperationId op = new OperationId(opId);
		if (!s.outgoingEdgesOf(getCurrentStateId()).contains(op))
			throw new IllegalArgumentException(opId
					+ " is not a valid operation on this state");

		StateId newState = s.getEdgeTarget(op);
		add(newState.getId(), opId);
		s.evaluateFormulas(getCurrentStateId());
		s.notifyAnimationChange(s.getEdgeSource(op), s.getEdgeTarget(op), op);
	}

	public void step(final int i) {
		String opId = String.valueOf(i);
		step(opId);
	}

	/**
	 * Finds one Operation that satisfies the operation name and predicate at
	 * the current state
	 * 
	 * @param opName
	 * @param predicate
	 * @return one operations that meets the specifications @ * @throws
	 *         BException
	 */
	public Operation findOneOp(final String opName, final String predicate)
			throws BException {
		return s.opFromPredicate(getCurrentStateId(), opName, predicate, 1)
				.get(0);
	}

	/**
	 * Finds an Operation with opName and predicate and carries out one
	 * animation step with that Operation
	 * 
	 * @param opName
	 * @param predicate
	 * @throws BException
	 */
	public void stepWithOp(final String opName, final String predicate)
			throws BException {
		Operation op = findOneOp(opName, predicate);
		step(op.getId());
	}

	/**
	 * Moves from the current state to the state at stateId.
	 * 
	 * @param stateId
	 */
	public void goToState(final StateId stateId) {
		if (!s.containsVertex(stateId))
			throw new IllegalArgumentException("state does not exist");
		if (!s.isExplored(stateId)) {
			s.explore(stateId);
		}

		add(stateId.getId(), null);
		s.evaluateFormulas(getCurrentStateId());
		s.notifyAnimationChange(getCurrentStateId(), stateId, null);
	}

	public void goToState(final String id) {
		goToState(s.getStates().get(id));
	}

	public void goToState(final int id) {
		goToState(String.valueOf(id));
	}

	public void randomAnim(final int steps) {
		if (steps <= 0)
			return;

		final StateId state = getCurrentStateId();

		boolean deadlock = true;
		deadlock = s.isDeadlock(state);

		if (deadlock)
			return;

		final Set<OperationId> operations = s.outgoingEdgesOf(state);
		int size = operations.size();
		OperationId[] op = operations.toArray(new OperationId[size]);
		int thresh = randomGenerator.nextInt(size);
		OperationId nextOp = op[thresh];

		final boolean invariantPreserved = s.getInfo().getInvariantOk()
				.get(state);

		if (!invariantPreserved)
			return;

		step(nextOp.getId());

		randomAnim(steps - 1);
	}

	public String printOps() {
		StringBuilder sb = new StringBuilder();
		StateId current = getCurrentStateId();
		Collection<OperationId> opIds = s.outgoingEdgesOf(current);
		sb.append("Operations: \n");
		for (OperationId opId : opIds) {
			Operation op = s.getInfo().getOp(opId);
			sb.append("  " + op.getId() + ": " + op.toString() + "\n");
		}
		return sb.toString();
	}

	public String printState() {
		StringBuilder sb = new StringBuilder();
		sb.append("Current State Id: " + getCurrentState() + "\n");
		HashMap<String, String> currentState = s.getInfo().getState(
				getCurrentStateId());
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

}