/**
 * 
 */
package de.prob.worksheet.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Rene
 *
 */
@Singleton
public class closeDocument extends HttpServlet {

	/**
	 * 
	 */
	@Inject
	public closeDocument() {}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final String wsid = req.getParameter("worksheetSessionId");
		final HttpSession session = req.getSession();

		resp.setStatus(HttpServletResponse.SC_ACCEPTED);

		session.removeAttribute(wsid);
		if (session.isNew()) 
			session.invalidate();
		
		return;
	}
}
