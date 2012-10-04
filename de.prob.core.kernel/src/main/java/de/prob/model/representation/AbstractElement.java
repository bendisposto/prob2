package de.prob.model.representation;

import java.util.List;

public interface AbstractElement extends IFormula{

	public String getName();

	public List<String> getConstantNames();

	public List<String> getVariableNames();

	public List<String> getOperationNames();
}
