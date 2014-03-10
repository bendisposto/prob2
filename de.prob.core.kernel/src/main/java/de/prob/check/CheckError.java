package de.prob.check;

public class CheckError implements IModelCheckingResult {

	private final String message;

	public CheckError(final String message) {
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
