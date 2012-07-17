package de.prob.statespace;

public class HistoryElement {
	private final StateId src;
	private final StateId dest;
	private final OperationId edge;

	private final HistoryElement previous;

	private final String representation;

	public HistoryElement(final StateId src) {
		this.src = src;

		// WHEN THE STATE IS ROOT, EVERYTHING IS NULL
		this.dest = null;
		this.edge = null;
		this.previous = null;

		representation = "[";
	}

	public HistoryElement(final StateId src, final StateId dest,
			final OperationId edge, final HistoryElement previous) {
		this.src = src;
		this.dest = dest;
		this.edge = edge;

		this.previous = previous;
		if (previous.getPrevious() == null) {
			representation = previous.getRepresentation() + edge;
		} else {
			representation = previous.getRepresentation() + ", " + edge;
		}
	}

	public StateId getSrc() {
		return src;
	}

	public StateId getDest() {
		return dest;
	}

	public OperationId getOp() {
		return edge;
	}

	public HistoryElement getPrevious() {
		return previous;
	}

	public StateId getCurrentState() {
		if (dest == null)
			return src;
		return dest;
	}

	public String getRepresentation() {
		return representation;
	}
}
