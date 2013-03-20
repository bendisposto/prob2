/**
 * 
 */
package de.prob.worksheet.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the Default implementation of the IWorksheetEvent
 * 
 * @author Rene
 * 
 */
public class DefaultWorksheetEvent implements IWorksheetEvent {
	/**
	 * The Logger for this class
	 */
	public static final Logger logger = LoggerFactory
			.getLogger(DefaultWorksheetEvent.class);
	/**
	 * The id of the Event
	 */
	private Integer id;
	/**
	 * The message of the Event
	 */
	private String message;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.api.IWorksheetEvent#getId()
	 */
	@Override
	public int getId() {
		logger.trace("in:");
		logger.trace("return: id={}", this.id);
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.api.IWorksheetEvent#setId(int)
	 */
	@Override
	public void setId(final int id) {
		logger.trace("in: id={}", id);
		this.id = id;
		logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.api.IWorksheetEvent#getMessage()
	 */
	@Override
	public String getMessage() {
		logger.trace("in:");
		logger.trace("return: message={}", this.message);
		return this.message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.api.IWorksheetEvent#setMessage(java.lang.String)
	 */
	@Override
	public void setMessage(final String message) {
		logger.trace("in: message={}", message);
		this.message = message;
		logger.trace("return:");
	}

}
