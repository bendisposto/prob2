package de.prob.model.eventb.proof;

public class Tuple {

	private final String type;
	private final String value;

	public Tuple(final String type, final String value) {
		this.type = type;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

}
