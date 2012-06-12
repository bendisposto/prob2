package de.prob.statespace;

public class StateId {
	private final String id;
	private final String hash;

	public StateId(final String id, final String state) {
		this.id = id;
		this.hash = hash(state);
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
		// if (hash == null)
		return id.hashCode();
	}

	public String hash(final String vars) {
		return null;
	}
}
