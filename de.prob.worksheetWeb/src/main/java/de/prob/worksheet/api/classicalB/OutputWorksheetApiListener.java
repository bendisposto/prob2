package de.prob.worksheet.api.classicalB;

import java.util.ArrayList;

import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.block.impl.JavascriptBlock;

public class OutputWorksheetApiListener implements WorksheetAPIListener {

	ArrayList<DefaultBlock> outputBlocks;

	public OutputWorksheetApiListener(final ArrayList<DefaultBlock> retVal) {
		this.outputBlocks = retVal;
	}

	@Override
	public void notify(final WorksheetAPIListenerEvent event) {
		if (event.type == WorksheetAPIListenerEvent.TYPE_ACTION) {
			switch (event.name) {
			case WorksheetAPIListenerEvent.NAME_API:
				final JavascriptBlock block = new JavascriptBlock();
				block.setOutput(true);
				block.getEditor().setEditorContent(
						event.data[0] + "\n" + event.data[1].toString());
				this.outputBlocks.add(block);
				break;
			case WorksheetAPIListenerEvent.NAME_ANIMATION:
				System.out.println("Animation" + event.data[0]);
				break;
			case WorksheetAPIListenerEvent.NAME_EVALUATION:
				System.out.println("Evaluation" + event.data[0]);
				break;
			case WorksheetAPIListenerEvent.NAME_HISTORY:
				System.out.println("History" + event.data[0]);
				break;
			case WorksheetAPIListenerEvent.NAME_STATE:
				System.out.println("State" + event.data[0]);
				break;
			default:
				break;
			}
		} else if (event.type == WorksheetAPIListenerEvent.TYPE_ERROR) {
			switch (event.name) {
			case WorksheetAPIListenerEvent.NAME_API:
				System.err.println("Api: " + event.data[0]);
				break;
			case WorksheetAPIListenerEvent.NAME_ANIMATION:
				System.err.println("Animation" + event.data[0]);
				break;
			case WorksheetAPIListenerEvent.NAME_EVALUATION:
				System.err.println("Evaluation" + event.data[0]);
				break;
			case WorksheetAPIListenerEvent.NAME_HISTORY:
				System.err.println("History" + event.data[0]);
				break;
			case WorksheetAPIListenerEvent.NAME_STATE:
				System.err.println("State" + event.data[0]);
				break;
			default:
				break;
			}
		} else if (event.type == WorksheetAPIListenerEvent.TYPE_OUTPUT) {
			switch (event.name) {
			case WorksheetAPIListenerEvent.NAME_API:
				final JavascriptBlock block = new JavascriptBlock();
				block.setOutput(true);
				block.getEditor().setEditorContent(
						event.data[0] + "\n" + event.data[1].toString());
				this.outputBlocks.add(block);
				System.err.println("Api: " + event.data[0] + "\n"
						+ event.data[1]);
				break;
			case WorksheetAPIListenerEvent.NAME_ANIMATION:
				System.err.println("Animation" + event.data[0] + "\n"
						+ event.data[1]);
				break;
			case WorksheetAPIListenerEvent.NAME_EVALUATION:
				System.err.println("Evaluation" + event.data[0] + "\n"
						+ event.data[1]);
				break;
			case WorksheetAPIListenerEvent.NAME_HISTORY:
				System.err.println("History" + event.data[0] + "\n"
						+ event.data[1]);
				break;
			case WorksheetAPIListenerEvent.NAME_STATE:
				System.err.println("State" + event.data[0] + "\n"
						+ event.data[1]);
				break;
			default:
				break;
			}
		}
	}

}
