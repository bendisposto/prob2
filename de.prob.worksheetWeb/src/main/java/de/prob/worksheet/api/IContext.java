package de.prob.worksheet.api;

import java.util.HashMap;

/**
 * The interface defines a simple <b>storage</b> for information which are
 * needed to initialize an evaluation Context like a scripting engine or a
 * worksheet evaluator.
 * 
 * It is most used for storing context information of a worksheet evaluator but
 * is concepted to be used (in later versions) by any Execution Context like
 * Groovy Execution or JSR 223 compliant Engine
 * 
 * @author Rene
 * @see de.prob.worksheet.api.evalStore.EvalStoreContext
 */
public interface IContext {

	/**
	 * Returns the ID of this context.
	 * 
	 * @return the id of the context
	 */
	public String getId();

	/**
	 * Sets the ID of this context
	 * 
	 * @param contextId
	 *            of this context
	 */
	public void setId(String contextId);

	/**
	 * Returns a HashMap containing the binding. Each binding is represented by
	 * a name(String) and value(Object) pair.
	 * 
	 * @return the Bindings of this context contained in a HashMap
	 */
	public HashMap<String, Object> getBindings();

	/**
	 * Sets the Bindings for this Context.
	 * 
	 * @param bindings
	 *            A HashMap with the bindings for this context
	 */
	public void setBindings(HashMap<String, Object> bindings);

	/**
	 * Returns a named binding from this context
	 * 
	 * @param name
	 *            of a binding to be retrieved
	 * @return the Object associated with teh specified name
	 */
	public Object getBinding(String name);

	/**
	 * Sets a named binding for this context. If the name is already in use, the
	 * name will be bound to the new Object.
	 * 
	 * @param name
	 *            of a binding to be set
	 * @param binding
	 *            to set for the name
	 */
	public void setBinding(String name, Object binding);

	/**
	 * Compares the bindings of this Context to another context.
	 * 
	 * @param obj
	 *            The Context to compare with
	 * @return true if the bindings of both contexts are equal
	 */
	public boolean equalsBindings(Object obj);

	/**
	 * Compares to IContext Object
	 * 
	 * @param obj
	 *            the context to compare this against
	 * @return true if both contexts are equal
	 */
	@Override
	public boolean equals(Object obj);

	/**
	 * Returns a clone of this Context without cloning bindings
	 * 
	 * @return the duplicate
	 */
	public IContext getDuplicate();
}
