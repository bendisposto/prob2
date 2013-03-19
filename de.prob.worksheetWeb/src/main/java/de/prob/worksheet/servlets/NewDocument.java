package de.prob.worksheet.servlets;

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

import de.prob.worksheet.WorksheetObjectMapper;
import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.api.evalStore.EvalStoreContext;
import de.prob.worksheet.block.impl.InitializeStoreBlock;
import de.prob.worksheet.document.IWorksheetData;
import de.prob.worksheet.document.impl.WorksheetDocument;
import de.prob.worksheet.evaluator.DocumentEvaluator;

@WebServlet(urlPatterns = { "/newDocument" })
public class NewDocument extends HttpServlet {
	private static final long serialVersionUID = -8455020946701964097L;
	Logger logger = LoggerFactory.getLogger(NewDocument.class);

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
		IWorksheetData doc = getDocument(attributes);
		// store the session attributes
		setSessionAttributes(req.getSession(),
				req.getParameter("worksheetSessionId"), attributes);

		// print the json string to the response
		final WorksheetObjectMapper mapper = new WorksheetObjectMapper();
		resp.setStatus(HttpServletResponse.SC_ACCEPTED);
		resp.getWriter()
				.print(mapper.writerWithDefaultPrettyPrinter()
						.writeValueAsString(doc));
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getSessionAttributes(HttpSession session,
			String wsid) {
		HashMap<String, Object> attributes = (HashMap<String, Object>) session
				.getAttribute(wsid);
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
			logger.debug("New 'Sub'session initialized with id :" + wsid);
			session.setAttribute(wsid, attributes);
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

	private IWorksheetData getDocument(HashMap<String, Object> attributes) {
		WorksheetDocument doc = (WorksheetDocument) attributes.get("document");
		if (doc == null) {
			// TODO add distinction between eclipse plugin mode and external
			// Browser mode (e.g. remove unnecessary menus in plugin mode)
			doc = new WorksheetDocument();
			doc.setId("ws-id-1");

			logger.debug("New WorksheetDocument initiaized");

			final InitializeStoreBlock block1 = new InitializeStoreBlock();
			doc.insertBlock(0, block1);
			if (block1.isImmediateEvaluation()) {
				DocumentEvaluator evaluator = new DocumentEvaluator();
				evaluator.evaluateDocument(doc, getContextHistory(attributes));
			} else {
				doc.markAllAfter(0);
			}
			attributes.put("document", doc);
		} else {
			doc.markAllAfter(0);
		}
		return doc;

	}

	private ContextHistory getContextHistory(HashMap<String, Object> attributes) {
		logger.trace("{}", attributes);
		Object temp = attributes.get("contextHistory");
		ContextHistory contextHistory = null;
		if (temp == null) {
			// TODO change if new Modules with other Contexts and different
			// EntryPoints will be added
			contextHistory = new ContextHistory(new EvalStoreContext("init",
					null, null));
			logger.info("new ContextHistory created");
			attributes.put("contextHistory", contextHistory);
		} else {
			contextHistory = (ContextHistory) temp;
		}
		logger.trace("{}", contextHistory);
		return contextHistory;
	}

	private void logParameters(HttpServletRequest req) {
		String[] params = { "worksheetSessionId" };
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
