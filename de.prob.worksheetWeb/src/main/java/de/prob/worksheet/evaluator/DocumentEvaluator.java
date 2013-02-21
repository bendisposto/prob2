package de.prob.worksheet.evaluator;

import de.prob.worksheet.WorksheetDocument;
import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.block.IBlock;
import de.prob.worksheet.block.JavascriptBlock;

public class DocumentEvaluator {

	public void evaluateBlock(WorksheetDocument doc, String blockId,
			ContextHistory contextHistory) {
		BlockEvaluator blockEvaluator = new BlockEvaluator();
		blockEvaluator.evaluate(doc, doc.getBlockById(blockId), contextHistory);

	}

	public void evaluateDocument(WorksheetDocument doc, String blockId,
			ContextHistory contextHistory) {
		evaluateFrom(doc, doc.getFirst().getId(), contextHistory);
	}

	public void evaluateFrom(WorksheetDocument doc, String blockId,
			ContextHistory contextHistory) {
		final int index = doc.getBlockIndexById(blockId);
		this.evaluateFrom(doc, index, contextHistory);
	}

	public void evaluateFrom(WorksheetDocument doc, int index,
			ContextHistory contextHistory) {

		doc.markAllAfter(index);

		IBlock[] blocks = doc.getBlocksFrom(index);

		contextHistory.removeHistoryAfterInitial(blocks[0].getId());

		BlockEvaluator blockEvaluator = new BlockEvaluator();
		for (final IBlock block : blocks) {
			blockEvaluator.evaluate(doc, block, contextHistory);
		}

		blocks = doc.getBlocksFrom(index);
		if (blocks[blocks.length - 1].isOutput()
				|| !blocks[blocks.length - 1].getEditor().getEditorContent()
						.trim().equals("")) {
			doc.appendBlock(new JavascriptBlock());
		}
	}
}
