package de.prob.statespace;

import java.util.List;

import com.google.common.base.MoreObjects;

public class OperationInfo {
	private final String operationName;
	private final List<String> parameterNames;
	private final List<String> outputParameterNames;
	private final List<String> readVariables;
	private final List<String> writtenVariables;
	private final List<String> nonDetWrittenVariables;

	public OperationInfo(
		final String operationName,
		final List<String> parameterNames,
		final List<String> outputParameterNames,
		final List<String> readVariables,
		final List<String> writtenVariables,
		final List<String> nonDetWrittenVariables
	) {
		this.operationName = operationName;
		this.parameterNames = parameterNames;
		this.outputParameterNames = outputParameterNames;
		this.readVariables = readVariables;
		this.writtenVariables = writtenVariables;
		this.nonDetWrittenVariables = nonDetWrittenVariables;
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

	public List<String> getReadVariables() {
		return this.readVariables;
	}

	public List<String> getWrittenVariables() {
		return this.writtenVariables;
	}

	public List<String> getNonDetWrittenVariables() {
		return this.nonDetWrittenVariables;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("operationName", this.operationName)
			.add("parameterNames", this.parameterNames)
			.add("outputParameterNames", this.outputParameterNames)
			.add("readVariables", this.readVariables)
			.add("writtenVariables", this.writtenVariables)
			.add("nonDetWrittenVariables", this.nonDetWrittenVariables)
			.toString();
	}
}
