package de.prob.model.eventb.proof;

public class Tuple {

	private final String type;
	private final String second;

	public Tuple(final String type, final String value) {
		this.type = type;
		second = value;
	}

	public String getFirst() {
		return type;
	}

	public String getSecond() {
		return second;
	}

}
