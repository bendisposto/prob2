package de.prob.check;

public class ModelCheckOk implements IModelCheckingResult {

	private final String message;
	private final StateSpaceStats stats;
	private final ModelCheckingOptions options;

	public ModelCheckOk(final StateSpaceStats stats, final String message,
			final ModelCheckingOptions options) {
		this.stats = stats;
		this.message = message;
		this.options = options;
	}

	public String getMessage() {
		return message;
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
