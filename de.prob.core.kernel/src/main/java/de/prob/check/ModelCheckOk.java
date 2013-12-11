package de.prob.check;

public class ModelCheckOk implements IModelCheckingResult {

	private final String message;

	public ModelCheckOk(final String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
