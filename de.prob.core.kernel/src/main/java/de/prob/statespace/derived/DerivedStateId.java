package de.prob.statespace.derived;

import de.prob.statespace.StateId;

public class DerivedStateId extends StateId {

	private final String label;
	private int count;

	public DerivedStateId(final String id, final String label,
			final String witness, final String count) {
		super(id, null);
		this.label = label;
		this.count = Integer.parseInt(count);

	}

	public String getLabel() {
		return label;
	}

	public int getCount() {
		return count;
	}

	public void setCount(final int count) {
		this.count = count;
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
