package de.prob.ui.stateview;

public class Variable {
	private String value;
	private String name;

	public Variable(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
}
