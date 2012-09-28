package de.prob.ui.stateview;

public class Variable {
	private String currentValue;
	private String previousValue;
	private String name;

	public Variable(String name, String currentValue, String previousValue) {
		this.name = name;
		this.currentValue = currentValue;
		this.previousValue = previousValue;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCurrentValue() {
		return currentValue;
	}
	
	public String getPreviousValue() {
		return previousValue;
	}
}
