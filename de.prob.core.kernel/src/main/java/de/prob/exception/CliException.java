package de.prob.exception;

import java.io.IOException;

@SuppressWarnings("serial")
public class CliException extends RuntimeException {

	public CliException(String msg) {
		super(msg);
	}

	public CliException(String message, IOException e) {
		super(message, e);
	}

}
