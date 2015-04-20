package de.prob.model.representation;

public class CSPElement extends AbstractElement {
	private final String name;

	public CSPElement(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
