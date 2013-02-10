package de.prob.worksheet.api;

/**
 * This interface defines the listeners for the worksheet apis
 * 
 * @author Rene
 * 
 */
public interface IWorksheetAPIListener {

	/**
	 * Sends an event to this listener
	 * 
	 * @param event
	 *            to be send
	 */
	public void notify(IWorksheetEvent event);
}
