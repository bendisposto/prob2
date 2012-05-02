package de.prob.model.classicalb;

public class RefType {
	private final ERefType relationship;

	public enum ERefType {
		SEES, USES, REFINES, INCLUDES, IMPORTS
	}

	public RefType(final ERefType relationship) {
		this.relationship = relationship;
	}

	@Override
	public String toString() {
		return relationship.toString();
	}
}
