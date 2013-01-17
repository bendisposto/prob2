package de.prob.scripting;

import com.google.inject.Inject;

import de.prob.model.representation.AbstractElement;
import de.prob.statespace.History;
import de.prob.statespace.StateSpace;

public class CSPModel extends AbstractElement {

	private final StateSpace statespace;
	private String filename = "";

	@Inject
	public CSPModel(final StateSpace statespace) {
		this.statespace = statespace;
	}

	public void init(final String filename) {
		this.filename = filename;
		statespace.setModel(this);
	}

	public String getFilename() {
		return filename;
	}

	public Object asType(final Class<?> className) {
		if (className.getSimpleName().equals("StateSpace")) {
			return statespace;
		}
		if (className.getSimpleName().equals("History")) {
			return new History(statespace);
		}
		throw new ClassCastException("No element of type " + className
				+ " found.");
	}

	public StateSpace getStatespace() {
		return statespace;
	}
}
