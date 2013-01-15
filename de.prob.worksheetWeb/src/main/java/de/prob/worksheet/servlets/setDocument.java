/**
 * 
 */
package de.prob.worksheet.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import de.prob.worksheet.WorksheetDocument;

/**
 * @author Rene
 * 
 */
public class setDocument extends HttpServlet {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 838311906090202227L;

	/**
	 * 
	 */
	@Inject
	public setDocument() {
	};

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

		// Get Session and needed Attributes
		final HttpSession session = req.getSession();
		final String wsid = req.getParameter("worksheetSessionID");

		final ObjectMapper jsonMapper = new ObjectMapper();

		final String docString = req.getParameter("document");
		WorksheetDocument doc;
		doc = jsonMapper.readValue(docString, WorksheetDocument.class);

		session.setAttribute("WorksheetDocument" + wsid, doc);
		// TODO add result msg;

		return;

	}

}
