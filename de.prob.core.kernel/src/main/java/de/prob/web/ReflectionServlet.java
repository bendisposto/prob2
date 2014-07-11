package de.prob.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.Main;
import de.prob.annotations.PublicSession;
import de.prob.annotations.Sessions;
import de.prob.web.data.SessionResult;

@SuppressWarnings("serial")
@Singleton
public class ReflectionServlet extends HttpServlet {

	public static final String URL_PATTERN = "/sessions/";

	Logger logger = LoggerFactory.getLogger(ReflectionServlet.class);

	private final Map<String, ISession> sessions;
	private final ExecutorService taskExecutor = Executors
			.newFixedThreadPool(3);
	private final CompletionService<SessionResult> taskCompletionService = new ExecutorCompletionService<SessionResult>(
			taskExecutor);

	private final static String FQN = "(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)+\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";

	@Inject
	public ReflectionServlet(@Sessions final Map<String, ISession> sessions) {
		this.sessions = sessions;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Future<SessionResult> message = taskCompletionService
								.take();
						if (message != null) { // will filter null values
							SessionResult res = message.get();
							if (res != null && res.result != null
									&& res.result.length > 0) {
								logger.trace("Got Result in Queue: {}",
										res.result);
								res.session.submit(res.result);
							}
						}
					} catch (Throwable e) {
						logger.error(
								"Exception in result handling loop. Ignoring",
								e);
					}
				}

			}
		}).start();
	}

	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {

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
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			// We have an implementation
			logger.trace("Instantiating");
			ISession obj = instantiate(clazz);
			logger.trace("Got the object");
			String id = obj.getSessionUUID().toString();
			sessions.put(id, obj);
			String rest = prepareExtraParameters(req);
			resp.sendRedirect(URL_PATTERN + className + "/" + id + rest);
			return;
		}

	}

	private void delegateToSession(final HttpServletRequest req,
			final HttpServletResponse resp, final ISession session)
			throws IOException {
		String mode = req.getParameter("mode");
		Map<String, String[]> parameterMap = req.getParameterMap();
		if ("update".equals(mode)) {
			int lastinfo = Integer.parseInt(req.getParameter("lastinfo"));

			String client = req.getParameter("client");
			session.sendPendingUpdates(client, lastinfo, req.startAsync());
		} else if ("command".equals(mode)) {
			Callable<SessionResult> command = session.command(parameterMap);
			send(resp, "submitted");
			submit(command);
		} else {
			String id = UUID.randomUUID().toString(); // client specific id
			send(resp, session.html(id, parameterMap));
		}
	}

	public void submit(final Callable<SessionResult> command) {
		taskCompletionService.submit(command);
	}

	private void send(final HttpServletResponse resp, final String html)
			throws IOException {
		PrintWriter writer = resp.getWriter();
		writer.write(html);
		writer.flush();
		writer.close();
	}

	private boolean isUUID(final String arg) {
		if (arg == null) {
			return false;
		}
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

	private ISession instantiate(final Class<ISession> clazz)
			throws IOException {
		boolean publicSession = false;
		Annotation[] annotations = clazz.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof PublicSession) {
				publicSession = true;
				break;
			}
		}

		ISession obj = null;
		if (!Main.restricted || publicSession)
			obj = Main.getInjector().getInstance(clazz);
		return obj;
	}

	@SuppressWarnings("unchecked")
	private Class<ISession> getClass(String servletName) {
		if (!servletName.matches(FQN)) {
			servletName = "de.prob.web.views." + servletName;
		}
		Class<ISession> clazz = null;
		try {
			clazz = (Class<ISession>) Class.forName(servletName);
		} catch (ClassNotFoundException e) {

		}
		return clazz;
	}

	private class PartList extends ArrayList<String> {

		private static final long serialVersionUID = -5668244262489304794L;

		public PartList(final String[] split) {
			super(Arrays.asList(split));
		}

		@Override
		public String get(final int index) {
			if (index >= this.size()) {
				return "";
			} else {
				return super.get(index);
			}
		}

	}

}
