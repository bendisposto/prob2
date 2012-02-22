package de.prob.model;

public class Operation {
	private final String id;
	private final String name;
	private final String params;

	public Operation(final String id, final String name, final String params) {
		this.id = id;
		this.name = name;
		this.params = params;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return name + "(" + params + ")";
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Operation) {
			Operation that = (Operation) obj;
			boolean b = that.id.equals(id);
			return b;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
