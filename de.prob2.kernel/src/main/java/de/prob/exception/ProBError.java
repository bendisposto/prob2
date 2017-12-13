package de.prob.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.be4.classicalb.core.parser.exceptions.BCompoundException;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.domainobjects.ErrorItem;

public class ProBError extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final String originalMessage;
	private final List<ErrorItem> errorItems;

	private static String formatMessageAndErrors(final String message, final List<ErrorItem> errors) {
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
				for (final ErrorItem err : errors) {
					out.append('\n');
					out.append(err);
				}
			}
		}

		return out.toString();
	}

	public ProBError(final String message, final List<ErrorItem> errors, final Throwable cause) {
		super(formatMessageAndErrors(message, errors), cause);
		this.originalMessage = message;
		this.errorItems = errors == null ? null : new ArrayList<>(errors);
	}

	public ProBError(final String message, final List<ErrorItem> errors) {
		this(message, errors, null);
	}

	public ProBError(final List<ErrorItem> errors) {
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

	public String getOriginalMessage() {
		return this.originalMessage;
	}

	public List<ErrorItem> getErrors() {
		return this.errorItems == null ? null : Collections.unmodifiableList(this.errorItems);
	}

	public ProBError(BCompoundException e) {
		this(convertParserExceptionToErrorItems(e));
	}

	private static List<ErrorItem> convertParserExceptionToErrorItems(BCompoundException e) {
		List<ErrorItem> errorItems = new ArrayList<>();
		for (BException bException : e.getBExceptions()) {
			List<ErrorItem.Location> errorItemlocations = new ArrayList<>();
			if (bException.getFilename() != null && bException.getCause() != null) {
				List<BException.Location> parserlocations = bException.getLocations();
				for (BException.Location location : parserlocations) {
					ErrorItem.Location loc = new ErrorItem.Location(bException.getFilename(), location.getStartLine(),
							location.getStartColumn(), location.getEndLine(), location.getEndColumn());
					errorItemlocations.add(loc);
				}
			}
			ErrorItem item = new ErrorItem(bException.getMessage(), ErrorItem.Type.ERROR, errorItemlocations);
			errorItems.add(item);
		}
		return errorItems;
	}
}
