package de.prob.model.representation;

public class CSPElement extends AbstractElement implements Named {
	private final String name;

	public CSPElement(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
