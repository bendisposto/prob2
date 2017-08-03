package de.prob.animator.domainobjects;

public enum FormulaExpand {
	expand("expand"),
	truncate("truncate"),
	;
	
	private final String prologName;
	
	private FormulaExpand(final String prologName) {
		this.prologName = prologName;
	}
	
	public String getPrologName() {
		return this.prologName;
	}
}
