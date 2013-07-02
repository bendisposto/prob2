package de.prob.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
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

import de.prob.annotations.Sessions;
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

	private final Map<String, ISession> sessioncontainer;

	private final Executor tpe = Executors.newCachedThreadPool();

	@Inject
	public ReflectorFilter(@Sessions Map<String, ISession> sessioncontainer) {
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
		System.out.println("Requested: " + requestURI);
		List<String> parts = new PartList(requestURI.split("/"));

		String className = parts.get(2);
		String session = parts.get(3);
		boolean uuid = isUUID(session);

		if (uuid && sessioncontainer.containsKey(session)) {
			delegateToSession(request, response, session);
		} else {
			Class<ISession> clazz = getClass(className);
			if (clazz == null) {
				response.sendRedirect("nonexisting_class.html");
				return;
			}

			// We have an implementation
			UUID id = uuid ? UUID.fromString(session) : freshId();
			SessionQueue realizer = freshRealizer();
			ISession obj = instantiate(response, clazz);
			obj.setUuid(id);
			obj.setQueue(realizer);
			sessioncontainer.put(id.toString(), obj);
			String rest = prepareExtraParameters(request);
			response.sendRedirect("/sessions/" + className + "/"
					+ id.toString() + rest);
			return;
		}
	}

	private SessionQueue freshRealizer() {
		return new SessionQueue();
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

		String mode = request.getParameter("mode");
		mode = mode == null ? "html" : mode;

		if ("html".equals(mode)) {
			String html = obj.requestHtml(parameterMap);
			response.getWriter().write(html);
			return;
		}
		if ("listen".equals(mode)) {
			final AsyncContext context = request.startAsync();
			SessionQueue realizer = obj.getQueue();
			realizer.setContext(context);
			tpe.execute(realizer);
			return;
		}
		if ("command".equals(mode)) {
			tpe.execute(new SessionRunnable(parameterMap, obj));
			return;
		}
		throw new IllegalArgumentException("Unknown command mode: " + mode);
	}

	private UUID freshId() {
		return UUID.randomUUID();
	}

	@Override
	public void destroy() {

	}

}
