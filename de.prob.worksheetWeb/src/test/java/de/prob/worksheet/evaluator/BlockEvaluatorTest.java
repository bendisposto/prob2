package de.prob.worksheet.evaluator;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.api.evalStore.EvalStoreContext;
import de.prob.worksheet.block.IBlockData;
import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.block.impl.HTMLBlock;
import de.prob.worksheet.block.impl.HTMLErrorBlock;
import de.prob.worksheet.block.impl.InitializeStoreBlock;
import de.prob.worksheet.block.impl.JavascriptBlock;
import de.prob.worksheet.document.impl.WorksheetDocument;

public class BlockEvaluatorTest {
	WorksheetDocument doc;

	@Before
	public void init() {
		doc = new WorksheetDocument();
		doc.appendBlock(new InitializeStoreBlock());
		DefaultBlock inBlock = new JavascriptBlock();
		doc.appendBlock(inBlock);
		doc.insertOutputBlocks(inBlock, new DefaultBlock[] { new HTMLBlock(),
				new HTMLErrorBlock() });
		doc.appendBlock(new JavascriptBlock());
	}

	@Test
	public void testEvaluate() {
		doc = new WorksheetDocument();
		DefaultBlock inBlock = new JavascriptBlock();
		inBlock.getEditor().setEditorContent("x");
		doc.appendBlock(inBlock);
		doc.insertOutputBlocks(inBlock, new DefaultBlock[] { new HTMLBlock(),
				new HTMLErrorBlock() });
		BlockEvaluator evaluater = new BlockEvaluator();
		evaluater.evaluate(doc, inBlock, new ContextHistory(
				new EvalStoreContext("root", null)));
		IBlockData[] res = doc.getBlocksFrom(0);
		assertTrue(res.length == 2);
	}
}
