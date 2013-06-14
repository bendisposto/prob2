package de.prob.statespace;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used within the {@link Trace} object to create the linked list
 * of operations. Each TraceElement references a particular operation that has
 * been executed. This includes saving the {@link StateId} source and
 * {@link StateId} destination, as well as the {@link OpInfo}. It also contains
 * a list of all executed operations and a pointer to the previous TraceElement.
 * When a State is root, everything except the source is set to null.
 * 
 * @author joy
 * 
 */
public class TraceElement {
	private final StateId src;
	private final StateId dest;
	private final OpInfo edge;

	private final TraceElement previous;

	private final List<OpInfo> opList;

	public TraceElement(final StateId src) {
		this.src = src;

		// WHEN THE STATE IS ROOT, EVERYTHING IS NULL
		dest = null;
		edge = null;
		previous = null;

		opList = new ArrayList<OpInfo>();
	}

	public TraceElement(final StateId src, final StateId dest,
			final OpInfo edge, final TraceElement previous) {
		this.src = src;
		this.dest = dest;
		this.edge = edge;

		this.previous = previous;

		List<OpInfo> previousOpList = new ArrayList<OpInfo>(
				previous.getOpList());
		previousOpList.add(edge);
		opList = previousOpList;
	}

	/**
	 * @return the {@link StateId} source corresponding to this element
	 */
	public StateId getSrc() {
		return src;
	}

	/**
	 * @return the {@link StateId} destination corresponding to this element
	 */
	public StateId getDest() {
		return dest;
	}

	/**
	 * @return the {@link OpInfo} operation corresponding to this element
	 */
	public OpInfo getOp() {
		return edge;
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
		if (dest == null) {
			return src;
		}
		return dest;
	}

	/**
	 * @return List of all executed operations ({@link OpInfo})
	 */
	public List<OpInfo> getOpList() {
		return opList;
	}

	@Override
	public String toString() {
		if (dest == null) {
			return "State: " + src.toString();
		} else {
			return "From: " + src.toString() + " To: " + dest.toString()
					+ " With: " + edge.getId();
		}
	}
}
