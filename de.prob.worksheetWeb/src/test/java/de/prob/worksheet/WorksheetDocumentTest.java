package de.prob.worksheet;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.prob.worksheet.block.IBlockData;
import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.block.impl.HTMLBlock;
import de.prob.worksheet.block.impl.HTMLErrorBlock;
import de.prob.worksheet.block.impl.InitializeStoreBlock;
import de.prob.worksheet.block.impl.JavascriptBlock;
import de.prob.worksheet.document.impl.WorksheetDocument;

public class WorksheetDocumentTest {

	@Test
	public void testWorksheetDocument() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBlocks() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetBlocks() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetHasMenu() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetHasMenu() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetHasBody() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetHasBody() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMenu() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetMenu() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetId() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetId() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBlockCounter() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetBlockCounter() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertBlock() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBlockIndex() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBlocksFromInt() {
		WorksheetDocument doc = new WorksheetDocument();

		IBlockData[] res = doc.getBlocksFrom(0);
		assertTrue(res.length == 0);

		doc.appendBlock(new InitializeStoreBlock());
		res = doc.getBlocksFrom(0);
		assertTrue(res.length == 1);
		res = doc.getBlocksFrom(1);
		assertTrue(res.length == 0);

		doc.appendBlock(new JavascriptBlock());
		res = doc.getBlocksFrom(0);
		assertTrue(res.length == 2);
		res = doc.getBlocksFrom(1);
		assertTrue(res.length == 1);
		res = doc.getBlocksFrom(2);
		assertTrue(res.length == 0);
	}

	@Test
	public void testMarkAllAfter() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetBlockIntIBlock() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveOutputBlocks() {
		WorksheetDocument doc = new WorksheetDocument();
		DefaultBlock block = new JavascriptBlock();
		doc.appendBlock(block);
		DefaultBlock out1 = new HTMLBlock();
		DefaultBlock out2 = new HTMLErrorBlock();

		doc.insertOutputBlocks(block, new DefaultBlock[] { out1, out2 });
		assertTrue(out1.getId().equals("ws-block-id-2"));
		assertTrue(out2.getId().equals("ws-block-id-3"));
		doc.removeOutputBlocks(block);
		IBlockData[] res = doc.getBlocksFrom(0);
		assertTrue(res.length == 1);
	}

	@Test
	public void testGetBlockById() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBlockIndexById() {
		fail("Not yet implemented");
	}

	@Test
	public void testAppendBlock() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetBlockIBlock() {
		fail("Not yet implemented");
	}

	@Test
	public void testMoveBlockTo() {
		fail("Not yet implemented");
	}

	@Test
	public void testMoveBlocksTo() {
		fail("Not yet implemented");
	}

	@Test
	public void testSwitchBlockType() {
		WorksheetDocument doc = new WorksheetDocument();
		DefaultBlock block = new JavascriptBlock();
		block.addOutputId("ws-block-id-5");
		block.addOutputId("ws-block-id-6");
		block.addOutputId("ws-block-id-7");
		doc.appendBlock(block);
		DefaultBlock block2 = new HTMLBlock();
		doc.switchBlockType(block.getId(), block2);
		assertTrue(block2.getOutputBlockIds().length == 3);
		assertTrue(block2.getId().equals(block.getId()));
	}

	@Test
	public void testInsertOutputBlocks() {
		WorksheetDocument doc = new WorksheetDocument();
		DefaultBlock block = new JavascriptBlock();
		doc.appendBlock(block);
		doc.insertOutputBlocks(block, new DefaultBlock[] { new HTMLBlock(),
				new HTMLErrorBlock() });
		IBlockData[] res = doc.getBlocksFrom(0);
		assertTrue(res.length == 3);
		String[] ids = doc.getBlockById(res[0].getId()).getOutputBlockIds();
		assertTrue(ids.length == 2);
	}

	@Test
	public void testIsLastBlock() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFirst() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBlocksFromIBlock() {
		fail("Not yet implemented");
	}

}
