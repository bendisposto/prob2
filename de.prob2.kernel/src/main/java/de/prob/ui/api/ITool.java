package de.prob.ui.api;

import java.util.List;

/**
 * API for integration of ProB visualization into other tools. A tool has to
 * implement this interface and register itself in the ToolRegistry.
 */
public interface ITool {

	/**
	 * doStep is called by the user interface if an event/operation/action is
	 * triggered, e.g. through a button click in the visualization. The UI
	 * provides the state in which the event is triggered, the name of the
	 * action and some parameters.
	 * 
	 * The tool performs the action and returns a reference for the new state.
	 * If the event cannot be executed the tool should raise an
	 * ImpossibleStepException
	 * 
	 * The stateref parameter is a string that is meaningful for the tool. The
	 * UI will send the stateref back to the tool each time it requests state
	 * specific information.
	 * 
	 * In order to do caching on the UI side, the tool should send different
	 * staterefs for different states.
	 * 
	 * The event/parameters are a matter of the protocol between visualizations
	 * and the tool. Usually doing a step requires an operation name and some
	 * parameters, but the interpretation of the Strings are up to the tool.
	 * 
	 * @param stateref a reference to the state to operate on
	 * @param event the event to execute
	 * @param parameters parameters for the event
	 * @return Reference of the next state
	 * @throws ImpossibleStepException if this step is not possible
	 */
	public String doStep(String stateref, String event, String... parameters)
			throws ImpossibleStepException;

	/**
	 * evaluate is called by the user interface to compute the values of an
	 * expression or predicate. The formula has to be valid in that particular
	 * formalism, i.e., it is syntactically correct and well-typed.
	 * 
	 * @param stateref see {@link #doStep(String, String, String...)}
	 * @param formula the formula to evaluate
	 * @return the result of the evaluation
	 * @throws IllegalFormulaException
	 *             if the formula is illegal
	 */
	public Object evaluate(String stateref, String formula)
			throws IllegalFormulaException;

	/**
	 * Checks if a given formula contains errors. If the formula is valid this
	 * method must return an empty list.
	 * 
	 * @param state see {@link #doStep(String, String, String...)}
	 * @param formula the formula to get errors for
	 * @return A list of error messages associated with the formula in the given
	 *         state.
	 */
	public List<String> getErrors(String state, String formula);

	/**
	 * Get a reference to the current state.
	 * @return the reference String for the current state
	 */
	public String getCurrentState();

	/**
	 * Check whether backtracking is possible.
	 * @return true if and only if the tool is able to evaluate a formula in a
	 *         different state than the current one.
	 */
	public boolean canBacktrack();

	/**
	 * Get a unique name for this tool.
	 * @return the String representation that specifies this exact instance of
	 *         the ITool. This should be as unique as possible.
	 */
	public String getName();

}
