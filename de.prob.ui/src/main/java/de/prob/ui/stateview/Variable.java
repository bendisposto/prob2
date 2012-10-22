package de.prob.ui.stateview;

public class Variable {
	private String currentValue;
	private String previousValue;
	private String name;

	public Variable(final String name, final String currentValue,
			final String previousValue) {
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

	public void setCurrentValue(final String currentValue) {
		this.currentValue = currentValue;
	}

	public void setPreviousValue(final String previousValue) {
		this.previousValue = previousValue;
	}
}
