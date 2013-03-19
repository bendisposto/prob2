package de.prob.worksheet.servlets;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.worksheet.api.ContextHistory;
import de.prob.worksheet.api.evalStore.EvalStoreContext;
import de.prob.worksheet.block.impl.InitializeStoreBlock;
import de.prob.worksheet.document.IWorksheetData;
import de.prob.worksheet.document.impl.WorksheetDocument;
import de.prob.worksheet.evaluator.DocumentEvaluator;

public class SubsessionServletHelper {
	static Logger logger = LoggerFactory
			.getLogger(SubsessionServletHelper.class);

	public static void setSessionAttributes(HttpSession session,
			String subSessionId, HashMap<String, Object> attributes) {
		SubsessionServletHelper.logger.trace(
				"in: session={}, subSessionId={} ,attributes={}", new Object[] {
						session, subSessionId, attributes });
		SubsessionServletHelper.logger.debug("Session attributes: "
				+ attributes.toString());
		session.setAttribute(subSessionId, attributes);
		SubsessionServletHelper.logger.trace("return:");
	}

	public static void setSessionProperties(HttpSession session) {
		SubsessionServletHelper.logger.trace("in: session={}", session);
		if (session.isNew()) {
			SubsessionServletHelper.logger.debug("New Session initialized");
			session.setMaxInactiveInterval(-1);
		}
		SubsessionServletHelper.logger.trace("return:");
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, Object> getSessionAttributes(
			HttpSession session, String subSessionId) {
		SubsessionServletHelper.logger.trace("in: session={}, subSessionId={}",
				session, subSessionId);
		HashMap<String, Object> attributes = (HashMap<String, Object>) session
				.getAttribute(subSessionId);
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
			SubsessionServletHelper.logger
					.debug("New 'Sub'session initialized with id :"
							+ subSessionId);
		}
		SubsessionServletHelper.logger.trace("return: attributes={}",
				attributes);
		return attributes;
	}

	public static ContextHistory getContextHistory(
			HashMap<String, Object> attributes) {
		SubsessionServletHelper.logger.trace("in: attributes={}", attributes);
		Object temp = attributes.get("contextHistory");
		ContextHistory contextHistory = null;
		if (temp == null) {
			contextHistory = new ContextHistory(new EvalStoreContext("init",
					null, null));
			SubsessionServletHelper.logger.info("new ContextHistory created");
			attributes.put("contextHistory", contextHistory);
		} else {
			contextHistory = (ContextHistory) temp;
		}
		SubsessionServletHelper.logger.trace("return: contextHistory={}",
				contextHistory);
		return contextHistory;
	}

	public static IWorksheetData getDocument(
			HashMap<String, Object> attributes, boolean create) {
		SubsessionServletHelper.logger.trace("in: attributes={}, create={}",
				attributes, create);
		WorksheetDocument doc = (WorksheetDocument) attributes.get("document");
		if (doc == null && create) {

			doc = new WorksheetDocument();
			doc.setId("ws-id-1");

			final InitializeStoreBlock initBlock = new InitializeStoreBlock();
			doc.insertBlock(0, initBlock);

			DocumentEvaluator evaluator = new DocumentEvaluator();
			evaluator.evaluateFrom(doc, 0, new ContextHistory(
					new EvalStoreContext("init", null, null)));
			SubsessionServletHelper.logger
					.debug("New WorksheetDocument initiaized");
			attributes.put("document", doc);
		}
		SubsessionServletHelper.logger.trace("return: document={}", doc);
		return doc;
	}

	public static void logParameters(HttpServletRequest req, String[] params) {
		String msg = "{ ";
		for (int x = 0; x < params.length; x++) {
			if (x != 0)
				msg += " , ";
			msg += params[x] + " : " + req.getParameter(params[x]);
		}
		msg += " }";
		SubsessionServletHelper.logger.debug(msg);

	}
}
