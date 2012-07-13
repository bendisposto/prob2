package de.prob.webconsole;

public class ResultObject {
	private String output;
	private boolean continued = false;

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public boolean isContinued() {
		return continued;
	}

	public void setContinued(boolean continued) {
		this.continued = continued;
	}

}
