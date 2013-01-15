/**
 * 
 */
package de.prob.worksheet.api.state;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Rene
 * 
 */
public class State {
	HashMap<String, String>	variables;

	/**
	 * 
	 */
	public State() {
		this.variables = new HashMap<String, String>();
	}

	/**
	 * @param varName
	 * @return
	 */
	public boolean hasVariable(final String varName) {
		return this.variables.containsKey(varName);
	}

	/**
	 * @param varName
	 */
	public void removeVariable(final String varName) {
		this.variables.remove(varName);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected State clone() {
		final State clone = new State();
		clone.variables = (HashMap<String, String>) this.variables.clone();
		return clone;
	}

	/**
	 * @param varName
	 * @param value
	 */
	public void addVariable(final String varName, final String value) {
		this.variables.put(varName, value);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String out = "";
		final Set<Entry<String, String>> entries = this.variables.entrySet();
		for (final Entry<String, String> variable : entries) {
			out += "&#09;";
			out += variable.getKey();
			out += " : ";
			out += variable.getValue();
			out += "</br>";
		}
		return out;
	}
}
