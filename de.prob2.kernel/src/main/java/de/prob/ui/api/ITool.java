package de.prob.ui.api;

import java.util.List;

/**
 * API for integration of ProB visualization into other tools. A tool has to
 * implement this interface and register itself in the ToolRegistry.
 * 
 * 
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
	 * @param stateref
	 * @param event
	 * @param parameters
	 * @return Reference of the next state
	 * @throws ImpossibleStepException
	 */
	public String doStep(String stateref, String event, String... parameters)
			throws ImpossibleStepException;

	/**
	 * evaluate is called by the user interface to compute the values of an
	 * expression or predicate. The formula has to be valid in that particular
	 * formalism, i.e., it is syntactically correct and well-typed.
	 * 
	 * @param stateref
	 *            see {@link ITool#doStep}
	 * @param formula
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
	 * @param state
	 * @param Formula
	 * @return A list of error messages associated with the formula in the given
	 *         state.
	 */
	public List<String> getErrors(String state, String Formula);

	/**
	 * @return the reference String for the current state
	 */
	public String getCurrentState();

	/**
	 * @return true if and only if the tool is able to evaluate a formula in a
	 *         different state than the current one.
	 */
	public boolean canBacktrack();

	/**
	 * @return the String representation that specifies this exact instance of
	 *         the ITool. This should be as unique as possible.
	 */
	public String getName();

}
