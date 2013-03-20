/**
 * 
 */
package de.prob.worksheet.api;

/**
 * @author Rene
 * 
 */
public class WorksheetErrorEvent extends DefaultWorksheetEvent {
	private boolean haltAll;

	/**
	 * Constructor for WorksheetErrorEvent
	 * 
	 * @param id
	 * @param message
	 * @param haltAll
	 */
	public WorksheetErrorEvent(int id, String message, boolean haltAll) {
		super();
		setId(id);
		setMessage(message);
		this.haltAll = haltAll;
	}

	/**
	 * @return the haltAll
	 */
	public boolean isHaltAll() {
		return haltAll;
	}

	/**
	 * @param haltAll
	 *            the haltAll to set
	 */
	public void setHaltAll(final boolean haltAll) {
		this.haltAll = haltAll;
	}
}
