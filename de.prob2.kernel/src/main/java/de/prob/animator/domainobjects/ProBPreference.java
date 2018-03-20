/* 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 */

package de.prob.animator.domainobjects;

import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class ProBPreference {

	public final String name;
	public final PrologTerm type;
	public final String description;
	public final String category;
	public final String defaultValue;

	private static final int NAME_POSITION = 1;
	private static final int TYPE_POSITION = 2;
	private static final int DESC_POSITION = 3;
	private static final int CAT_POSITION = 4;
	private static final int DEFAULT_POSITION = 5;

	public ProBPreference(final CompoundPrologTerm term) {
		name = PrologTerm.atomicString(term.getArgument(NAME_POSITION));
		type = term.getArgument(TYPE_POSITION);
		description = PrologTerm.atomicString(term.getArgument(DESC_POSITION));
		category = PrologTerm.atomicString(term.getArgument(CAT_POSITION));
		final PrologTerm defaultTerm = term.getArgument(DEFAULT_POSITION);
		defaultValue = defaultTerm.isAtom() ? PrologTerm
				.atomicString(defaultTerm) : defaultTerm.toString();
	}

	@Override
	public String toString() {
		return name + "(cat. " + category + ", type " + type + ", default " + defaultValue + ") " + description;
	}

}
