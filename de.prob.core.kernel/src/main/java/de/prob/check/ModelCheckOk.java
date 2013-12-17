package de.prob.check;

public class ModelCheckOk implements IModelCheckingResult {

	private final String message;
	private final StateSpaceStats stats;

	public ModelCheckOk(final StateSpaceStats stats, final String message) {
		this.stats = stats;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public StateSpaceStats getStats() {
		return stats;
	}

}
