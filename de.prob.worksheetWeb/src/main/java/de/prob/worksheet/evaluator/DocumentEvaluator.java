package de.prob.worksheet.evaluator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.block.impl.JavascriptBlock;
import de.prob.worksheet.document.impl.WorksheetDocument;

public class DocumentEvaluator {
	private static Logger logger = LoggerFactory
			.getLogger(DocumentEvaluator.class);

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

		DefaultBlock[] blocks = doc.getBlocksFrom(index);

		contextHistory.removeHistoryAfterInitial(blocks[0].getId());

		BlockEvaluator blockEvaluator = new BlockEvaluator();
		for (final DefaultBlock block : blocks) {
			blockEvaluator.evaluate(doc, block, contextHistory);
		}

		blocks = doc.getBlocksFrom(index);

		if (blocks != null && blocks.length > 0) {
			if (blocks[blocks.length - 1].isOutput()
					|| blocks[blocks.length - 1].isInputAndOutput()) {
				doc.appendBlock(new JavascriptBlock());
			} else {
				if (blocks[blocks.length - 1].getEditor() != null) {
					String content = blocks[blocks.length - 1].getEditor()
							.getEditorContent();
					if (content != null && !content.trim().equals("")) {
						doc.appendBlock(new JavascriptBlock());
					}
				}
			}
		} else {
			DocumentEvaluator.logger
					.error("Tried to get Blocks from index {} but result was null or emtpy",
							index);
		}

	}
}
