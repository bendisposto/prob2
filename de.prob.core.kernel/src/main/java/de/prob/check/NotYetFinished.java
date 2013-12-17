package de.prob.check;

public class NotYetFinished implements IModelCheckingResult {

	private final int maxNumberNodesLeft;
	private final StateSpaceStats stats;

	public NotYetFinished(final StateSpaceStats stats,
			final int maxNumberNodesLeft) {
		this.stats = stats;
		this.maxNumberNodesLeft = maxNumberNodesLeft;
	}

	public int getMaxNumberNodesLeft() {
		return maxNumberNodesLeft;
	}

	@Override
	public StateSpaceStats getStats() {
		return stats;
	}

}
