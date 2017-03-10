package de.prob.exception;

@SuppressWarnings("serial")
public class CliError extends RuntimeException {

	public CliError(String msg) {
		super(msg);
	}

	public CliError(String message, Exception e) {
		super(message, e);
	}

}
