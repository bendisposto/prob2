package de.prob.worksheet.evaluator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.block.impl.EventBBlock;
import de.prob.worksheet.document.impl.WorksheetDocument;

public class DocumentEvaluator {
	private static Logger logger = LoggerFactory
			.getLogger(DocumentEvaluator.class);

	public void evaluateBlock(WorksheetDocument doc, String blockId) {
		BlockEvaluator blockEvaluator = new BlockEvaluator();
		blockEvaluator.evaluate(doc, doc.getBlockById(blockId));

	}

	public void evaluateDocument(WorksheetDocument doc) {
		evaluateFrom(doc, doc.getFirst().getId());
	}

	public void evaluateFrom(WorksheetDocument doc, String blockId) {
		DocumentEvaluator.logger.trace("in: doc={}, blockId={}", new Object[] {
				doc, blockId });
		final int index = doc.getBlockIndexById(blockId);
		this.evaluateFrom(doc, index);
		DocumentEvaluator.logger.trace("out:");
	}

	public void evaluateFrom(WorksheetDocument doc, int index) {

		doc.markAllAfter(index);

		DefaultBlock[] blocks = doc.getBlocksFrom(index);

		doc.history.reset(blocks[0].getId());

		BlockEvaluator blockEvaluator = new BlockEvaluator();
		for (final DefaultBlock block : blocks) {
			blockEvaluator.evaluate(doc, block);
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
