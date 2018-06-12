package de.prob.statespace;

import java.util.List;

public class OperationInfo {
	private final String operationName;
	private final List<String> parameterNames;
	private final List<String> outputParameterNames;

	public OperationInfo(String opName, List<String> parameterNames, List<String> outputParameterNames) {
		this.operationName = opName;
		this.parameterNames = parameterNames;
		this.outputParameterNames = outputParameterNames;
	}

	public String getOperationName() {
		return operationName;
	}

	public List<String> getParameterNames() {
		return parameterNames;
	}

	public List<String> getOutputParameterNames() {
		return outputParameterNames;
	}

	@Override
	public String toString() {
		return String.format("[opName: %s, params: [%s], outputParams: [%s]]", operationName,
				String.join(", ", parameterNames), String.join(", ", outputParameterNames));
	}
}
