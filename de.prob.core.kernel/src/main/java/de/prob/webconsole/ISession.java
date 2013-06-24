package de.prob.webconsole;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ISession {
	void doGet(String session, HttpServletRequest request,
			HttpServletResponse response);
}
