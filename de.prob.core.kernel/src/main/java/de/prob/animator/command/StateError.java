/**
 * 
 */
package de.prob.animator.command;

import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * An instance of this class represents a state based error. Such errors
 * happened in an event starting in a state.
 * 
 * The current implementation is very limited, in future, the error should be
 * easy to examine by the user, including visualization of predicates or
 * expressions.
 * 
 * @author plagge
 */
public class StateError {
	/**
	 * @uml.property  name="event"
	 */
	private final String event;
	/**
	 * @uml.property  name="shortDescription"
	 */
	private final String shortDescription;
	/**
	 * @uml.property  name="longDescription"
	 */
	private final String longDescription;

	public StateError(final String event, final String shortDescription,
			final String longDescription) {
		super();
		this.event = event;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
	}

	public StateError(final CompoundPrologTerm term) {
		this.event = PrologTerm.atomicString(term.getArgument(1));
		this.shortDescription = PrologTerm.atomicString(term.getArgument(2));
		this.longDescription = PrologTerm.atomicString(term.getArgument(3));
	}

	/**
	 * @return
	 * @uml.property  name="event"
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @return
	 * @uml.property  name="shortDescription"
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * @return
	 * @uml.property  name="longDescription"
	 */
	public String getLongDescription() {
		return longDescription;
	}

}
