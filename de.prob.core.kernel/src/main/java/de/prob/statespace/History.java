package de.prob.statespace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.domainobjects.OpInfo;

public class History {

	private final HistoryElement current;
	private final HistoryElement head;
	private final List<IAnimationListener> animationListeners;

	private final StateSpace s;

	public History(final StateSpace s) {
		this.s = s;
		head = new HistoryElement(s.getState(s.getVertex("root")));
		current = head;
		animationListeners = new ArrayList<IAnimationListener>();
	}

	public History(final StateSpace s, final HistoryElement head,
			final List<IAnimationListener> animationListeners) {
		this.s = s;
		this.head = head;
		this.current = head;
		this.animationListeners = animationListeners;
	}

	public History(final StateSpace s, final HistoryElement head,
			final HistoryElement current,
			final List<IAnimationListener> animationListeners) {
		this.s = s;
		this.head = head;
		this.current = current;
		this.animationListeners = animationListeners;
	}

	public History add(final String name, final List<String> params) {
		return add(getOp(name, params));
	}

	public History add(final String opId) {
		// if(!s.getOps().containsKey(opId))
		// throw new IllegalArgumentException(opId +
		// " is not a valid operation in the statespace");
		OpInfo op = s.getOps().get(opId);
		if (!s.outgoingEdgesOf(current.getCurrentState()).contains(op))
			throw new IllegalArgumentException(opId
					+ " is not a valid operation on this state");

		StateId newState = s.getState(op);
		s.evaluateFormulas(current.getCurrentState());

		History newHistory = new History(s, new HistoryElement(
				current.getCurrentState(), newState, op, current),
				animationListeners);
		notifyAnimationChange(this,newHistory);

		return newHistory;
	}

	public History add(final int i) {
		String opId = String.valueOf(i);
		return add(opId);
	}

	/**
	 * Moves one step back in the animation if this is possible.
	 */
	public History back() {
		if (canGoBack()) {
			History history = new History(s, head, current.getPrevious(),
					animationListeners);
			notifyAnimationChange(this,history);
			return history;
		}
		return this;
	}

	/**
	 * Moves one step forward in the animation if this is possible
	 * 
	 * @return
	 */
	public History forward() {
		if (canGoForward()) {
			HistoryElement p = head;
			while (p.getPrevious() != current) {
				p = p.getPrevious();
			}
			History history = new History(s, head, p, animationListeners);
			notifyAnimationChange(this,history);
			return history;
		}
		return this;
	}

	public boolean canGoForward() {
		return current != head;
	}

	public boolean canGoBack() {
		return current.getPrevious() != null;
	}

	@Override
	public String toString() {
		return s.printOps(current.getCurrentState()) + getRep();
	}

	public String getRep() {
		return head.getRepresentation() + "] Current Transition is: "
				+ current.getOp();
	}

	public OpInfo findOneOp(final String opName, final String predicate)
			throws BException {
		List<OpInfo> ops = s.opFromPredicate(current.getCurrentState(), opName,
				predicate, 1);
		if (!ops.isEmpty())
			return ops.get(0);
		throw new IllegalArgumentException("Operation with name " + opName
				+ " not found.");
	}

	public History add(final String opName, final String predicate)
			throws BException {
		OpInfo op = findOneOp(opName, predicate);
		return add(op.id);
	}

	public String getOp(final String name, final List<String> params) {
		Set<OpInfo> outgoingEdges = s
				.outgoingEdgesOf(current.getCurrentState());
		String id = null;
		for (OpInfo op : outgoingEdges) {
			if (op.getName().equals(name) && op.getParams().equals(params)) {
				id = op.getId();
				break;
			}
		}
		return id;
	}
	
	public History randomAnimation(final int numOfSteps) {
		StateId currentState = this.current.getCurrentState();
		History oldHistory = this;
		HistoryElement previous = this.current;
		HistoryElement current = this.current;
		for(int i = 0; i < numOfSteps; i++) {
			previous = current;
			List<OpInfo> ops = new ArrayList<OpInfo>();
			ops.addAll(s.outgoingEdgesOf(currentState));
			Collections.shuffle(ops);
			OpInfo op = ops.get(0);
			
			StateId newState = s.getState(op);
			s.evaluateFormulas(newState);
			
			current = new HistoryElement(currentState,newState,op,previous);
			currentState = newState;
		}
		
		History newHistory = new History(s, current, animationListeners);
		notifyAnimationChange(oldHistory, newHistory);
		
		return newHistory;
	}

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

	public void notifyAnimationChange(final History oldHistory,final History newHistory) {
		for (IAnimationListener listener : animationListeners) {
			listener.currentStateChanged(oldHistory,newHistory);
		}
	}

	public HistoryElement getCurrent() {
		return current;
	}

	public HistoryElement getHead() {
		return head;
	}
	
	public StateSpace getStatespace() {
		return s;
	}
	
	public Set<OpInfo> getNextTransitions() {
		return s.outgoingEdgesOf(current.getCurrentState());
	}
	
}