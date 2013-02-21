/**
 * 
 */
package de.prob.worksheet.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extends the DefaultWorksheetEvent. It adds some Action specific
 * properties. The class is used to notify the evaluators of changes of the
 * context
 * 
 * @author Rene
 * 
 */
public class WorksheetActionEvent extends DefaultWorksheetEvent {
	public static final Logger logger = LoggerFactory
			.getLogger(WorksheetActionEvent.class);

	/**
	 * This Object stores the data state that had been present before the action
	 * had been performed. Most of the Time this should be some context bindings
	 * (see IContext)
	 */
	private Object dataBefore;
	/**
	 * This Object stores the data state that is present after the action had
	 * been performed. Most of the Time this should be some context bindings
	 * (see IContext)
	 */
	private Object dataAfter;

	/**
	 * Returns the data state before the action has been performed
	 * 
	 * @return the data state before the action was performed
	 */
	public Object getDataBefore() {
		logger.trace("in:");
		logger.trace("return: data={}", dataBefore);
		return this.dataBefore;
	}

	/**
	 * Sets the data state before the action has been performed
	 * 
	 * @param dataBefore
	 *            the data state before the action was performed
	 */
	public void setDataBefore(final Object data) {
		logger.trace("in: data={}", data);
		this.dataBefore = data;
		logger.trace("return:");
	}

	/**
	 * Returns the data state after the action had been performed
	 * 
	 * @return the data after action perform
	 */
	public Object getDataAfter() {
		logger.trace("in:");
		logger.trace("return: data={}", dataAfter);
		return this.dataAfter;
	}

	/**
	 * Sets the data state after the action had been performed
	 * 
	 * @param dataAfter
	 *            the data after the action perform to be set
	 */
	public void setDataAfter(final Object data) {
		logger.trace("in: data={}", data);
		this.dataAfter = data;
		logger.trace("return:");
	}
}
