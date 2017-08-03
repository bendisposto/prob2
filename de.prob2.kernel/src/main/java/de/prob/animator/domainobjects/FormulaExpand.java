package de.prob.animator.domainobjects;

public enum FormulaExpand {
	EXPAND("expand"),
	TRUNCATE("truncate"),
	;
	
	private final String prologName;
	
	private FormulaExpand(final String prologName) {
		this.prologName = prologName;
	}
	
	public String getPrologName() {
		return this.prologName;
	}
}
