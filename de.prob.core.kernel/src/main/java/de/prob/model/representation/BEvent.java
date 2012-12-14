package de.prob.model.representation;

import org.apache.commons.codec.digest.DigestUtils;

public abstract class BEvent extends AbstractElement {

	private final String name;

	public BEvent(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public String sha() {
		return DigestUtils.shaHex(toString());
	}
}
