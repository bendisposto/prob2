/**
 * 
 */
package de.prob.worksheet.evaluator.evalStore;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.api.IWorksheetAPIListener;
import de.prob.worksheet.api.IWorksheetEvent;
import de.prob.worksheet.api.WorksheetErrorEvent;
import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.block.impl.HTMLErrorBlock;
import de.prob.worksheet.block.impl.InitializeStoreBlock;
import de.prob.worksheet.block.impl.StoreValuesBlock;

/**
 * @author Rene
 * 
 */
public class ErrorListener implements IWorksheetAPIListener {
	public static final Logger logger = LoggerFactory
			.getLogger(ErrorListener.class);
	private static final Injector INJECTOR = ServletContextListener.INJECTOR;

	public ArrayList<DefaultBlock> outputBlocks;
	private boolean haltAll;

	public ErrorListener(ArrayList<DefaultBlock> output) {
		ErrorListener.logger.trace(output.toString());
		setHaltAll(false);
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
	public void notify(IWorksheetEvent event) {
		ErrorListener.logger.trace("{}", event);
		final WorksheetErrorEvent typedEvent = (WorksheetErrorEvent) event;
		if (typedEvent.isHaltAll())
			setHaltAll(true);

		DefaultBlock block = null;
		switch (event.getId()) {
		case IEvalStoreConstants.NOT_INITIALIZED:
			block = new StoreValuesBlock();
			block.setToUnicode(false);
			addOutput(block, typedEvent.getMessage(), typedEvent.isHaltAll());
			break;
		case IEvalStoreConstants.NO_ANIMATION:
			block = new InitializeStoreBlock();
			block.setToUnicode(false);
			addOutput(block, typedEvent.getMessage(), typedEvent.isHaltAll());
			break;
		default:
			block = new HTMLErrorBlock();
			block.setToUnicode(false);
			addOutput(block, typedEvent.getMessage(), typedEvent.isHaltAll());
			break;
		}
	}

	private void addOutput(DefaultBlock block, final String output,
			boolean haltAll) {
		ErrorListener.logger.trace("block:{}", block);
		ErrorListener.logger.trace("output:{}", output);
		ErrorListener.logger.trace("halt{}", haltAll);
		ErrorListener.logger.debug("block.toUnicode={}", block.isToUnicode());
		block.setOutput(true);
		block.getEditor().setEditorContent(output);
		outputBlocks.add(block);
		ErrorListener.logger.debug("{}", outputBlocks);
	}

	public boolean isHaltAll() {
		ErrorListener.logger.trace("");
		return haltAll;
	}

	public void setHaltAll(boolean haltAll) {
		ErrorListener.logger.trace("{}", haltAll);
		this.haltAll = haltAll;
	}
}
