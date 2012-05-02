package de.prob.statespace;

import java.util.HashMap;
import java.util.Set;

import de.prob.animator.command.ExploreStateCommand;

public class StateSpaceInfo {
	private final HashMap<String, Operation> ops = new HashMap<String, Operation>();
	private final HashMap<String, HashMap<String, String>> variables = new HashMap<String, HashMap<String, String>>();
	private final HashMap<String, Boolean> invariantOk = new HashMap<String, Boolean>();
	private final HashMap<String, Boolean> timeoutOccured = new HashMap<String, Boolean>();
	private final HashMap<String, Set<String>> operationsWithTimeout = new HashMap<String, Set<String>>();

	/**
	 * Records an Operation (op) that corresponding to the given state id.
	 * 
	 * @param id
	 * @param op
	 */
	public void add(String id, Operation op) {
		ops.put(id, op);
	}

	/**
	 * Records the map of variables (vars) corresponding to the given state id.
	 * 
	 * @param id
	 * @param vars
	 */
	public void add(String id, HashMap<String, String> vars) {
		variables.put(id, vars);
	}

	/**
	 * Records if the invariant (invOK) at a given state id is ok.
	 * 
	 * @param id
	 * @param invOK
	 */
	public void addInvOk(String id, Boolean invOK) {
		invariantOk.put(id, invOK);
	}

	/**
	 * Records if a timeout occurred (tOccured) for the given id
	 * 
	 * @param id
	 * @param tOccured
	 */
	public void addTimeOcc(String id, Boolean tOccured) {
		// FIXME is this id a state id or an operation id
		timeoutOccured.put(id, tOccured);
	}

	/**
	 * Records the operations with timeout (opsWT) that correspond to the given
	 * id
	 * 
	 * @param id
	 * @param opsWT
	 */
	public void add(String id, Set<String> opsWT) {
		operationsWithTimeout.put(id, opsWT);
	}

	/**
	 * Takes a processed ExploreStateCommand and extracts the values for the
	 * variables, invariantOk, timeoutOccured, and the operationsWithTimeout
	 * 
	 * @param stateId
	 * @param command
	 */
	public void add(String stateId, ExploreStateCommand command) {
		variables.put(stateId, command.getVariables());
		invariantOk.put(stateId, command.isInvariantOk());
		timeoutOccured.put(stateId, command.isTimeoutOccured());
		operationsWithTimeout.put(stateId, command.getOperationsWithTimeout());
	}

	public HashMap<String, Boolean> getInvariantOk() {
		return invariantOk;
	}

	public HashMap<String, Boolean> getTimeoutOccured() {
		return timeoutOccured;
	}

	public HashMap<String, Set<String>> getOperationsWithTimeout() {
		return operationsWithTimeout;
	}

	/**
	 * Returns the map representation of the variables for the given state id
	 * 
	 * @param stateId
	 * @return
	 */
	public HashMap<String, String> getState(final String stateId) {
		return variables.get(stateId);
	}

	public HashMap<String, String> getState(final int stateId) {
		String id = String.valueOf(stateId);
		return getState(id);
	}

	public HashMap<String, Operation> getOps() {
		return ops;
	}

	public Operation getOp(final String opId) {
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

}
