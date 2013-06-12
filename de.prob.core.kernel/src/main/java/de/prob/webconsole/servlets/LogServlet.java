package de.prob.webconsole.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

import com.google.gson.Gson;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class LogServlet extends HttpServlet {

	List<LogElement> elements = new ArrayList<LogElement>();

	@Override
	public void doGet(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		String parameter = req.getParameter("since");
		Map<String, Object> response = new HashMap<String, Object>();

		if (parameter != null) {
			int pos = Integer.parseInt(parameter);
			if (pos < elements.size()) {
				response.put("elements", elements.subList(pos, elements.size()));
			} else {
				response.put("elements", new ArrayList<LogElement>());
			}
			response.put("numLogged", elements.size());
		}

		Gson g = new Gson();
		String json = g.toJson(response);
		out.println(json);
		out.close();
	}

	public enum LogType {
		error, warning, info, debug, trace
	}

	private class LogElement {
		private final String type;
		private final String msg;
		private final String from;
		private final String level;

		public LogElement(final String from, final Level level, final String msg) {
			this.from = from;
			type = level.toString().toLowerCase();
			this.msg = msg;
			this.level = level.toString();
		}
	}

	public synchronized void logEvent(final ILoggingEvent event) {
		String from = event.getLoggerName();
		if (event.hasCallerData()) {
			StackTraceElement[] callerData = event.getCallerData();
			if (callerData.length > 0) {
				StackTraceElement call = callerData[0];
				from = call.getClassName() + "." + call.getMethodName() + ":"
						+ call.getLineNumber();
			}
		}
		elements.add(new LogElement(from, event.getLevel(), event
				.getFormattedMessage()));
	}

}