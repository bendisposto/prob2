package de.prob.web.views;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.prob.web.ISession;

public class ReflectorDebugServlet implements ISession {

	@Override
	public void doGet(String session, HttpServletRequest request,
			HttpServletResponse response) {
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer.write("Session " + session);
		writer.write("<br/>\n");
		Enumeration<String> v = request.getParameterNames();
		for (Enumeration<String> e = v; e.hasMoreElements();) {
			String name = e.nextElement();
			String value = request.getParameter(name);
			writer.write(name + "=" + value + "<br />\n");
		}
		writer.flush();
		writer.close();

	}

	@Override
	public void restoreView(String session, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			PrintWriter writer = response.getWriter();
			writer.write("Restore View");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
