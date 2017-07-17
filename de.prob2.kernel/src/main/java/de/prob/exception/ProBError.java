package de.prob.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProBError extends RuntimeException {
	private final List<String> errors;

	private static String formatMessageAndErrors(final String message, final List<String> errors) {
		final StringBuilder out = new StringBuilder();
		
		if (message != null && !message.isEmpty()) {
			out.append(message);
			if (errors != null) {
				out.append('\n');
			}
		}
		
		if (errors != null) {
			if (errors.isEmpty()) {
				out.append("ProB returned no error messages.");
			} else {
				out.append("ProB returned error messages:");
				for (final String err : errors) {
					out.append('\n');
					out.append(err);
				}
			}
		}
		
		return out.toString();
	}

	public ProBError(final String message, final List<String> errors, final Throwable cause) {
		super(formatMessageAndErrors(message, errors), cause);
		
		this.errors = new ArrayList<>(errors);
	}

	public ProBError(final String message, final List<String> errors) {
		this(message, errors, null);
	}

	public ProBError(final List<String> errors) {
		this(null, errors, null);
	}

	public ProBError(final String message, final Throwable cause) {
		this(message, null, cause);
	}

	public ProBError(final String message) {
		this(message, null, null);
	}

	public ProBError(final Throwable cause) {
		this(cause.getMessage(), null, cause);
	}

	public List<String> getErrors() {
		return Collections.unmodifiableList(this.errors);
	}

	private static final long serialVersionUID = 6643099683773925615L;
}
