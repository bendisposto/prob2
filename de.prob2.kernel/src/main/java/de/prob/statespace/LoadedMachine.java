package de.prob.statespace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.prob.animator.command.GetMachineIdentifiersCommand;
import de.prob.animator.command.GetMachineOperationInfos;
import de.prob.animator.command.GetMachineOperationInfos.OperationInfo;
import de.prob.animator.domainobjects.IEvalElement;

public class LoadedMachine {

	private final StateSpace stateSpace;
	private Map<String, OperationInfo> machineOperationInfos;
	private List<String> variableNames;
	private List<String> constantNames;

	private List<IEvalElement> variableEvalElements;
	private List<IEvalElement> constantEvalElements;

	public LoadedMachine(StateSpace stateSpace) {
		this.stateSpace = stateSpace;
	}

	public boolean containsOperations(String name) {
		return getOperations().containsKey(name);
	}

	public Set<String> getOperationNames() {
		return new HashSet<>(getOperations().keySet());
	}

	public OperationInfo getMachineOperationInfo(String operationName) {
		return getOperations().get(operationName);
	}

	private Map<String, OperationInfo> getOperations() {
		if (this.machineOperationInfos == null) {
			GetMachineOperationInfos command = new GetMachineOperationInfos();
			this.stateSpace.execute(command);
			this.machineOperationInfos = command.getOperationInfos().stream()
					.collect(Collectors.toMap(OperationInfo::getOperationName, i -> i));
		}
		return this.machineOperationInfos;
	}

	public List<String> getVariableNames() {
		if (this.variableNames == null) {
			GetMachineIdentifiersCommand command = new GetMachineIdentifiersCommand(
					GetMachineIdentifiersCommand.Category.VARIABLES);
			this.stateSpace.execute(command);
			this.variableNames = command.getIdentifiers();
		}
		return new ArrayList<>(this.variableNames);
	}

	public List<IEvalElement> getVariableEvalElements() {
		if (variableEvalElements == null) {
			variableEvalElements = new ArrayList<>();
			for (String string : getVariableNames()) {
				variableEvalElements.add(stateSpace.getModel().parseFormula(string));
			}
		}
		return variableEvalElements;
	}

	public List<String> getConstantNames() {
		if (this.constantNames == null) {
			GetMachineIdentifiersCommand command = new GetMachineIdentifiersCommand(
					GetMachineIdentifiersCommand.Category.CONSTANTS);
			this.stateSpace.execute(command);
			this.constantNames = command.getIdentifiers();
		}
		return new ArrayList<>(this.constantNames);
	}

	public List<IEvalElement> getConstantEvalElements() {
		if (constantEvalElements == null) {
			constantEvalElements = new ArrayList<>();
			for (String string : getConstantNames()) {
				constantEvalElements.add(stateSpace.getModel().parseFormula(string));
			}
		}
		return constantEvalElements;
	}
	
}
