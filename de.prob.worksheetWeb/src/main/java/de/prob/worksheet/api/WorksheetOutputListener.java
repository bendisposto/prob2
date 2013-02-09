/**
 * 
 */
package de.prob.worksheet.api;

/**
 * @author Rene
 * 
 */
public class WorksheetOutputListener implements IWorksheetAPIListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.api.IWorksheetAPIListener#notify(de.prob.worksheet.
	 * api.WorksheetAPIListenerEvent)
	 */
	@Override
	public void notify(final IWorksheetEvent event) {
		assert (event instanceof WorksheetOutputEvent);

		final WorksheetOutputEvent outEvent = (WorksheetOutputEvent) event;

	}

}
