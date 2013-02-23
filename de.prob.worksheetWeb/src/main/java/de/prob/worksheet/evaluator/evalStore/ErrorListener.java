/**
 * 
 */
package de.prob.worksheet.evaluator.evalStore;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.api.IWorksheetAPIListener;
import de.prob.worksheet.api.IWorksheetEvent;
import de.prob.worksheet.api.WorksheetErrorEvent;
import de.prob.worksheet.block.impl.DefaultBlock;

/**
 * @author Rene
 * 
 */
public class ErrorListener implements IWorksheetAPIListener {
	Logger logger = LoggerFactory.getLogger(ErrorListener.class);
	private static final Injector INJECTOR = ServletContextListener.INJECTOR;

	public ArrayList<DefaultBlock> outputBlocks;
	private boolean haltAll;

	/**
	 * 
	 */
	public ErrorListener(ArrayList<DefaultBlock> output) {
		logger.trace(output.toString());
		this.setHaltAll(false);
		this.outputBlocks = output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.api.IWorksheetAPIListener#notify(de.prob.worksheet.
	 * api.IWorksheetEvent)
	 */
	@Override
	public void notify(IWorksheetEvent event) {
		logger.trace("{}", event);
		final WorksheetErrorEvent typedEvent = (WorksheetErrorEvent) event;
		if (typedEvent.isHaltAll())
			this.setHaltAll(true);
		switch (event.getId()) {
		default:
			this.addOutput("Fehler", typedEvent.getMessage(),
					typedEvent.isHaltAll());
			break;
		}
	}

	private void addOutput(final String outputBlockType, final String output,
			boolean haltAll) {
		logger.trace("type:{}", outputBlockType);
		logger.trace("output:{}", output);
		logger.trace("halt{}", haltAll);

		logger.debug("{}", ServletContextListener.INJECTOR.getAllBindings());
		final DefaultBlock block = ServletContextListener.INJECTOR.getInstance(Key
				.get(DefaultBlock.class, Names.named(outputBlockType)));
		block.setOutput(true);
		block.getEditor().setEditorContent(output);
		this.outputBlocks.add(block);
		logger.debug("{}", this.outputBlocks);
	}

	public boolean isHaltAll() {
		logger.trace("");
		return this.haltAll;
	}

	public void setHaltAll(boolean haltAll) {
		logger.trace("{}", haltAll);
		this.haltAll = haltAll;
	}
}
