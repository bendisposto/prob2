/**
 * 
 */
package de.prob.worksheet.api;

/**
 * @author Rene
 * 
 */
public class DefaultWorksheetEvent implements IWorksheetEvent {

	private Integer id;
	private String name;
	private String description;
	private String message;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.api.IWorksheetEvent#getId()
	 */
	@Override
	public int getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.api.IWorksheetEvent#setId(int)
	 */
	@Override
	public void setId(final int id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.api.IWorksheetEvent#setName(java.lang.String)
	 */
	@Override
	public void setName(final String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.api.IWorksheetEvent#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.api.IWorksheetEvent#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.api.IWorksheetEvent#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

}
