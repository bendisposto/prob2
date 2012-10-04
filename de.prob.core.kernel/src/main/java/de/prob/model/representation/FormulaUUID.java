package de.prob.model.representation;


public class FormulaUUID {
	static int count = 0;
	public final String uuid;

	public FormulaUUID() {
		uuid = "Formula_" + (++count);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FormulaUUID) {
			FormulaUUID that = (FormulaUUID) obj;
			return this.uuid.equals(that.uuid);
		}
		return false;
	}

	@Override
	public String toString() {
		return uuid;
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}
	
}
