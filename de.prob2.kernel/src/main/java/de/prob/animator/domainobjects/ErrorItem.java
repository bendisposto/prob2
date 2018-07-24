package de.prob.animator.domainobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.be4.classicalb.core.parser.exceptions.BException;

import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class ErrorItem {
	public enum Type {
		WARNING,
		ERROR,
		INTERNAL_ERROR,
	}
	
	public static final class Location {
		private final String filename;
		private final int startLine;
		private final int startColumn;
		private final int endLine;
		private final int endColumn;
		
		public Location(final String filename, final int startLine, final int startColumn, final int endLine, final int endColumn) {
			super();
			
			Objects.requireNonNull(filename, "filename");
			
			this.filename = filename;
			this.startLine = startLine;
			this.startColumn = startColumn;
			this.endLine = endLine;
			this.endColumn = endColumn;
		}
		
		public static Location fromProlog(final PrologTerm location) {
			if (!location.hasFunctor("error_span", 5)) {
				throw new IllegalArgumentException(String.format(
					"Error locations list should contain terms of form " +
					"error_span(Filename,StartLine,StartCol,EndLine,EndCol), " +
					"but found term %s with arity %d",
					location.getFunctor(), location.getArity()
				));
			}
			
			final String filename = PrologTerm.atomicString(location.getArgument(1));
			final int startLine = ((IntegerPrologTerm)location.getArgument(2)).getValue().intValueExact();
			final int startColumn = ((IntegerPrologTerm)location.getArgument(3)).getValue().intValueExact();
			final int endLine = ((IntegerPrologTerm)location.getArgument(4)).getValue().intValueExact();
			final int endColumn = ((IntegerPrologTerm)location.getArgument(5)).getValue().intValueExact();
			
			return new Location(filename, startLine, startColumn, endLine, endColumn);
		}
		
		public static Location fromParserLocation(final BException.Location location) {
			return new ErrorItem.Location(
				location.getFilename() == null ? "(unknown file)" : location.getFilename(),
				location.getStartLine(),
				location.getStartColumn(),
				location.getEndLine(),
				location.getEndColumn()
			);
		}
		
		public String getFilename() {
			return this.filename;
		}
		
		public int getStartLine() {
			return this.startLine;
		}
		
		public int getStartColumn() {
			return this.startColumn;
		}
		
		public int getEndLine() {
			return this.endLine;
		}
		
		public int getEndColumn() {
			return this.endColumn;
		}
		
		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || !this.getClass().equals(o.getClass())) {
				return false;
			}
			final ErrorItem.Location location = (ErrorItem.Location)o;
			return
				this.getFilename().equals(location.getFilename())
				&& this.getStartLine() == location.getStartLine()
				&& this.getStartColumn() == location.getStartColumn()
				&& this.getEndLine() == location.getEndLine()
				&& this.getEndColumn() == location.getEndColumn();
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.getFilename(), this.getStartLine(), this.getStartColumn(), this.getEndLine(), this.getEndColumn());
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder(this.filename);
			sb.append(':');
			sb.append(this.getStartLine());
			sb.append(':');
			sb.append(this.getStartColumn());
			
			if (this.getStartLine() != this.getEndLine() || this.getStartColumn() != this.getEndColumn()) {
				sb.append(" to ");
				sb.append(this.getEndLine());
				sb.append(':');
				sb.append(this.getEndColumn());
			}
			
			return sb.toString();
		}
	}
	
	private final String message;
	private final ErrorItem.Type type;
	private final List<ErrorItem.Location> locations;
	
	public ErrorItem(final String message, final ErrorItem.Type type, final List<ErrorItem.Location> locations) {
		super();
		
		Objects.requireNonNull(message, "message");
		Objects.requireNonNull(type, "type");
		Objects.requireNonNull(locations, "locations");
		
		this.message = message;
		this.type = type;
		this.locations = new ArrayList<>(locations);
	}
	
	public static ErrorItem fromProlog(final PrologTerm error) {
		if (!error.hasFunctor("error", 3)) {
			throw new IllegalArgumentException(String.format(
				"Errors list should contain terms of form " +
				"error(Msg,Type,Locations), but found term %s with arity %d",
				error.getFunctor(), error.getArity()
			));
		}
		
		final String message = PrologTerm.atomicString(error.getArgument(1));
		
		final String typeName = PrologTerm.atomicString(error.getArgument(2));
		final Type type;
		switch (typeName) {
			case "warning":
				type = Type.WARNING;
				break;
			
			case "error":
				type = Type.ERROR;
				break;
			
			case "internal_error":
				type = Type.INTERNAL_ERROR;
				break;
			
			default:
				throw new IllegalArgumentException("Unknown error type: " + typeName);
		}
		
		final List<Location> locations = ((ListPrologTerm)error.getArgument(3)).stream()
			.map(Location::fromProlog)
			.collect(Collectors.toList());
		
		return new ErrorItem(message, type, locations);
	}
	
	public static ErrorItem fromParserException(final BException exception) {
		return new ErrorItem(exception.getMessage(), Type.ERROR, exception.getLocations().stream()
			.map(Location::fromParserLocation)
			.collect(Collectors.toList()));
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public ErrorItem.Type getType() {
		return this.type;
	}
	
	public List<ErrorItem.Location> getLocations() {
		return Collections.unmodifiableList(this.locations);
	}
	
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || !this.getClass().equals(o.getClass())) {
			return false;
		}
		final ErrorItem errorItem = (ErrorItem)o;
		return
			this.getMessage().equals(errorItem.getMessage())
			&& this.getType().equals(errorItem.getType())
			&& this.getLocations().equals(errorItem.getLocations());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.getMessage(), this.getType(), this.getLocations());
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		switch (this.getType()) {
			case WARNING:
				sb.append("Warning: ");
				break;
			
			case ERROR:
				sb.append("Error: ");
				break;
			
			case INTERNAL_ERROR:
				sb.append("Internal error: ");
				break;
			
			default:
				sb.append(this.getType());
				sb.append(": ");
		}
		
		sb.append(this.getMessage());
		
		if (!this.getLocations().isEmpty()) {
			sb.append(
				this.locations.stream()
				.map(ErrorItem.Location::toString)
				.collect(Collectors.joining("; ", " (", ")"))
			);
		}
		
		return sb.toString();
	}
}
