package de.prob.check;

public class NotYetFinished implements IModelCheckingResult {

	private final int maxNumberNodesLeft;

	public NotYetFinished(final int maxNumberNodesLeft) {
		this.maxNumberNodesLeft = maxNumberNodesLeft;
	}

	public int getMaxNumberNodesLeft() {
		return maxNumberNodesLeft;
	}

}
