package de.prob.statespace.derived;

import java.util.List;

import de.prob.statespace.StateId;

public class DerivedStateId extends StateId {

	private final List<String> labels;
	private int count;

	public DerivedStateId(final String id, final List<String> labels,
			final int count) {
		super(id, null);
		this.labels = labels;
		this.count = count;
	}

	public List<String> getLabels() {
		return labels;
	}

	@Override
	public String toString() {
		return id + ": " + labels;
	}

	public int getCount() {
		return count;
	}

	public void setCount(final int count) {
		this.count = count;
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
