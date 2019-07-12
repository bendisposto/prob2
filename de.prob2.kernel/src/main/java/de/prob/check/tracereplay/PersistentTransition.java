package de.prob.check.tracereplay;

import java.util.*;

import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.statespace.OperationInfo;
import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.LoadedMachine;
import de.prob.statespace.State;
import de.prob.statespace.Transition;

public class PersistentTransition {

	private final String name;
	private Map<String, String> params;
	private Map<String, String> results;
	private Map<String, String> destState;
	private Set<String> destStateNotChanged;
	private List<String> preds;

	public PersistentTransition(Transition transition) {
		this(transition, false, null);
	}

	public PersistentTransition(Transition transition, boolean storeDestinationState, PersistentTransition transitionAfter) {
		this.name = transition.getName();
		final LoadedMachine loadedMachine = transition.getStateSpace().getLoadedMachine();
		final State destinationState = transition.getDestination();
		if ("$setup_constants".equals(name)) {
			if (storeDestinationState) {
				addValuesToDestState(destinationState.getConstantValues(FormulaExpand.EXPAND), null);
			}
		} else {
			if (storeDestinationState) {
				addValuesToDestState(destinationState.getVariableValues(FormulaExpand.EXPAND), transitionAfter
				);
			}

			if (!"$initialise_machine".equals(name)) {
				// for each operation
				OperationInfo machineOperationInfo = loadedMachine.getMachineOperationInfo(name);
				params = new HashMap<>();
				for (int i = 0; i < machineOperationInfo.getParameterNames().size(); i++) {
					params.put(machineOperationInfo.getParameterNames().get(i), transition.getParameterValues().get(i));
				}
				results = new HashMap<>();
				for (int i = 0; i < machineOperationInfo.getOutputParameterNames().size(); i++) {
					results.put(machineOperationInfo.getOutputParameterNames().get(i),
							transition.getReturnValues().get(i));
				}
			}
		}
	}

	private void addValuesToDestState(Map<IEvalElement, AbstractEvalResult> map, PersistentTransition transitionAfter) {
		if (destState == null) {
			destState = new HashMap<>();
			destStateNotChanged = new HashSet<>();
		}
		for (Map.Entry<IEvalElement, AbstractEvalResult> entry : map.entrySet()) {
			if (entry.getValue() instanceof EvalResult) {
			    String name = entry.getKey().getCode();
				String value = ((EvalResult) entry.getValue()).getValue();
				destState.put(name, value);
				if(transitionAfter != null && value.equals(transitionAfter.destState.get(name))) {
				    transitionAfter.destState.remove(name);
				    transitionAfter.destStateNotChanged.add(name);
                }
			}
		}
	}

	public String getOperationName() {
		return name;
	}

	public Map<String, String> getParameters() {
		if (this.params == null) {
			return null;
		}
		return new HashMap<>(this.params);
	}

	public Map<String, String> getOuputParameters() {
		if (this.results == null) {
			return null;
		}
		return new HashMap<>(this.results);
	}

	public Map<String, String> getDestinationStateVariables() {
		if (this.destState == null) {
			return null;
		}
		return new HashMap<>(this.destState);
	}

	public List<String> getAdditionalPredicates() {
		if (this.preds == null) {
			return Collections.emptyList();
		}
		return new ArrayList<>(this.preds);
	}

}
