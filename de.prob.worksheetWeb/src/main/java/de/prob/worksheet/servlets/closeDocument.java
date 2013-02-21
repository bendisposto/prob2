/**
 * 
 */
package de.prob.worksheet.servlets;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rene
 * 
 */
@WebServlet(urlPatterns = { "/closeDocument" })
public class CloseDocument extends HttpServlet {

	Logger logger = LoggerFactory.getLogger(CloseDocument.class);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setCharacterEncoding("utf-8");
		this.logParameters(req);

		// initialize the session
		this.setSessionProperties(req.getSession());

		// Load the session attibutes
		// HashMap<String, Object> attributes =
		// this.getSessionAttributes(req.getSession(),
		// req.getParameter("worksheetSessionId"));

		this.removeSessionAttributes(req.getSession(),
				req.getParameter("worksheetSessionId"));

		return;
	}

	private void removeSessionAttributes(HttpSession session, String wsid) {
		HashMap<String, Object> attributes = (HashMap<String, Object>) session
				.getAttribute(wsid);
		if (attributes == null)
			logger.warn("No Subsession with this id exists!");
		session.removeAttribute(wsid);
		logger.debug("Subsession with id " + wsid + " cleared: "
				+ attributes.toString());
		logger.debug("Subsession Count: " + countAttributes(session));
	}

	private int countAttributes(HttpSession session) {
		Enumeration<String> names = session.getAttributeNames();
		int x = 0;
		while (names.hasMoreElements()) {
			x++;
			names.nextElement();
		}
		return x;
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
