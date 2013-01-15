/**
 * 
 */
package de.prob.worksheet.api;

/**
 * @author Rene
 * 
 */
public class WorksheetOutputEvent extends DefaultWorksheetEvent {
	private String	OutputBlockType;
	private Object	dataObject;

	/**
	 * @return the outputBlockType
	 */
	public String getOutputBlockType() {
		return this.OutputBlockType;
	}

	/**
	 * @param outputBlockType
	 *            the outputBlockType to set
	 */
	public void setOutputBlockType(final String outputBlockType) {
		this.OutputBlockType = outputBlockType;
	}

	/**
	 * @return the dataObject
	 */
	public Object getDataObject() {
		return this.dataObject;
	}

	/**
	 * @param dataObject
	 *            the dataObject to set
	 */
	public void setDataObject(final Object dataObject) {
		this.dataObject = dataObject;
	}

}
