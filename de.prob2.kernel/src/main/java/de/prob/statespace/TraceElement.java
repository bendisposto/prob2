package de.prob.statespace;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used within the {@link Trace} object to create the linked list
 * of operations. Each TraceElement references a particular operation that has
 * been executed.
 * 
 * @author joy
 * 
 */
public class TraceElement {
	private final int index;
	private final StateId currentState;
	private final OpInfo transition;
	private final List<OpInfo> opList;

	private final TraceElement previous;

	public TraceElement(final StateId stateId) {
		this.currentState = stateId;

		// FOR THE FIRST STATE ID, EVERYTHING IS
		transition = null;
		previous = null;
		index = 0;
		opList = new ArrayList<OpInfo>();
	}

	public TraceElement(final OpInfo edge, final TraceElement previous) {
		this.currentState = edge.getDestId();
		this.transition = edge;

		this.previous = previous;
		index = previous.getIndex() + 1;
		this.opList = previous.opList;
		this.opList.add(transition);
	}

	public TraceElement(final OpInfo edge, final TraceElement previous,
			final List<OpInfo> opList) {
		this.currentState = edge.getDestId();
		this.transition = edge;
		this.previous = previous;
		index = previous.getIndex() + 1;
		this.opList = opList;
	}

	/**
	 * @return the {@link StateId} source corresponding to this element
	 */
	public StateId getSrc() {
		if (transition != null) {
			return transition.getSrcId();
		}
		return currentState;
	}

	/**
	 * @return the {@link StateId} destination corresponding to this element
	 */
	public StateId getDest() {
		if (transition != null) {
			return transition.getDestId();
		}
		return null;
	}

	/**
	 * @return the {@link OpInfo} operation corresponding to this element
	 */
	public OpInfo getOp() {
		return transition;
	}

	/**
	 * @return returns the pointer to the previous TraceElement
	 */
	public TraceElement getPrevious() {
		return previous;
	}

	/**
	 * @return the current {@link StateId} that is associated with the
	 *         TraceElement. If the destination is null, this is source.
	 *         Otherwise, this is destination.
	 */
	public StateId getCurrentState() {
		return currentState;
	}

	/**
	 * @return List of all executed operations ({@link OpInfo})
	 */
	public List<OpInfo> getOpList() {
		return opList;
	}

	/**
	 * @return int index in list of TraceElements
	 */
	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		if (transition == null) {
			return currentState.getId();
		} else {
			return transition.toString();
		}
	}
}
