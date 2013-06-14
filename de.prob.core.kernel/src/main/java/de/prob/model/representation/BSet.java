package de.prob.model.representation;

import de.prob.unicode.UnicodeTranslator;

public class BSet extends AbstractElement {

	private final String name;

	public BSet(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return UnicodeTranslator.toUnicode(name);
	}
}
