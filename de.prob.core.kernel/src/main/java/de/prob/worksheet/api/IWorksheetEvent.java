/**
 * 
 */
package de.prob.worksheet.api;

/**
 * This interface defines the structure of all events for the worksheet
 * 
 * @author Rene
 * 
 */
public interface IWorksheetEvent {
	/**
	 * Returns the id of this event
	 * 
	 * @return the id of this event
	 */
	public int getId();

	/**
	 * Sets the id of this event
	 * 
	 * @param id
	 *            of this event
	 */
	public void setId(int id);

	/**
	 * @return the message
	 */
	public String getMessage();

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(final String message);

}
