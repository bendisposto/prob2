package de.prob.worksheet.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.worksheet.document.IWorksheetData;

@WebServlet(urlPatterns = { "/saveDocument" })
public class SaveDocument extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7762787871923711945L;
	Logger logger = LoggerFactory.getLogger(SaveDocument.class);

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
		resp.setCharacterEncoding("utf-8");
		this.logParameters(req);

		// initialize the session
		this.setSessionProperties(req.getSession());

		// Load the session attibutes
		HashMap<String, Object> attributes = this.getSessionAttributes(
				req.getSession(), req.getParameter("worksheetSessionId"));

		// load or create the document
		IWorksheetData doc = this.getDocument(attributes);
		attributes = new HashMap<String, Object>();
		attributes.put("document", doc);

		// store the session attributes
		this.setSessionAttributes(req.getSession(),
				req.getParameter("worksheetSessionId"), attributes);

		// print the json string to the response
		/*
		 * final XmlMapper mapper = new XmlMapper();
		 * 
		 * AnnotationIntrospector introspector = new
		 * XmlJaxbAnnotationIntrospector(mapper.getTypeFactory());
		 * AnnotationIntrospector secondary = new
		 * JacksonAnnotationIntrospector(); mapper.setAnnotationIntrospector(new
		 * AnnotationIntrospectorPair(introspector, secondary));
		 * resp.getWriter().print(mapper.writeValueAsString(doc));
		 */
		resp.setStatus(HttpServletResponse.SC_ACCEPTED);
		JAXB.marshal(doc, resp.getWriter());
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

	private IWorksheetData getDocument(HashMap<String, Object> attributes) {
		IWorksheetData doc = (IWorksheetData) attributes.get("document");
		if (doc == null) {
			logger.error("No document is initialized");
		}
		return doc;

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
