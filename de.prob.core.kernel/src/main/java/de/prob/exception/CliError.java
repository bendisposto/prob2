package de.prob.exception;

import java.io.IOException;

import de.prob.prolog.term.PrologTerm;

@SuppressWarnings("serial")
public class CliError extends RuntimeException {

	public CliError(String msg) {
		super(msg);
	}

	public CliError(String message, IOException e) {
		super(message, e);
	}
	
	public CliError(PrologTerm pt) {
		super(pt.toString());
		
	}

}
