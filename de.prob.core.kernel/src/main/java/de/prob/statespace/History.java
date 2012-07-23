package de.prob.statespace;

import java.util.List;
import java.util.Set;

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

	public Operation findOneOp(final String opName, final String predicate)
			throws BException {
		List<Operation> ops = s.opFromPredicate(current.getCurrentState(),
				opName, predicate, 1);
		if (!ops.isEmpty())
			return ops.get(0);
		throw new IllegalArgumentException("Operation with name " + opName
				+ " not found.");
	}

	public History add(final String opName, final String predicate)
			throws BException {
		Operation op = findOneOp(opName, predicate);
		return add(op.getId());
	}

	public String getOp(final String name, final List<String> params) {
		Set<OperationId> outgoingEdges = s.outgoingEdgesOf(current
				.getCurrentState());
		String id = null;
		for (OperationId operationId : outgoingEdges) {
			Operation op = s.getInfo().getOp(operationId);
			if (op.getName().equals(name) && op.getParams().equals(params)) {
				id = op.getId();
				break;
			}
		}
		return id;
	}
}