package de.prob.webconsole.servlets.visualizations;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ISessionServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException;
}
