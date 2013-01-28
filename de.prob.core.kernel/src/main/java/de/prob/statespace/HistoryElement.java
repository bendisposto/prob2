package de.prob.statespace;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.domainobjects.OpInfo;

public class HistoryElement {
	private final StateId src;
	private final StateId dest;
	private final OpInfo edge;

	private final HistoryElement previous;

	private final List<OpInfo> opList;

	public HistoryElement(final StateId src) {
		this.src = src;

		// WHEN THE STATE IS ROOT, EVERYTHING IS NULL
		this.dest = null;
		this.edge = null;
		this.previous = null;

		opList = new ArrayList<OpInfo>();
	}

	public HistoryElement(final StateId src, final StateId dest,
			final OpInfo edge, final HistoryElement previous) {
		this.src = src;
		this.dest = dest;
		this.edge = edge;

		this.previous = previous;

		List<OpInfo> previousOpList = new ArrayList<OpInfo>(
				previous.getOpList());
		previousOpList.add(edge);
		this.opList = previousOpList;
	}

	public StateId getSrc() {
		return src;
	}

	public StateId getDest() {
		return dest;
	}

	public OpInfo getOp() {
		return edge;
	}

	public HistoryElement getPrevious() {
		return previous;
	}

	public StateId getCurrentState() {
		if (dest == null) {
			return src;
		}
		return dest;
	}

	public List<OpInfo> getOpList() {
		return opList;
	}

	@Override
	public String toString() {
		if (dest == null) {
			return "State: " + src.toString();
		} else {
			return "From: " + src.toString() + " To: " + dest.toString()
					+ " With: " + edge.toString();
		}
	}
}
