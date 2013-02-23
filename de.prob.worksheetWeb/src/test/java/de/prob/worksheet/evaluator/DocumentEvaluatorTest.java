package de.prob.worksheet.evaluator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.prob.worksheet.WorksheetDocument;
import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.api.evalStore.EvalStoreContext;
import de.prob.worksheet.block.IBlockData;
import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.block.impl.InitializeStoreBlock;
import de.prob.worksheet.block.impl.JavascriptBlock;

public class DocumentEvaluatorTest {

	@Test
	public void test() {
		WorksheetDocument doc = new WorksheetDocument();
		DefaultBlock inBlock = new JavascriptBlock();
		inBlock.getEditor().setEditorContent("x");
		doc.appendBlock(inBlock);

		DocumentEvaluator evaluator = new DocumentEvaluator();
		evaluator.evaluateFrom(doc, 0, new ContextHistory(new EvalStoreContext(
				"root", null)));

		IBlockData[] res = doc.getBlocks();
		assertTrue(res.length == 3);

		DefaultBlock newBlock = new InitializeStoreBlock();
		doc.switchBlockType("ws-block-id-1", newBlock);
		assertTrue(res.length == 3);
		evaluator.evaluateFrom(doc, 0, new ContextHistory(new EvalStoreContext(
				"root", null)));

		res = doc.getBlocks();
		assertTrue(res.length == 2);

	}

}
