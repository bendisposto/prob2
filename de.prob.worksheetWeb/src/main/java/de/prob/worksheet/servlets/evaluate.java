/**
 * 
 */
package de.prob.worksheet.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.WorksheetDocument;
import de.prob.worksheet.block.IBlock;
import de.prob.worksheet.block.JavascriptBlock;
import de.prob.worksheet.evaluator.IWorksheetEvaluator;

/**
 * @author Rene
 * 
 */
@Singleton
public class evaluate extends HttpServlet {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -6247147601671174584L;
	Logger logger = LoggerFactory.getLogger(evaluate.class);


	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		logParameters(req);

		resp.setCharacterEncoding("UTF-8");
		//initialize session and document
		final String wsid = req.getParameter("worksheetSessionId");
		HashMap<String, Object> sessionAttributes=(HashMap<String, Object>) req.getSession().getAttribute(wsid);
		if(sessionAttributes==null)
			sessionAttributes=new HashMap<String, Object>();
		WorksheetDocument doc=(WorksheetDocument) sessionAttributes.get("document");

		if (req.getSession().isNew() || doc==null) {
			System.err.println("No worksheet Document is initialized (first a call to newDocument Servlet is needed)");
			resp.getWriter().write("Error: No document is initialized");
			if(req.getSession().isNew())
				req.getSession().invalidate();
			return;
		}
		
		final String id = req.getParameter("id");
		final int index = doc.getBlockIndexById(id);
		doc.markAllAfter(index);

		IBlock[] blocks = doc.getBlocksFrom(index);

		for (final IBlock block : blocks) {
			this.evaluateBlock(doc,block, req.getSession(), wsid);
		}

		blocks = doc.getBlocksFrom(index);
		if (blocks[blocks.length - 1].getOutput() || !blocks[blocks.length - 1].getEditor().getEditorContent().trim().equals("")) {
			doc.appendBlock(new JavascriptBlock());
		}
		blocks = doc.getBlocksFrom(index);
	
		// DEBUG
		System.out.println("Document Block Count: " + doc.getBlocks().length);
		System.out.println("Document Blocks: " + Arrays.toString(doc.getBlocks()));
		// END DEBUG

		
		final ObjectMapper mapper = new ObjectMapper();
		
		resp.getWriter().print(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(blocks));

		return;

	}

	private void evaluateBlock(WorksheetDocument doc,final IBlock block, final HttpSession session, final String wsid) {
		if (block.getOutput()) return;

		final int index = doc.getBlockIndexById(block.getId());

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
		doc.removeOutputBlocks(block);

		// append outputBlocks after this block;
		int oBlockIndex = index;
		for (final IBlock outBlock : blocks) {
			oBlockIndex++;
			doc.insertBlock(oBlockIndex, outBlock);
			block.addOutputId(outBlock.getId());
		}
		return;
	}
	private void logParameters(HttpServletRequest req){
		String[] params={"worksheetSessionId","id"};
		String msg="{ ";
		for(int x=0;x<params.length;x++){
			if(x!=0)msg+=" , ";
			msg+=params[x]+" : "+req.getParameter(params[x]);
		}
		msg+=" }";
		logger.debug(msg);
	}
}
