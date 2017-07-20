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

	private final static int NAME_POSITION = 1;
	private final static int TYPE_POSITION = 2;
	private final static int DESC_POSITION = 3;
	private final static int CAT_POSITION = 4;
	private final static int DEFAULT_POSITION = 5;

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
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append("(cat. ").append(category);
		sb.append(", type ").append(type.toString());
		sb.append(", default ").append(defaultValue);
		sb.append(") ").append(description);
		return sb.toString();
	}

}
