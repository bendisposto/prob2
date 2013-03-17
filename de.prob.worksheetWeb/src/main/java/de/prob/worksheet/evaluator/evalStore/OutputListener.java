/**
 * 
 */
package de.prob.worksheet.evaluator.evalStore;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.WorksheetObjectMapper;
import de.prob.worksheet.api.IWorksheetAPIListener;
import de.prob.worksheet.api.IWorksheetEvent;
import de.prob.worksheet.api.WorksheetOutputEvent;
import de.prob.worksheet.block.IBlockData;
import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.block.impl.HTMLBlock;

/**
 * @author Rene
 * 
 */
public class OutputListener implements IWorksheetAPIListener {
	Logger logger = LoggerFactory.getLogger(OutputListener.class);
	private static final Injector INJECTOR = ServletContextListener.INJECTOR;

	ArrayList<DefaultBlock> outputBlocks;

	public OutputListener(ArrayList<DefaultBlock> output) {
		logger.trace("{}", output);
		outputBlocks = output;
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
		case IEvalStoreConstants.CMD_TREE:
			WorksheetObjectMapper mapper = new WorksheetObjectMapper();
			try {
				String out = mapper.writeValueAsString(typedEvent
						.getDataObject());
				addOutput(typedEvent.getOutputBlockType(), out);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			break;
		case IEvalStoreConstants.CMD_OUT:
			HTMLBlock block = new HTMLBlock();
			block.setToUnicode(false);
			block.getEditor().setEscapeHtml(true);
			addOutput(block, typedEvent.getMessage());
			break;
		default:
			addOutput(typedEvent.getOutputBlockType(), typedEvent.getMessage());
			break;
		}
	}

	private void addOutput(final String outputBlockType, final String output) {
		logger.trace("{},{}", outputBlockType, output);
		final DefaultBlock block = OutputListener.INJECTOR.getInstance(Key.get(
				DefaultBlock.class, Names.named(outputBlockType)));
		block.setOutput(true);
		block.getEditor().setEditorContent(output);
		outputBlocks.add(block);
		logger.debug("{}", outputBlocks);
	}

	private void addOutput(final DefaultBlock block, final String output) {
		logger.trace("{},{}", block, output);
		block.setOutput(true);
		block.getEditor().setEditorContent(output);
		outputBlocks.add(block);
		logger.debug("{}", outputBlocks);
	}

	public IBlockData[] getBlocks() {
		logger.trace("");
		return outputBlocks.toArray(new DefaultBlock[outputBlocks.size()]);
	}
}
