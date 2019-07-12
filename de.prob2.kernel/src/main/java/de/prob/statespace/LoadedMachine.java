package de.prob.statespace;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.prob.animator.command.GetMachineIdentifiersCommand;
import de.prob.animator.command.GetMachineOperationInfos;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;

public class LoadedMachine {

	private final StateSpace stateSpace;
	private Map<String, OperationInfo> machineOperationInfos;
	private List<String> variableNames;
	private List<String> constantNames;
	private List<String> setNames;

	private final Map<FormulaExpand, List<IEvalElement>> variableEvalElements;
	private final Map<FormulaExpand, List<IEvalElement>> constantEvalElements;
	private final Map<FormulaExpand, List<IEvalElement>> setEvalElements;

	public LoadedMachine(StateSpace stateSpace) {
		this.stateSpace = stateSpace;
		this.variableEvalElements = new EnumMap<>(FormulaExpand.class);
		this.constantEvalElements = new EnumMap<>(FormulaExpand.class);
		this.setEvalElements = new EnumMap<>(FormulaExpand.class);
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

	private List<IEvalElement> namesToEvalElements(final List<String> names, final FormulaExpand expand) {
		return names.stream()
			.map(name -> stateSpace.getModel().parseFormula(name, expand))
			.collect(Collectors.toList());
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

	public List<IEvalElement> getVariableEvalElements(final FormulaExpand expand) {
		return variableEvalElements.computeIfAbsent(expand, k -> namesToEvalElements(getVariableNames(), k));
	}

	/**
	 * @deprecated Use {@link #getVariableEvalElements(FormulaExpand)} with an explicit {@link FormulaExpand} argument instead
	 */
	@Deprecated
	public List<IEvalElement> getVariableEvalElements() {
		return getVariableEvalElements(FormulaExpand.EXPAND);
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

	public List<IEvalElement> getConstantEvalElements(final FormulaExpand expand) {
		return constantEvalElements.computeIfAbsent(expand, k -> namesToEvalElements(getConstantNames(), k));
	}

	/**
	 * @deprecated Use {@link #getConstantEvalElements(FormulaExpand)} with an explicit {@link FormulaExpand} argument instead
	 */
	@Deprecated
	public List<IEvalElement> getConstatsEvalElements() {
		return getConstantEvalElements(FormulaExpand.EXPAND);
	}

	public List<String> getSetNames() {
		if (this.setNames == null) {
			GetMachineIdentifiersCommand command = new GetMachineIdentifiersCommand(
					GetMachineIdentifiersCommand.Category.SETS);
			this.stateSpace.execute(command);
			this.setNames = command.getIdentifiers();
		}
		return new ArrayList<>(this.setNames);
	}

	public List<IEvalElement> getSetEvalElements(final FormulaExpand expand) {
		return setEvalElements.computeIfAbsent(expand, k -> namesToEvalElements(getSetNames(), k));
	}

	/**
	 * @deprecated Use {@link #getSetEvalElements(FormulaExpand)} with an explicit {@link FormulaExpand} argument instead
	 */
	@Deprecated
	public List<IEvalElement> getSetEvalElements() {
		return getSetEvalElements(FormulaExpand.EXPAND);
	}
}
