package de.prob.animator.domainobjects;

import java.util.List;

public class TypeCheckResult {

	private final String type;
	private final List<String> errors;

	public TypeCheckResult(String type, List<String> errors) {
		this.type = type;
		this.errors = errors;
	}

	public String getType() {
		return type;
	}

	public List<String> getErrors() {
		return errors;
	}

	public boolean isOk() {
		return errors.isEmpty();
	}
}
