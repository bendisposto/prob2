package de.prob.statespace.derived;

import de.prob.statespace.StateId;

public class DerivedStateId extends StateId {

	private final String label;
	private final boolean invOk;

	public DerivedStateId(final String id, final String label,
			final boolean invOk) {
		super(id, null);
		this.label = label;
		this.invOk = invOk;
	}

	public String getLabel() {
		return label;
	}

	public boolean isInvOk() {
		return invOk;
	}

	@Override
	public String toString() {
		return id + ": " + label;
	}

	@Override
	public boolean equals(final Object that) {
		if (that instanceof DerivedStateId) {
			return this.getId().equals(((DerivedStateId) that).getId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
