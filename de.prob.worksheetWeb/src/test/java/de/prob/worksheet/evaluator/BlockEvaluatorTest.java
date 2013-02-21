package de.prob.worksheet.evaluator;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.prob.worksheet.WorksheetDocument;
import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.api.evalStore.EvalStoreContext;
import de.prob.worksheet.block.HTMLBlock;
import de.prob.worksheet.block.HTMLErrorBlock;
import de.prob.worksheet.block.IBlock;
import de.prob.worksheet.block.InitializeStoreBlock;
import de.prob.worksheet.block.JavascriptBlock;

public class BlockEvaluatorTest {
	WorksheetDocument doc;

	@Before
	public void init() {
		doc = new WorksheetDocument();
		doc.appendBlock(new InitializeStoreBlock());
		IBlock inBlock = new JavascriptBlock();
		doc.appendBlock(inBlock);
		doc.insertOutputBlocks(inBlock, new IBlock[] { new HTMLBlock(),
				new HTMLErrorBlock() });
		doc.appendBlock(new JavascriptBlock());
	}

	@Test
	public void testEvaluate() {
		doc = new WorksheetDocument();
		IBlock inBlock = new JavascriptBlock();
		inBlock.getEditor().setEditorContent("x");
		doc.appendBlock(inBlock);
		doc.insertOutputBlocks(inBlock, new IBlock[] { new HTMLBlock(),
				new HTMLErrorBlock() });
		BlockEvaluator evaluater = new BlockEvaluator();
		evaluater.evaluate(doc, inBlock, new ContextHistory(
				new EvalStoreContext("root", null)));
		IBlock[] res = doc.getBlocksFrom(0);
		assertTrue(res.length == 2);
	}
}
