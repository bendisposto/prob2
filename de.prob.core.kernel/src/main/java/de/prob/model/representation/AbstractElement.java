package de.prob.model.representation;

import java.util.List;

public interface AbstractElement {

	public String getName();

	public List<String> getConstants();

	public List<String> getVariables();

	public List<String> getOperations();
}
