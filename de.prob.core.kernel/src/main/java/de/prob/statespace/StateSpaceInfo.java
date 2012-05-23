package de.prob.statespace;

import java.util.HashMap;
import java.util.Set;

import de.prob.animator.command.ExploreStateCommand;

public class StateSpaceInfo {
	private final HashMap<OperationId, Operation> ops = new HashMap<OperationId, Operation>();
	private final HashMap<StateId, HashMap<String, String>> variables = new HashMap<StateId, HashMap<String, String>>();
	private final HashMap<StateId, Boolean> invariantOk = new HashMap<StateId, Boolean>();
	private final HashMap<StateId, Boolean> timeoutOccured = new HashMap<StateId, Boolean>();
	private final HashMap<StateId, Set<String>> operationsWithTimeout = new HashMap<StateId, Set<String>>();

	/**
	 * Records an Operation (op) that corresponding to the given operation id.
	 * 
	 * @param id
	 * @param op
	 */
	public void add(final String id, final Operation op) {
		ops.put(new OperationId(id), op);
	}

	/**
	 * Records the map of variables (vars) corresponding to the given state id.
	 * 
	 * @param id
	 * @param vars
	 */
	public void add(final String id, final HashMap<String, String> vars) {
		variables.put(new StateId(id), vars);
	}

	/**
	 * Records if the invariant (invOK) at a given state id is ok.
	 * 
	 * @param id
	 * @param invOK
	 */
	public void addInvOk(final String id, final Boolean invOK) {
		invariantOk.put(new StateId(id), invOK);
	}

	/**
	 * Records if a timeout occurred (tOccured) for the given id
	 * 
	 * @param id
	 * @param tOccured
	 */
	public void addTimeOcc(final String id, final Boolean tOccured) {
		// FIXME is this id a state id or an operation id
		timeoutOccured.put(new StateId(id), tOccured);
	}

	/**
	 * Records the operations with timeout (opsWT) that correspond to the given
	 * id
	 * 
	 * @param id
	 * @param opsWT
	 */
	public void add(final String id, final Set<String> opsWT) {
		operationsWithTimeout.put(new StateId(id), opsWT);
	}

	/**
	 * Takes a processed ExploreStateCommand and extracts the values for the
	 * variables, invariantOk, timeoutOccured, and the operationsWithTimeout
	 * 
	 * @param stateId
	 * @param command
	 */
	public void add(final String stateId, final ExploreStateCommand command) {
		variables.put(new StateId(stateId), command.getVariables());
		invariantOk.put(new StateId(stateId), command.isInvariantOk());
		timeoutOccured.put(new StateId(stateId), command.isTimeoutOccured());
		operationsWithTimeout.put(new StateId(stateId),
				command.getOperationsWithTimeout());
	}

	public HashMap<StateId, Boolean> getInvariantOk() {
		return invariantOk;
	}

	public HashMap<StateId, Boolean> getTimeoutOccured() {
		return timeoutOccured;
	}

	public HashMap<StateId, Set<String>> getOperationsWithTimeout() {
		return operationsWithTimeout;
	}

	/**
	 * Returns the map representation of the variables for the given state id
	 * 
	 * @param stateId
	 * @return returns the variables at the given state
	 */
	public HashMap<String, String> getState(final String stateId) {
		return variables.get(new StateId(stateId));
	}

	public HashMap<String, String> getState(final int stateId) {
		String id = String.valueOf(stateId);
		return getState(id);
	}

	public HashMap<OperationId, Operation> getOps() {
		return ops;
	}

	public Operation getOp(final String opId) {
		return getOp(new OperationId(opId));
	}

	public Operation getOp(final OperationId opId) {
		return ops.get(opId);
	}

	@Override
	public String toString() {
		String result = "";
		result += "Operations: \n  " + ops.toString() + "\n";
		result += "Variables: \n  " + variables.toString() + "\n";
		result += "Invariants Ok: \n  " + invariantOk.toString() + "\n";
		result += "Timeout Occured: \n  " + timeoutOccured.toString() + "\n";
		result += "Operations With Timeout: \n  "
				+ operationsWithTimeout.toString() + "\n";
		return result;
	}

	public String getVariable(final String stateId, final String variable) {
		return variables.get(new StateId(stateId)).get(variable);
	}
}
