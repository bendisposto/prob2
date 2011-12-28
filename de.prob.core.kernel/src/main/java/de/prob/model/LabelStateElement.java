package de.prob.model;

class LabelStateElement implements IStateElement {

	private final String label;
	private final String id;

	public LabelStateElement(final String label, final String id) {
		this.label = label;
		this.id = id;
	}

	public String getText() {
		return label;
	}

	public String getId() {
		return id;
	}

}
