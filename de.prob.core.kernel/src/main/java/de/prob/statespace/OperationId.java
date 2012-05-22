package de.prob.statespace;

public class OperationId {
	private final String id;

	public OperationId(final String id) {
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
		if (obj instanceof OperationId) {
			OperationId that = (OperationId) obj;
			return that.getId().equals(id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
