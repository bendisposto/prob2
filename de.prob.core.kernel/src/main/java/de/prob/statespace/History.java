package de.prob.statespace;

import de.be4.classicalb.core.parser.exceptions.BException;

public class History {

	private final HistoryElement current;
	private final HistoryElement head;

	private final StateSpace s;

	public History(final StateSpace s) {
		this.s = s;
		head = new HistoryElement(s.getState(s.getVertex("root")));
		current = head;
	}

	public History(final StateSpace s, final HistoryElement head) {
		this.s = s;
		this.head = head;
		this.current = head;
	}

	public History(final StateSpace s, final HistoryElement head,
			final HistoryElement current) {
		this.s = s;
		this.head = head;
		this.current = current;
	}

	public History add(final String opId) {
		OperationId op = new OperationId(opId);
		if (!s.outgoingEdgesOf(current.getCurrentState()).contains(op))
			throw new IllegalArgumentException(opId
					+ " is not a valid operation on this state");

		StateId newState = s.getState(op);
		s.evaluateFormulas(current.getCurrentState());

		return new History(s, new HistoryElement(current.getCurrentState(),
				newState, op, current));
	}

	public History add(final int i) {
		String opId = String.valueOf(i);
		return add(opId);
	}

	/**
	 * Moves one step back in the animation if this is possible.
	 */
	public History back() {
		if (canGoBack())
			return new History(s, head, current.getPrevious());
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
			return new History(s, head, p);
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
		return head.getRepresentation() + "] Current Transition is: "
				+ current.getOp();
	}

	/**
	 * Carries out one step in the animation with the id from an Operation. If
	 * the opId is contained in the outgoing edges (it is enabled) explore it
	 * (if not explored) and add state to history
	 * 
	 * @param opId
	 */

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
		return s.opFromPredicate(current.getCurrentState(), opName, predicate,
				1).get(0);
	}

	/**
	 * Finds an Operation with opName and predicate and carries out one
	 * animation step with that Operation
	 * 
	 * @param opName
	 * @param predicate
	 * @throws BException
	 */
	public void execOp(final String opName, final String predicate)
			throws BException {
		Operation op = findOneOp(opName, predicate);
		add(op.getId());
	}

}