package de.prob.worksheet.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.worksheet.WorksheetObjectMapper;
import de.prob.worksheet.document.IWorksheetData;

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
		SubsessionServletHelper.logParameters(req);

		// initialize the session
		SubsessionServletHelper.setSessionProperties(req.getSession());

		// Create the Session
		final String subSessionId = req.getParameter("worksheetSessionId");
		SubsessionServletHelper
				.createNewSession(req.getSession(), subSessionId);

		// Load the session attibutes
		HashMap<String, Object> attributes = SubsessionServletHelper
				.getSessionAttributes(req.getSession(), subSessionId);

		// load or create the document
		IWorksheetData doc = SubsessionServletHelper.getNewDocument(attributes);

		// store the session attributes
		SubsessionServletHelper.setSessionAttributes(req.getSession(),
				req.getParameter("worksheetSessionId"), attributes);

		// print the json string to the response
		final WorksheetObjectMapper mapper = new WorksheetObjectMapper();
		resp.setCharacterEncoding("UTF-8");
		resp.setStatus(HttpServletResponse.SC_ACCEPTED);
		resp.getWriter()
				.print(mapper.writerWithDefaultPrettyPrinter()
						.writeValueAsString(doc));
	}
}
