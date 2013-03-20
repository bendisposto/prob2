package de.prob.worksheet.servlets.eval;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.name.Names;

import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.WorksheetObjectMapper;
import de.prob.worksheet.block.impl.DefaultBlock;
import de.prob.worksheet.document.impl.WorksheetDocument;
import de.prob.worksheet.evaluator.DocumentEvaluator;

@WebServlet(urlPatterns = { "/SwitchBlock" })
public class SwitchBlock extends HttpServlet {
	private static final long serialVersionUID = -1903390313586306774L;
	Logger logger = LoggerFactory.getLogger(SwitchBlock.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		logParameters(req);
		resp.setCharacterEncoding("UTF-8");
		// initialize the session
		setSessionProperties(req.getSession());

		// Load the session attibutes
		HashMap<String, Object> attributes = getSessionAttributes(
				req.getSession(), req.getParameter("worksheetSessionId"));

		// load or create the document
		WorksheetDocument doc = getDocument(attributes);
		logger.debug("in contextHistory: {}", doc.history);

		final WorksheetObjectMapper mapper = new WorksheetObjectMapper();

		// create new Block of whished type
		DefaultBlock newBlock = ServletContextListener.INJECTOR
				.getInstance(Key.get(DefaultBlock.class,
						Names.named(req.getParameter("type"))));
		// Switch Block in Document
		doc.switchBlockType(req.getParameter("blockId"), newBlock);

		// Maybe move this lineof code to the end of the method
		int startIndex = doc.getBlockIndex(newBlock);

		// if needed Evaluate the Document
		if (newBlock.isImmediateEvaluation() || !newBlock.isOutput()) {
			DocumentEvaluator documentEvalutor = new DocumentEvaluator();
			logger.debug("evaluating switched block");
			documentEvalutor.evaluateFrom(doc, newBlock.getId());
		}
		logger.debug("out contextHistory: {}", doc.history);
		// store the session attributes

		setSessionAttributes(req.getSession(),
				req.getParameter("worksheetSessionId"), attributes);

		// print the json string to the response
		resp.setStatus(HttpServletResponse.SC_ACCEPTED);
		resp.getWriter().print(
				mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
						doc.getBlocksFrom(startIndex)));
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getSessionAttributes(HttpSession session,
			String wsid) {
		HashMap<String, Object> attributes = (HashMap<String, Object>) session
				.getAttribute(wsid);
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
			logger.debug("New 'Sub'session initialized with id :" + wsid);
		}
		logger.debug("Session attributes: " + attributes.toString());
		return attributes;
	}

	private void setSessionAttributes(HttpSession session, String wsid,
			HashMap<String, Object> attributes) {
		logger.debug("Session attributes: " + attributes.toString());
		session.setAttribute(wsid, attributes);
	}

	private void setSessionProperties(HttpSession session) {
		if (session.isNew()) {
			logger.debug("New Session initialized");
			session.setMaxInactiveInterval(-1);
		}
	}

	private WorksheetDocument getDocument(HashMap<String, Object> attributes) {
		WorksheetDocument doc = (WorksheetDocument) attributes.get("document");
		return doc;
	}

	private void logParameters(HttpServletRequest req) {
		String[] params = { "worksheetSessionId", "blockId", "type" };
		String msg = "{ ";
		for (int x = 0; x < params.length; x++) {
			if (x != 0)
				msg += " , ";
			msg += params[x] + " : " + req.getParameter(params[x]);
		}
		msg += " }";
		logger.debug(msg);

	}
}
