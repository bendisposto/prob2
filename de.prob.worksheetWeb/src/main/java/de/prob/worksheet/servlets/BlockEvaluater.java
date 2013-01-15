package de.prob.worksheet.servlets;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.WorksheetDocument;
import de.prob.worksheet.block.IBlock;
import de.prob.worksheet.block.JavascriptBlock;
import de.prob.worksheet.evaluator.IWorksheetEvaluator;

@Singleton
public class BlockEvaluater extends HttpServlet {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4355352920265179769L;
	private WorksheetDocument	doc;

	@Inject
	public BlockEvaluater() {

	}

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

		/*
		 * what needs to be in a session? - The Worksheet Document
		 */
		final HttpSession session = req.getSession();
		final String wsid = req.getParameter("worksheetSessionID");
		if (session.isNew()) {
			// TODO maybe just tell the client that he doesn't have a session
			// yet instead of creating a new document here.
			this.doc = new WorksheetDocument();
			session.setAttribute("WorksheetDocument" + wsid, this.doc);
			this.test(this.doc);

		}
		this.doc = (WorksheetDocument) req.getSession().getAttribute("WorksheetDocument" + wsid);

		// TODO create singleton pattern or injection for ObjectMapper
		final ObjectMapper mapper = new ObjectMapper();
		final String blocksJSON = req.getParameter("blocks");
		if (blocksJSON == null) {
			resp.getWriter().close();
			return;
		}
		final IBlock[] submittedBlocks = mapper.readValue(blocksJSON, IBlock[].class);

		
		// get index of first updated Block
		final int topBlock = this.doc.getBlockIndex(submittedBlocks[0]);
		if (topBlock == -1) {
			// error block doesn't exist in doc ???
			System.out.println("asssssss");
		}

		// undo all blocks after this block including himself
		this.doc.undoFrom(topBlock);

		// evaluate all input blocks
		for (final IBlock block : submittedBlocks) {
			if (block.isOutput()) {
				continue;
			}
			this.evaluateBlock(block, session, wsid);
		}

		// DEBUG
		System.out.println("Document Block Count: " + this.doc.getBlocks().length);
		System.out.println("Document Blocks: " + Arrays.toString(this.doc.getBlocks()));
		// END DEBUG

		IBlock[] blocks = this.doc.getBlocksFrom(topBlock);
		if (blocks[blocks.length - 1].isOutput() || !blocks[blocks.length - 1].getEditor().getEditorContent().trim().equals("")) {
			this.doc.appendBlock(new JavascriptBlock());
		}
		blocks = this.doc.getBlocksFrom(topBlock);
		
		final ObjectWriter writer = mapper.writer();
		resp.getWriter().write(writer.writeValueAsString(blocks));
		resp.getWriter().close();
	}

	private void evaluateBlock(final IBlock block, final HttpSession session, final String wsid) {
		final int blockIndex = this.doc.getBlockIndex(block);
		// mark all blocks after this block;
		this.doc.markAllAfter(blockIndex);

		// update Block in Document
		this.doc.setBlock(blockIndex, block);

		// evaluate
		final String evalType = block.getEvaluatorType();
		IWorksheetEvaluator evaluator = (IWorksheetEvaluator) session.getAttribute(evalType + wsid);
		if (evaluator == null) {
			evaluator = ServletContextListener.INJECTOR.getInstance(Key.get(IWorksheetEvaluator.class, Names.named(evalType)));
			session.setAttribute(evalType + wsid, evaluator);
		}

		final String evalCode = block.getEditor().getEditorContent();
		final IBlock[] blocks = evaluator.evaluate(evalCode);

		// remove old Output for this block from document;
		this.doc.removeOutputBlocks(block);

		// append outputBlocks after this block;
		int oBlockIndex = blockIndex;
		for (final IBlock outBlock : blocks) {
			oBlockIndex++;
			this.doc.insertBlock(oBlockIndex, outBlock);
			block.addOutputId(outBlock.getId());
		}
		return;
	}

	private void test(final WorksheetDocument doc) {
		doc.setId("ui-id-1");
		final JavascriptBlock block1 = new JavascriptBlock();
		block1.setId("block-1");
		doc.insertBlock(0, block1);
	}
}
