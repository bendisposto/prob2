package de.prob.model.brules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import de.be4.classicalb.core.parser.rules.AbstractOperation;
import de.be4.classicalb.core.parser.rules.ComputationOperation;
import de.be4.classicalb.core.parser.rules.FunctionOperation;
import de.be4.classicalb.core.parser.rules.RuleOperation;
import de.be4.classicalb.core.parser.rules.RulesProject;
import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.exception.ProBError;
import de.prob.statespace.State;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

public class RulesChecker {

	private Trace trace;
	private boolean init = false;
	private final RulesModel rulesModel;
	private final RulesProject rulesProject;

	private final HashMap<AbstractOperation, Set<AbstractOperation>> predecessors = new HashMap<>();
	private final HashMap<AbstractOperation, Set<AbstractOperation>> successors = new HashMap<>();

	private Map<AbstractOperation, OperationState> operationStates;

	public RulesChecker(Trace trace) {
		this.trace = trace;
		this.trace.setExploreStateByDefault(false);
		if (trace.getModel() instanceof RulesModel) {
			rulesModel = (RulesModel) trace.getModel();
			rulesProject = rulesModel.getRulesProject();
			determineDepedencies();
		} else {
			throw new IllegalArgumentException("Expected Rules Model.");
		}
	}

	private void determineDepedencies() {
		for (AbstractOperation op : rulesProject.getOperationsMap().values()) {
			if (!(op instanceof FunctionOperation)) {
				Set<AbstractOperation> set = op.getTransitiveDependencies().stream()
						.filter(p -> !(p instanceof FunctionOperation)).collect(Collectors.toSet());
				predecessors.put(op, set);
				for (AbstractOperation abstractOperation : set) {
					if (!successors.containsKey(abstractOperation)) {
						successors.put(abstractOperation, new HashSet<>());
					}
					successors.get(abstractOperation).add(op);
				}
			}
		}
	}

	public void init() {
		if (!init) {
			// init machine
			while (!trace.getCurrentState().isInitialised()) {
				trace = trace.anyOperation(null);
			}

			// extract current state of all operations
			this.operationStates = evalOperations(trace.getCurrentState(), rulesProject.getOperationsMap().values());
		}
	}

	public void checkAllRules() {
		init();
		// determine all operations that can be executed in this state
		Set<AbstractOperation> executableOperations = getExecutableOperations();
		while (!executableOperations.isEmpty()) {
			for (AbstractOperation op : executableOperations) {
				executeOperation(op);
			}
			executableOperations = getExecutableOperations();
		}
	}

	private OperationState executeOperation(AbstractOperation op) {
		try {
			List<Transition> transitions = trace.getStateSpace()
					.getTransitionsBasedOnParameterValues(trace.getCurrentState(), op.getName(), new ArrayList<>(), 1);
			trace = trace.add(transitions.get(0));
			OperationState opState = evalOperation(trace.getCurrentState(), op);
			this.operationStates.put(op, opState);
			return opState;
		} catch (ProBError | IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}

	private Set<AbstractOperation> getExecutableOperations() {
		final Set<AbstractOperation> todo = new HashSet<>();
		for (Entry<AbstractOperation, OperationState> eval : operationStates.entrySet()) {
			AbstractOperation op = eval.getKey();
			OperationState value = eval.getValue();
			if (value.isNotExecuted() && !value.isDisabled()) {
				boolean canBeExecuted = true;
				for (AbstractOperation pred : predecessors.get(op)) {
					OperationState predState = operationStates.get(pred);
					if (predState.isExecuted()) {
						if (predState instanceof RuleState) {
							RuleState ruleState = (RuleState) predState;
							if (ruleState == RuleState.FAIL) {
								canBeExecuted = false;
								break;
							}
						}
					} else {
						canBeExecuted = false;
						break;
					}
				}
				if (canBeExecuted) {
					todo.add(op);
				}
			}
		}
		return todo;
	}

	public boolean executeOperationAndDependencies(String opName) {
		checkThatOperationExists(opName);
		checkThatOperationIsNotAFunctionOperation(opName);
		AbstractOperation goalOperation = rulesProject.getOperationsMap().get(opName);
		init();
		List<AbstractOperation> executionOrder = goalOperation.getSortedListOfTransitiveDependencies();
		executionOrder.add(goalOperation);
		executionOrder = executionOrder.stream().filter(op -> !(op instanceof FunctionOperation))
				.collect(Collectors.toList());

		List<AbstractOperation> operationsToBeExecuted = new ArrayList<>();
		for (AbstractOperation dep : executionOrder) {
			OperationState operationState = operationStates.get(dep);

			if (operationState.isDisabled()) {
				return false;
			}
			if (dep != goalOperation && operationState == RuleState.FAIL) {
				return false;
			}

			if (operationState.isNotExecuted()) {
				operationsToBeExecuted.add(dep);
			}
		}
		for (AbstractOperation op : operationsToBeExecuted) {
			OperationState opState = executeOperation(op);
			if (op != goalOperation && opState == RuleState.FAIL) {
				return false;
			}
		}
		return true;
	}

	public OperationState evalOperation(State state, AbstractOperation operation) {
		Set<AbstractOperation> set = new HashSet<>();
		set.add(operation);
		Map<AbstractOperation, OperationState> evalOperations = evalOperations(state, set);
		return evalOperations.get(operation);
	}

	public Map<AbstractOperation, OperationState> evalOperations(State state,
			Collection<AbstractOperation> operations) {
		ArrayList<IEvalElement> formulas = new ArrayList<>();
		for (AbstractOperation abstractOperation : operations) {
			if (abstractOperation instanceof ComputationOperation || abstractOperation instanceof RuleOperation) {
				formulas.add(rulesModel.getEvalElement(abstractOperation));
			}
		}
		state.getStateSpace().subscribe(this, formulas);
		Map<IEvalElement, AbstractEvalResult> values = state.getValues();
		final Map<AbstractOperation, OperationState> states = new HashMap<>();
		for (AbstractOperation op : operations) {
			if (op instanceof RuleOperation) {
				states.put(op, RuleState.valueOf(values.get(rulesModel.getEvalElement(op))));
			} else if (op instanceof ComputationOperation) {
				states.put(op, ComputationState.valueOf(values.get(rulesModel.getEvalElement(op))));
			}
		}
		return states;
	}

	private void checkThatOperationIsNotAFunctionOperation(String opName) {
		if (this.rulesProject.getOperationsMap().get(opName) instanceof FunctionOperation) {
			throw new IllegalArgumentException("Function operations are not supported: " + opName);
		}
	}

	public Trace getCurrentTrace() {
		return this.trace;
	}

	public Map<AbstractOperation, OperationState> getOperationStates() {
		return new HashMap<>(this.operationStates);
	}

	public OperationState getOperationState(String opName) {
		checkThatOperationExists(opName);
		checkThatOperationIsNotAFunctionOperation(opName);
		init();
		AbstractOperation abstractOperation = rulesProject.getOperationsMap().get(opName);
		return this.operationStates.get(abstractOperation);
	}

	private void checkThatOperationExists(String opName) {
		if (!rulesProject.getOperationsMap().containsKey(opName)) {
			throw new IllegalArgumentException("Unknown operation name: " + opName);
		}
	}

}
