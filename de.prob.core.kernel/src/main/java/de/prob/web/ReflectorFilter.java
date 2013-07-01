package de.prob.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.webconsole.ServletContextListener;

@Singleton
public class ReflectorFilter implements Filter {

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

	private final HashMap<String, ISession> sessioncontainer;

	private final Executor tpe = Executors.newCachedThreadPool();

	@Inject
	public ReflectorFilter(HashMap<String, ISession> sessioncontainer) {
		this.sessioncontainer = sessioncontainer;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		final HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String requestURI = request.getRequestURI();

		List<String> parts = new PartList(requestURI.split("/"));

		String arg = parts.get(2);

		if (isUUID(arg)) {
			delegateToSession(request, response, arg);
		} else {
			Class<ISession> clazz = getClass(arg);
			if (clazz == null) {
				response.sendRedirect("nonexisting_class.html");
				return;
			}

			// We have an implementation

			UUID id = freshId();
			ISession obj = instantiate(response, clazz);
			obj.setUuid(id);
			sessioncontainer.put(id.toString(), obj);
			String rest = prepareExtraParameters(request);
			response.sendRedirect("/sessions/" + id.toString() + rest);
			return;
		}
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

	private ISession instantiate(HttpServletResponse response,
			Class<ISession> clazz) throws IOException {
		ISession obj = null;
		try {
			obj = ServletContextListener.INJECTOR.getInstance(clazz);
		} catch (Exception e) {
			response.sendRedirect("nonexisting_class.html");
		}
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

	private void delegateToSession(final HttpServletRequest request,
			HttpServletResponse response, String session) throws IOException {
		final ISession obj = sessioncontainer.get(session);
		final Map<String, String[]> parameterMap = request.getParameterMap();

		if (request.getParameter("ajax") == null
				|| !request.getParameter("ajax").equals("true")) {
			String html = obj.requestHtml(parameterMap);
			response.getWriter().write(html);
		} else {
			final AsyncContext context = request.startAsync();
			tpe.execute(new SessionRunnable(context, parameterMap, obj));
		}
	}

	private UUID freshId() {
		return UUID.randomUUID();
	}

	@Override
	public void destroy() {

	}

}
