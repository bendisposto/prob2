package de.prob.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.annotations.Sessions;
import de.prob.webconsole.ServletContextListener;

@SuppressWarnings("serial")
@Singleton
public class ReflectionServlet extends HttpServlet {

	public static final String URL_PATTERN = "/sessions/";

	Logger logger = LoggerFactory.getLogger(ReflectionServlet.class);

	private final Map<String, ISession> sessions;
	private static final Gson GSON = new Gson();

	@Inject
	public ReflectionServlet(@Sessions Map<String, ISession> sessions) {
		this.sessions = sessions;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String uri = req.getRequestURI();
		List<String> parts = new PartList(uri.split("/"));

		String className = parts.get(2);
		String session = parts.get(3);
		boolean uuid = isUUID(session);

		if (uuid && sessions.containsKey(session)) {
			// logger.trace("Delegating");
			delegateToSession(req, resp, sessions.get(session));
			// logger.trace("Delegated call completed");
		} else {
			Class<ISession> clazz = getClass(className);
			if (clazz == null) {
				resp.sendError(404);
				return;
			}

			// We have an implementation
			logger.trace("Instantiating");
			ISession obj = instantiate(clazz);
			logger.trace("Got the object");
			String id = obj.getUuid().toString();
			sessions.put(id, obj);
			String rest = prepareExtraParameters(req);
			resp.sendRedirect(URL_PATTERN + className + "/" + id + rest);
			return;
		}

	}

	private void delegateToSession(HttpServletRequest req,
			HttpServletResponse resp, ISession session) throws IOException {
		String mode = req.getParameter("mode");
		Map<String, String[]> parameterMap = req.getParameterMap();
		if ("update".equals(mode)) {
			int lastinfo = Integer.parseInt(req.getParameter("lastinfo"));
			String client = req.getParameter("client");
			session.updatesSince(client, lastinfo, req.startAsync());
		} else if ("command".equals(mode)) {
			session.command(parameterMap);
		} else {
			String id = UUID.randomUUID().toString();
			send(resp, session.html(id, parameterMap));
		}
	}

	private void send(HttpServletResponse resp, String html) throws IOException {
		PrintWriter writer = resp.getWriter();
		writer.write(html);
		writer.flush();
		writer.close();
	}

	private boolean isUUID(String arg) {
		if (arg == null)
			return false;
		try {
			UUID.fromString(arg);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}

	}

	private String prepareExtraParameters(final HttpServletRequest request) {
		StringBuffer sb = new StringBuffer("?");
		for (Enumeration<String> parameters = request.getParameterNames(); parameters
				.hasMoreElements();) {
			String name = parameters.nextElement();
			String value = request.getParameter(name);
			sb.append(name);
			sb.append("=");
			sb.append(value);
			sb.append("&");
		}
		String rest = sb.substring(0, sb.length() - 1);
		return rest;
	}

	private ISession instantiate(Class<ISession> clazz) throws IOException {
		ISession obj = null;
		obj = ServletContextListener.INJECTOR.getInstance(clazz);
		return obj;
	}

	@SuppressWarnings("unchecked")
	private Class<ISession> getClass(String servletName) {
		Class<ISession> clazz = null;
		try {
			clazz = (Class<ISession>) Class.forName(servletName);
		} catch (ClassNotFoundException e) {

		}
		return clazz;
	}

	private class PartList extends ArrayList<String> {

		private static final long serialVersionUID = -5668244262489304794L;

		public PartList(String[] split) {
			super(Arrays.asList(split));
		}

		@Override
		public String get(int index) {
			if (index >= this.size())
				return "";
			else
				return super.get(index);
		}

	}

}
