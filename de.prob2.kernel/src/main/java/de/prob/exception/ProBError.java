package de.prob.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.be4.classicalb.core.parser.exceptions.BCompoundException;

import de.prob.animator.domainobjects.ErrorItem;

import edu.mit.csail.sdg.alloy4.Err;

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

	public ProBError(BCompoundException e) {
		this(null, convertParserExceptionToErrorItems(e), e);
	}

	public ProBError(Err e) {
		this(null, convertAlloyExceptionToErrorItems(e), e);
	}

	private static List<ErrorItem> convertParserExceptionToErrorItems(BCompoundException e) {
		return e.getBExceptions().stream()
			.map(ErrorItem::fromParserException)
			.collect(Collectors.toList());
	}

	private static List<ErrorItem> convertAlloyExceptionToErrorItems(Err e) {
		return Collections.singletonList(
			new ErrorItem(e.msg, ErrorItem.Type.ERROR, Collections.singletonList(
				new ErrorItem.Location(e.pos.filename, e.pos.y, e.pos.x, e.pos.y2, e.pos.x2)
			))
		);
	}

	public String getOriginalMessage() {
		return this.originalMessage;
	}

	public List<ErrorItem> getErrors() {
		return this.errorItems == null ? null : Collections.unmodifiableList(this.errorItems);
	}
}
