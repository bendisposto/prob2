/**
 * 
 */
package de.prob.worksheet.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.prob.worksheet.WorksheetDocument;

/**
 * @author Rene
 * 
 */
@WebServlet(urlPatterns={"/getDocument"})
public class getDocument extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 531283514393446460L;
	Logger logger = LoggerFactory.getLogger(getDocument.class);

	private WorksheetDocument doc;



	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
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
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		logParameters(req);
		resp.setCharacterEncoding("UTF-8");
		// Get Session and needed Attributes
		final HttpSession session = req.getSession();
		final String wsid = req.getParameter("worksheetSessionId");
		if (session.isNew()) {
			System.err.println("No worksheet Document is initialized (first a call to newDocument Servlet is needed)");
			resp.getWriter().write("Error: No document is initialized");
			return;
		}
		this.doc = (WorksheetDocument) req.getSession().getAttribute("WorksheetDocument" + wsid);

		final ObjectMapper mapper = new ObjectMapper();
		resp.getWriter().print(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.doc));

		return;
	}

	private void logParameters(HttpServletRequest req) {
		String[] params = { "worksheetSessionId", "document" };
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
