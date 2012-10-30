package de.prob.model.classicalb.newdom;

import de.prob.model.representation.newdom.AbstractElement;

public class Output extends AbstractElement {

	private final String label;

	public Output(final String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
