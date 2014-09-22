package de.prob.exception;

import java.io.IOException;

@SuppressWarnings("serial")
public class CliError extends RuntimeException {

	public CliError(String msg) {
		super(msg);
	}

	public CliError(String message, IOException e) {
		super(message, e);
	}

}
