package de.prob.worksheet.api;

/**
 * IWorksheetAPI is the default interface for APIs developed for the Worksheet
 * Evaluators
 * 
 * @author Rene
 * 
 */
public interface IWorksheetAPI {

	/**
	 * Notifies all registered ErrorListeners
	 * 
	 * @param id
	 *            of the error
	 * @param message
	 *            for the error
	 * @param haltAll
	 *            flag that tells the Listener to stop evaluation of further
	 *            blocks
	 */
	public abstract void notifyErrorListeners(int id, String message,
			boolean haltAll);

	/**
	 * Registeres an errorListener for this APIs events
	 * 
	 * @param listener
	 *            to be registered
	 */
	public abstract void addErrorListener(IWorksheetAPIListener listener);

	/**
	 * Unregisteres an errorListener for this APIs events
	 * 
	 * @param listener
	 *            to be removed
	 */
	public abstract void removeErrorListener(IWorksheetAPIListener listener);

	/**
	 * Notifies all registered OutputListeners
	 * 
	 * @param id
	 *            of the event
	 * @param message
	 *            for the event
	 * @param outputBlockType
	 *            used for creation of a new OutputBlock
	 * @param dataObject
	 *            for additional data
	 */
	public abstract void notifyOutputListeners(int id, String message,
			String outputBlockType, Object dataObject);

	/**
	 * Register an OutputListener for this APIs events
	 * 
	 * @param listener
	 *            to be registered
	 */
	public abstract void addOutputListener(IWorksheetAPIListener listener);

	/**
	 * Unregister an OutputListener for this APIs events
	 * 
	 * @param listener
	 *            to be removed
	 */
	public abstract void removeOutputListener(IWorksheetAPIListener listener);

	/**
	 * Notifies all registered ActionListeners
	 * 
	 * @param id
	 *            of the event
	 * @param message
	 *            of the event
	 * @param dataBefore
	 *            data before the action was done
	 * @param dataAfter
	 *            data that has changed
	 */
	public abstract void notifyActionListeners(int id, String message,
			Object dataAfter);

	/**
	 * Registeres an ActionListern for this APIs events
	 * 
	 * @param listener
	 *            to be registered
	 */
	public abstract void addActionListener(IWorksheetAPIListener listener);

	/**
	 * Unregisteres an ActionListener
	 * 
	 * @param listener
	 *            to be removed
	 */
	public abstract void removeActionListener(IWorksheetAPIListener listener);

	public abstract void setContext(IContext context);

}