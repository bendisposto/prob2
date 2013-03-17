package de.prob.worksheet.evaluator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.block.impl.EventBBlock;
import de.prob.worksheet.document.impl.WorksheetDocument;

public class DocumentEvaluator {
	private static Logger logger = LoggerFactory
			.getLogger(DocumentEvaluator.class);

	public void evaluateBlock(WorksheetDocument doc, String blockId,
			ContextHistory contextHistory) {
		BlockEvaluator blockEvaluator = new BlockEvaluator();
		blockEvaluator.evaluate(doc, doc.getBlockById(blockId), contextHistory);

	}

	public void evaluateDocument(WorksheetDocument doc,
			ContextHistory contextHistory) {
		evaluateFrom(doc, doc.getFirst().getId(), contextHistory);
	}

	public void evaluateFrom(WorksheetDocument doc, String blockId,
			ContextHistory contextHistory) {
		DocumentEvaluator.logger.trace(
				"in: doc={}, blockId={}, contextHistory={}", new Object[] {
						doc, blockId, contextHistory });
		final int index = doc.getBlockIndexById(blockId);
		this.evaluateFrom(doc, index, contextHistory);
		DocumentEvaluator.logger.trace("out:");
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
				doc.appendBlock(new EventBBlock());
			} else {
				if (blocks[blocks.length - 1].getEditor() != null) {
					String content = blocks[blocks.length - 1].getEditor()
							.getEditorContent();
					if (content != null && !content.trim().equals("")) {
						doc.appendBlock(new EventBBlock());
					}
				}
			}

		} else {
			DocumentEvaluator.logger
					.error("Tried to get Blocks from index {} but result was null or emtpy",
							index);
		}
		if (index > 0) {
			blocks = doc.getBlocksFrom(index - 1);
			if (blocks.length >= 2) {
				DefaultBlock last = blocks[blocks.length - 1];
				DefaultBlock secondLast = blocks[blocks.length - 2];

				if (last.getClass().equals(secondLast.getClass())) {
					if (!last.isInputAndOutput()
							&& !last.isNeitherInNorOutput()
							&& !secondLast.isInputAndOutput()
							&& !secondLast.isNeitherInNorOutput()) {
						String lastContent = last.getEditor()
								.getEditorContent();
						String secondLastContent = secondLast.getEditor()
								.getEditorContent();
						if (lastContent != null
								&& lastContent.trim().equals("")
								&& secondLastContent != null
								&& secondLastContent.trim().equals(""))
							doc.removeBlock(last);
					}
				}
			}
		}
	}
}
