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
import de.prob.worksheet.api.WorksheetOutputEvent;
import de.prob.worksheet.block.IBlock;

/**
 * @author Rene
 * 
 */
public class OutputListener implements IWorksheetAPIListener {
	Logger logger = LoggerFactory.getLogger(OutputListener.class);
	private static final Injector INJECTOR = ServletContextListener.INJECTOR;

	ArrayList<IBlock> outputBlocks;

	/**
	 * 
	 */
	public OutputListener(ArrayList<IBlock> output) {
		logger.trace("{}", output);
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
	public void notify(final IWorksheetEvent event) {
		logger.trace("{}", event);
		final WorksheetOutputEvent typedEvent = (WorksheetOutputEvent) event;
		switch (event.getId()) {
		default:
			this.addOutput(
					typedEvent.getOutputBlockType(),
					typedEvent.getMessage() + "</br>"
							+ typedEvent.getDataObject());
			break;
		}
	}

	private void addOutput(final String outputBlockType, final String output) {
		logger.trace("{},{}", outputBlockType, output);
		final IBlock block = OutputListener.INJECTOR.getInstance(Key.get(
				IBlock.class, Names.named(outputBlockType)));
		block.setOutput(true);
		block.getEditor().setEditorContent(output);
		this.outputBlocks.add(block);
		logger.debug("{}", this.outputBlocks);
	}

	public IBlock[] getBlocks() {
		logger.trace("");
		return this.outputBlocks.toArray(new IBlock[this.outputBlocks.size()]);
	}
}
