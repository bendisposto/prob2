package de.prob.worksheet.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWorksheetAPI {
	public static Logger logger = LoggerFactory
			.getLogger(DefaultWorksheetAPI.class);
	private List<IWorksheetAPIListener> actionListeners;
	private List<IWorksheetAPIListener> outputListeners;
	private List<IWorksheetAPIListener> errorListeners;

	public DefaultWorksheetAPI() {
		logger.trace("in:");
		this.errorListeners = new ArrayList<IWorksheetAPIListener>();
		this.outputListeners = new ArrayList<IWorksheetAPIListener>();
		this.actionListeners = new ArrayList<IWorksheetAPIListener>();
		logger.trace("return:");
	}

	public void notifyErrorListeners(final int id, final String message,
			final boolean haltAll) {
		logger.trace("in: id={}", id);
		logger.trace("haltAll={}", haltAll);
		logger.trace("message={}", message);

		final WorksheetErrorEvent event = new WorksheetErrorEvent(id, message,
				haltAll);
		for (final IWorksheetAPIListener listener : this.errorListeners) {
			listener.notify(event);
		}
		logger.trace("return:");
	}

	public void addErrorListener(final IWorksheetAPIListener listener) {
		logger.trace("in: listener={}", listener);
		assert (this.errorListeners != null);
		assert (listener != null);

		if (!this.errorListeners.contains(listener)) {
			this.errorListeners.add(listener);
			logger.debug("listeners:{}", this.errorListeners);
		}
		logger.trace("return:");
	}

	public void removeErrorListener(final IWorksheetAPIListener listener) {
		logger.trace("in: listener={}", listener);
		assert (this.errorListeners != null);
		this.errorListeners.remove(listener);
		logger.debug("{}", errorListeners);
		logger.trace("return:");
	}

	public void notifyOutputListeners(final int id, final String message,
			final String outputBlockType, final Object dataObject) {
		logger.trace("in: id={}", id);
		logger.trace("message={}", message);
		logger.trace("data={}", dataObject);

		final WorksheetOutputEvent event = new WorksheetOutputEvent();
		event.setId(id);
		event.setOutputBlockType(outputBlockType);
		event.setMessage(message);
		event.setDataObject(dataObject);
		for (final IWorksheetAPIListener listener : this.outputListeners) {
			listener.notify(event);
		}
		logger.trace("return:");
	}

	public void addOutputListener(final IWorksheetAPIListener listener) {
		logger.trace("in: listener={}", listener);
		assert (this.outputListeners != null);
		assert (this.outputListeners != null);
		if (!this.outputListeners.contains(listener)) {
			this.outputListeners.add(listener);
			logger.debug("{}", outputListeners);
		}
		logger.trace("return:");
	}

	public void removeOutputListener(final IWorksheetAPIListener listener) {
		logger.trace("in: listener={}", listener);
		this.outputListeners.remove(listener);
		logger.debug("{}", outputListeners);
		logger.trace("return:");
	}

	public void notifyActionListeners(final int id, final String message,
			final Object dataBefore, final Object dataAfter) {
		logger.trace("in: id{}", id);
		logger.trace("message{}", message);
		logger.trace("before{}", dataBefore);
		logger.trace("after{}", dataAfter);

		final WorksheetActionEvent event = new WorksheetActionEvent();
		event.setId(id);
		event.setMessage(message);
		event.setDataBefore(dataBefore);
		event.setDataAfter(dataAfter);
		for (final IWorksheetAPIListener listener : this.actionListeners) {
			listener.notify(event);
		}
		logger.trace("return");
	}

	public void addActionListener(final IWorksheetAPIListener listener) {
		logger.trace("in: listener={}", listener);
		assert (this.actionListeners != null);
		assert (this.actionListeners != null);

		this.actionListeners.add(listener);
		logger.debug("{}", actionListeners);
		logger.trace("return:");
	}

	public void removeActionListener(final IWorksheetAPIListener listener) {
		logger.trace("in: listener={}", listener);
		this.actionListeners.remove(listener);
		logger.debug("{}", actionListeners);
		logger.trace("return:");
	}
}
