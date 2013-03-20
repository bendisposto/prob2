package de.prob.worksheet.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This
 * 
 * @author Rene
 * 
 */
public abstract class DefaultWorksheetAPI implements IWorksheetAPI {
	public static Logger logger = LoggerFactory
			.getLogger(DefaultWorksheetAPI.class);
	private List<IWorksheetAPIListener> actionListeners;
	private List<IWorksheetAPIListener> outputListeners;
	private List<IWorksheetAPIListener> errorListeners;

	public DefaultWorksheetAPI() {
		DefaultWorksheetAPI.logger.trace("in:");
		errorListeners = new ArrayList<IWorksheetAPIListener>();
		outputListeners = new ArrayList<IWorksheetAPIListener>();
		actionListeners = new ArrayList<IWorksheetAPIListener>();
		DefaultWorksheetAPI.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.api.IWorksheetAPI#notifyErrorListeners(int,
	 * java.lang.String, boolean)
	 */
	@Override
	public void notifyErrorListeners(final int id, final String message,
			final boolean haltAll) {
		DefaultWorksheetAPI.logger.trace("in: id={}, message={}, haltAll={}",
				new Object[] { id, message, haltAll });

		final WorksheetErrorEvent event = new WorksheetErrorEvent(id, message,
				haltAll);
		for (final IWorksheetAPIListener listener : errorListeners) {
			listener.notify(event);
		}
		DefaultWorksheetAPI.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.api.IWorksheetAPI#addErrorListener(de.prob.worksheet
	 * .api.IWorksheetAPIListener)
	 */
	@Override
	public void addErrorListener(final IWorksheetAPIListener listener) {
		DefaultWorksheetAPI.logger.trace("in: listener={}", listener);
		assert (errorListeners != null);
		assert (listener != null);

		if (!errorListeners.contains(listener)) {
			errorListeners.add(listener);
			DefaultWorksheetAPI.logger.debug("ErrorListeners={}",
					errorListeners);
		}
		DefaultWorksheetAPI.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.api.IWorksheetAPI#removeErrorListener(de.prob.worksheet
	 * .api.IWorksheetAPIListener)
	 */
	@Override
	public void removeErrorListener(final IWorksheetAPIListener listener) {
		DefaultWorksheetAPI.logger.trace("in: listener={}", listener);
		assert (errorListeners != null);
		errorListeners.remove(listener);
		DefaultWorksheetAPI.logger.debug("ErrorListeners={}", errorListeners);
		DefaultWorksheetAPI.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.api.IWorksheetAPI#notifyOutputListeners(int,
	 * java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void notifyOutputListeners(final int id, final String message,
			final String outputBlockType, final Object dataObject) {
		DefaultWorksheetAPI.logger.trace(
				"in: id={}, message={}, outputBlockType={}, data={}",
				new Object[] { id, message, outputBlockType, dataObject });

		final WorksheetOutputEvent event = new WorksheetOutputEvent();
		event.setId(id);
		event.setOutputBlockType(outputBlockType);
		event.setMessage(message);
		event.setDataObject(dataObject);
		for (final IWorksheetAPIListener listener : outputListeners) {
			listener.notify(event);
		}
		DefaultWorksheetAPI.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.api.IWorksheetAPI#addOutputListener(de.prob.worksheet
	 * .api.IWorksheetAPIListener)
	 */
	@Override
	public void addOutputListener(final IWorksheetAPIListener listener) {
		DefaultWorksheetAPI.logger.trace("in: listener={}", listener);
		assert (outputListeners != null);
		assert (outputListeners != null);
		if (!outputListeners.contains(listener)) {
			outputListeners.add(listener);
			DefaultWorksheetAPI.logger.debug("OutputListeners{}",
					outputListeners);
		}
		DefaultWorksheetAPI.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.api.IWorksheetAPI#removeOutputListener(de.prob.worksheet
	 * .api.IWorksheetAPIListener)
	 */
	@Override
	public void removeOutputListener(final IWorksheetAPIListener listener) {
		DefaultWorksheetAPI.logger.trace("in: listener={}", listener);
		outputListeners.remove(listener);
		DefaultWorksheetAPI.logger.debug("OutputListeners={}", outputListeners);
		DefaultWorksheetAPI.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.api.IWorksheetAPI#notifyActionListeners(int,
	 * java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void notifyActionListeners(final int id, final String message,
			final Object dataAfter) {
		DefaultWorksheetAPI.logger.trace("in: id{}, message{}, after{}",
				new Object[] { id, message, dataAfter });

		final WorksheetActionEvent event = new WorksheetActionEvent();
		event.setId(id);
		event.setMessage(message);
		event.setDataAfter(dataAfter);
		for (final IWorksheetAPIListener listener : actionListeners) {
			listener.notify(event);
		}
		DefaultWorksheetAPI.logger.trace("return");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.api.IWorksheetAPI#addActionListener(de.prob.worksheet
	 * .api.IWorksheetAPIListener)
	 */
	@Override
	public void addActionListener(final IWorksheetAPIListener listener) {
		DefaultWorksheetAPI.logger.trace("in: listener={}", listener);
		assert (actionListeners != null);
		assert (actionListeners != null);

		actionListeners.add(listener);
		DefaultWorksheetAPI.logger.debug("ActionListeners={}", actionListeners);
		DefaultWorksheetAPI.logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.api.IWorksheetAPI#removeActionListener(de.prob.worksheet
	 * .api.IWorksheetAPIListener)
	 */
	@Override
	public void removeActionListener(final IWorksheetAPIListener listener) {
		DefaultWorksheetAPI.logger.trace("in: listener={}", listener);
		actionListeners.remove(listener);
		DefaultWorksheetAPI.logger.debug("ActionListeners={}", actionListeners);
		DefaultWorksheetAPI.logger.trace("return:");
	}
}
