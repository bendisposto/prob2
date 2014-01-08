package de.prob.check;

public class NotYetFinished implements IModelCheckingResult {

	private final int maxNumberNodesLeft;
	private final StateSpaceStats stats;
	private final ModelCheckingOptions options;

	public NotYetFinished(final StateSpaceStats stats,
			final int maxNumberNodesLeft, final ModelCheckingOptions options) {
		this.stats = stats;
		this.maxNumberNodesLeft = maxNumberNodesLeft;
		this.options = options;
	}

	public int getMaxNumberNodesLeft() {
		return maxNumberNodesLeft;
	}

	@Override
	public StateSpaceStats getStats() {
		return stats;
	}

	@Override
	public ModelCheckingOptions getOptions() {
		return options;
	}

}
