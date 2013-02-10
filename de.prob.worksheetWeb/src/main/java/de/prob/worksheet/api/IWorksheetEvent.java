/**
 * 
 */
package de.prob.worksheet.api;

/**
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
	 * @deprecated
	 * @param name
	 */
	public void setName(String name);

	/**
	 * @deprecated
	 * @return
	 */
	public String getName();

	/**
	 * @deprecated
	 * @return
	 */
	public String getDescription();

	/**
	 * @deprecated
	 * @param description
	 */
	public void setDescription(String description);

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
