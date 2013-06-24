package de.prob.webconsole;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

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

@Singleton
public class ReflectorFilter implements Filter {

	private final HashMap<String, ISession> sessioncontainer;

	@Inject
	public ReflectorFilter(HashMap<String, ISession> sessioncontainer) {
		this.sessioncontainer = sessioncontainer;
	}

	private int sessioncounter = 0;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String requestURI = request.getRequestURI();

		List<String> parts = new PartList(requestURI.split("/"));

		String servletName = parts.get(2);
		String session = parts.get(3);

		Class<ISession> clazz = null;
		try {
			clazz = (Class<ISession>) Class.forName(servletName);
		} catch (ClassNotFoundException e) {

		}
		if (clazz == null) {
			response.sendRedirect("nonexisting_class.html");
			return;
		}

		// We have an implementation

		if (session.isEmpty()) {
			// no session yet
			String id = fresh();
			try {
				ISession o = (ISession) clazz.newInstance();
				sessioncontainer.put(id, o);
			} catch (Exception e) {
				response.sendRedirect("nonexisting_class.html");
				return;
			}
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

			response.sendRedirect("/sb/" + servletName + "/" + id + rest);
			return;
		}

		ISession obj = sessioncontainer.get(session);
		obj.doGet(session, request, response);
	}

	private String fresh() {
		return Integer.toString(sessioncounter++);
	}

	@Override
	public void destroy() {

	}

}
