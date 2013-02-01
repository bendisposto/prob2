package de.prob.scripting;

import com.google.inject.Inject;

import de.prob.model.representation.AbstractElement;
import de.prob.statespace.History;
import de.prob.statespace.StateSpace;

public class CSPModel extends AbstractElement {

	private final StateSpace statespace;
	private String content;

	@Inject
	public CSPModel(final StateSpace statespace) {
		this.statespace = statespace;
	}

	public void init(final String content) {
		this.content = content;
		statespace.setModel(this);
	}

	public String getContent() {
		return content;
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
