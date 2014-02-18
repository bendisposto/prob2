package de.prob.check;

public class NotYetFinished implements IModelCheckingResult {

	private final int maxNumberNodesLeft;
	private final String message;

	public NotYetFinished(final String message, final int maxNumberNodesLeft) {
		this.message = message;
		this.maxNumberNodesLeft = maxNumberNodesLeft;
	}

	public int getMaxNumberNodesLeft() {
		return maxNumberNodesLeft;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return getMessage();
	}

}
