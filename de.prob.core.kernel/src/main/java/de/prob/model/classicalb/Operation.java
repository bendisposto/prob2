package de.prob.model.classicalb;

import java.util.List;

import com.google.common.base.Joiner;

import de.prob.model.representation.IEntity;
import de.prob.model.representation.Label;

public class Operation extends Label {

	private final Label output;
	private final Label parameters;

	public Operation(final String name, final Label output,
			final Label parameters) {
		super(name);
		this.output = output;
		this.parameters = parameters;
	}

	public Label output() {
		return output;
	}

	public Label parameters() {
		return parameters;
	}

	@Override
	public String toString() {
		final List<IEntity> outChildren = output.getChildren();
		final String returnValues = outChildren.isEmpty() ? "" : Joiner.on(',')
				.join(outChildren) + "<--";
		final List<IEntity> paramChildren = parameters.getChildren();
		final String params = paramChildren.isEmpty() ? "" : "("
				+ Joiner.on(',').join(paramChildren) + ")";
		return returnValues + name + params;
	}
}
