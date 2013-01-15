/**
 * 
 */
package de.prob.worksheet.evaluator.state;

import java.util.ArrayList;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.api.IWorksheetAPIListener;
import de.prob.worksheet.api.IWorksheetEvent;
import de.prob.worksheet.api.WorksheetErrorEvent;
import de.prob.worksheet.api.WorksheetOutputEvent;
import de.prob.worksheet.block.IBlock;

/**
 * @author Rene
 *
 */
public class ErrorListener implements IWorksheetAPIListener {
	private static final Injector	INJECTOR	= ServletContextListener.INJECTOR;

	public ArrayList<IBlock> outputBlocks;
	/**
	 * 
	 */
	public ErrorListener(ArrayList<IBlock> output) {
		this.outputBlocks=output;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.api.IWorksheetAPIListener#notify(de.prob.worksheet.api.IWorksheetEvent)
	 */
	@Override
	public void notify(IWorksheetEvent event) {
		final WorksheetErrorEvent typedEvent = (WorksheetErrorEvent) event;
		switch (event.getId()) {
			case 3001:
				this.addOutput("errorHtml", typedEvent.getMessage(),typedEvent.isHaltAll());
				break;
			default:
				this.addOutput("errorHtml", "Error "+typedEvent.getId()+": "+typedEvent.getMessage(),false);
				break;
		}
	}

	private void addOutput(final String outputBlockType, final String output,boolean haltAll) {
		final IBlock block = ErrorListener.INJECTOR.getInstance(Key.get(IBlock.class, Names.named(outputBlockType)));
		block.setOutput(true);
		block.getEditor().setEditorContent(output);
		this.outputBlocks.add(block);
	}
}
