package de.prob.model.representation.newdom;

public abstract class Action extends AbstractElement {

	private final String code;

	public Action(final String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
