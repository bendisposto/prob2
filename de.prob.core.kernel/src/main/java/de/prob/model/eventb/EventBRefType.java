package de.prob.model.eventb;

public class EventBRefType {
	private final EEBRefType relationship;

	public enum EEBRefType {
		SEES, REFINES, EXTENDS
	}

	public EventBRefType(final EEBRefType relationship) {
		this.relationship = relationship;
	}

	@Override
	public String toString() {
		return relationship.toString();
	}

	public EEBRefType getRelationship() {
		return relationship;
	}
}
