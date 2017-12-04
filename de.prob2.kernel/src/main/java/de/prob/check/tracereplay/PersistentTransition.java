package de.prob.check.tracereplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.prob.animator.command.GetMachineOperationInfos.OperationInfo;
import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.LoadedMachine;
import de.prob.statespace.State;
import de.prob.statespace.Transition;

public class PersistentTransition {

	private final String name;
	private final Map<String, String> params = new LinkedHashMap<>();
	private final Map<String, String> results = new LinkedHashMap<>();
	private final Map<String, String> destState = new LinkedHashMap<>();
	private List<String> preds = new ArrayList<>();

	public PersistentTransition(Transition transition) {
		this(transition, false);
	}

	public PersistentTransition(Transition transition, boolean storeDestinationState) {
		this.name = transition.getName();
		final LoadedMachine loadedMachine = transition.getStateSpace().getLoadedMachine();
		final State destinationState = transition.getDestination();
		if (name.equals("$setup_constants")) {
			if (storeDestinationState) {
				addValuesToDestState(destinationState.getConstantValues());
			}

		} else {
			if (storeDestinationState) {
				addValuesToDestState(destinationState.getVariableValues());
			}

			if (name.equals("$initialise_machine")) {

			} else {
				// for each operation
				OperationInfo machineOperationInfo = loadedMachine.getMachineOperationInfo(name);
				for (int i = 0; i < machineOperationInfo.getParameterNames().size(); i++) {
					params.put(machineOperationInfo.getParameterNames().get(i), transition.getParameterValues().get(i));
				}
				for (int i = 0; i < machineOperationInfo.getOutputParameterNames().size(); i++) {
					results.put(machineOperationInfo.getOutputParameterNames().get(i),
							transition.getReturnValues().get(i));
				}
			}
		}
	}

	private void addValuesToDestState(Map<IEvalElement, AbstractEvalResult> map) {
		for (Entry<IEvalElement, AbstractEvalResult> entry : map.entrySet()) {
			if (entry.getValue() instanceof EvalResult) {
				EvalResult evalResult = (EvalResult) entry.getValue();
				destState.put(entry.getKey().getCode(), evalResult.getValue());
			}
		}
	}

	public String getOperationName() {
		return name;
	}

	public Map<String, String> getParameters() {
		return new HashMap<>(this.params);
	}

	public Map<String, String> getOuputParameters() {
		return new HashMap<>(this.results);
	}

	public Map<String, String> getDestinationStateVariables() {
		return new HashMap<>(this.destState);
	}

	public List<String> getAdditionalPredicates() {
		return this.preds;
	}

}
