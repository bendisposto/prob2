/**
 * 
 */
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

import de.prob.worksheet.WorksheetObjectMapper;
import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.api.evalStore.EvalStoreContext;
import de.prob.worksheet.block.IBlockData;
import de.prob.worksheet.document.impl.WorksheetDocument;
import de.prob.worksheet.evaluator.DocumentEvaluator;

/**
 * @author Rene
 * 
 */
@WebServlet(urlPatterns = { "/evaluate" })
public class Evaluate extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6247147601671174584L;
	Logger logger = LoggerFactory.getLogger(Evaluate.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		this.doPost(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		logParameters(req);

		resp.setCharacterEncoding("UTF-8");
		// initialize session and document
		this.setSessionProperties(req.getSession());

		HashMap<String, Object> sessionAttributes = this.getSessionAttributes(
				req.getSession(), req.getParameter("worksheetSessionId"));
		WorksheetDocument doc = (WorksheetDocument) sessionAttributes
				.get("document");

		final int index = doc.getBlockIndexById(req.getParameter("id"));

		DocumentEvaluator docEvaluator = new DocumentEvaluator();

		docEvaluator.evaluateFrom(doc, index,
				getContextHistory(sessionAttributes));

		this.setSessionAttributes(req.getSession(),
				req.getParameter("worksheetSessionId"), sessionAttributes);

		final WorksheetObjectMapper mapper = new WorksheetObjectMapper();

		IBlockData[] blocks = doc.getBlocksFrom(index);
		resp.getWriter().print(
				mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
						blocks));
		return;

	}

	private ContextHistory getContextHistory(HashMap<String, Object> attributes) {
		logger.trace("{}", attributes);
		Object temp = attributes.get("contextHistory");
		ContextHistory contextHistory = null;
		if (temp == null) {
			contextHistory = new ContextHistory(new EvalStoreContext("init",
					null));
			logger.info("new ContextHistory created");
			attributes.put("contextHistory", contextHistory);
		} else {
			contextHistory = (ContextHistory) temp;
		}
		logger.trace("{}", contextHistory);
		return contextHistory;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getSessionAttributes(HttpSession session,
			String wsid) {
		logger.trace("{} {}", session, wsid);
		HashMap<String, Object> attributes = (HashMap<String, Object>) session
				.getAttribute(wsid);
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
			logger.debug("New 'Sub'session initialized with id  :" + wsid);
		}
		logger.trace("{}", attributes);
		return attributes;
	}

	private void setSessionAttributes(HttpSession session, String wsid,
			HashMap<String, Object> attributes) {
		logger.trace("{}" + session);
		logger.trace("{}" + wsid);
		logger.trace("{}" + attributes);
		session.setAttribute(wsid, attributes);
	}

	private void setSessionProperties(HttpSession session) {
		logger.trace("{}", session);
		if (session.isNew()) {
			logger.debug("New Session initialized");
			session.setMaxInactiveInterval(-1);
		}
	}

	private void logParameters(HttpServletRequest req) {
		String[] params = { "worksheetSessionId", "id" };
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
