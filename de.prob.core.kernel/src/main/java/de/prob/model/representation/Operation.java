package de.prob.model.representation;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import de.prob.model.classicalb.ClassicalBEntity;

public class Operation {

	private final String name;
	private final List<ClassicalBEntity> output = new ArrayList<ClassicalBEntity>();
	private final List<ClassicalBEntity> parameters = new ArrayList<ClassicalBEntity>();

	public Operation(final String name) {
		this.name = name;
	}

	public List<ClassicalBEntity> output() {
		return output;
	}

	public List<ClassicalBEntity> parameters() {
		return parameters;
	}

	@Override
	public String toString() {
		String returnValues = output.isEmpty() ? "" : Joiner.on(',').join(
				output)
				+ "<--";
		String params = parameters.isEmpty() ? "" : "("
				+ Joiner.on(',').join(parameters) + ")";
		return returnValues + name + params;
	}
}
