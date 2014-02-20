package de.prob.check;

public class ModelCheckOk implements IModelCheckingResult {

	private final String message;

	public ModelCheckOk(final String message) {
		this.message = message;
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
