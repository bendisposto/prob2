package de.prob.worksheet.servlets;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.worksheet.ContextHistory;
import de.prob.worksheet.WorksheetDocument;
import de.prob.worksheet.api.evalStore.EvalStoreContext;
import de.prob.worksheet.block.InitializeStoreBlock;
import de.prob.worksheet.evaluator.DocumentEvaluator;

public class SubsessionServletHelper {
	Logger logger = LoggerFactory.getLogger(SubsessionServletHelper.class);

	public void setSessionAttributes(HttpSession session, String subSessionId,
			HashMap<String, Object> attributes) {
		logger.trace("in: session={}, subSessionId={} ,attributes={}",
				new Object[] { session, subSessionId, attributes });
		logger.debug("Session attributes: " + attributes.toString());
		session.setAttribute(subSessionId, attributes);
		logger.trace("return:");
	}

	public void setSessionProperties(HttpSession session) {
		logger.trace("in: session={}", session);
		if (session.isNew()) {
			logger.debug("New Session initialized");
			session.setMaxInactiveInterval(-1);
		}
		logger.trace("return:");
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getSessionAttributes(HttpSession session,
			String subSessionId) {
		logger.trace("in: session={}, subSessionId={}", session, subSessionId);
		HashMap<String, Object> attributes = (HashMap<String, Object>) session
				.getAttribute(subSessionId);
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
			logger.debug("New 'Sub'session initialized with id :"
					+ subSessionId);
		}
		logger.trace("return: attributes={}", attributes);
		return attributes;
	}

	public ContextHistory getContextHistory(HashMap<String, Object> attributes) {
		logger.trace("in: attributes={}", attributes);
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
		logger.trace("return: contextHistory={}", contextHistory);
		return contextHistory;
	}

	public WorksheetDocument getDocument(HashMap<String, Object> attributes,
			boolean create) {
		logger.trace("in: attributes={}, create={}", attributes, create);
		WorksheetDocument doc = (WorksheetDocument) attributes.get("document");
		if (doc == null && create) {

			doc = new WorksheetDocument();
			doc.setId("ws-id-1");

			final InitializeStoreBlock initBlock = new InitializeStoreBlock();
			doc.insertBlock(0, initBlock);

			DocumentEvaluator evaluator = new DocumentEvaluator();
			evaluator.evaluateFrom(doc, 0, new ContextHistory(
					new EvalStoreContext("init", null)));
			logger.debug("New WorksheetDocument initiaized");
		}
		logger.trace("return: document={}", doc);
		return doc;
	}

	public void logParameters(HttpServletRequest req, String[] params) {
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
