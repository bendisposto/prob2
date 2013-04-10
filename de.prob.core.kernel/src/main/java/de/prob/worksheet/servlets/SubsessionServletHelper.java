package de.prob.worksheet.servlets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		SubsessionServletHelper.logger.trace("return: attributes={}",
				attributes);
		return attributes;
	}

	public static boolean createNewSession(HttpSession session,
			String subSessionId) {
		HashMap<String, Object> attributes = (HashMap<String, Object>) session
				.getAttribute(subSessionId);
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
			session.setAttribute(subSessionId, attributes);
			SubsessionServletHelper.logger
					.debug("New 'Sub'session initialized with id :"
							+ subSessionId);

			return true;
		} else {
			logger.error("SubSession " + subSessionId + " already exists");
			return false;
		}
	}

	public static WorksheetDocument getDocument(
			HashMap<String, Object> attributes) {
		SubsessionServletHelper.logger.trace("in: attributes={}", attributes);
		WorksheetDocument doc = (WorksheetDocument) attributes.get("document");
		SubsessionServletHelper.logger.trace("return: document={}", doc);
		return doc;
	}

	public static void logParameters(HttpServletRequest req) {
		String msg = "Parameter: { ";
		Iterator<Entry<String, String[]>> it = req.getParameterMap().entrySet()
				.iterator();
		Entry<String, String[]> next = null;
		while (it.hasNext()) {
			if (next != null) {
				msg += ", ";
			}
			next = it.next();
			msg += next.getKey() + " : " + Arrays.toString(next.getValue());
		}
		msg += " }";
		logger.debug(msg);
	}

	public static IWorksheetData getNewDocument(
			HashMap<String, Object> attributes) {
		WorksheetDocument doc = (WorksheetDocument) attributes.get("document");
		if (doc == null) {
			doc = new WorksheetDocument();
			doc.setId("ws-id-1");

			logger.debug("New WorksheetDocument initiaized");

			final InitializeStoreBlock block1 = new InitializeStoreBlock();
			block1.setMark(true);
			doc.insertBlock(0, block1);
			if (block1.isImmediateEvaluation()) {
				DocumentEvaluator evaluator = new DocumentEvaluator();
				evaluator.evaluateDocument(doc);
			} else {
				doc.markAllAfter(0);
			}
			attributes.put("document", doc);
		} else {
			doc.markAllAfter(0);
		}
		return doc;
	}
}
