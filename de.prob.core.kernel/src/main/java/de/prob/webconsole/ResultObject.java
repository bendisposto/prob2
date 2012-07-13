package de.prob.webconsole;

import java.util.ArrayList;
import java.util.List;

public class ResultObject {
	private String output;
	private List<String> imports = new ArrayList<String>();
	private boolean continued = false;
	private List<String> newBindings = new ArrayList<String>();

	public void setImports(List<String> imports) {
		this.imports.clear();
		for (String string : imports) {
			this.imports.add(string.substring(6, string.length() - 1));
		}
	}

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

	public void addBindings(String newvar) {
		newBindings.add(newvar);
	}

}
