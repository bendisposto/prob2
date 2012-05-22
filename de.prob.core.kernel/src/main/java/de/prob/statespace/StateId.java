package de.prob.statespace;

public class StateId {
	private final String id;

	public StateId(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof StateId) {
			StateId that = (StateId) obj;
			return that.getId().equals(id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
