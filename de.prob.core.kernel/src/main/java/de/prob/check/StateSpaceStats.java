package de.prob.check;

public class StateSpaceStats {

	private final int totalNodes;
	private final int totalTransitions;
	private final int processedNodes;

	public StateSpaceStats(final int totalNodes, final int totalTransitions,
			final int processedNodes) {
		this.totalNodes = totalNodes;
		this.totalTransitions = totalTransitions;
		this.processedNodes = processedNodes;
	}

	public int getNrTotalNodes() {
		return totalNodes;
	}

	public int getNrTotalTransitions() {
		return totalTransitions;
	}

	public int getNrProcessedNodes() {
		return processedNodes;
	}
}
