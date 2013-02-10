/**
 * 
 */
package de.prob.worksheet.api;

/**
 * @author Rene
 * 
 */
public class WorksheetActionEvent extends DefaultWorksheetEvent {
	private Object dataBefore;
	private Object dataAfter;

	/** 
	 * @return the dataBefore
	 */
	public Object getDataBefore() {
		return this.dataBefore;
	}

	/**
	 * @param dataBefore
	 *            the dataBefore to set
	 */
	public void setDataBefore(final Object dataBefore) {
		this.dataBefore = dataBefore;
	}

	/**
	 * @return the dataAfter
	 */
	public Object getDataAfter() {
		return this.dataAfter;
	}

	/**
	 * @param dataAfter
	 *            the dataAfter to set
	 */
	public void setDataAfter(final Object dataAfter) {
		this.dataAfter = dataAfter;
	}
}
