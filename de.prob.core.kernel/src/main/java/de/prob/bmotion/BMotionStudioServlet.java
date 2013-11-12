package de.prob.bmotion;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.annotations.Sessions;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.web.ISession;
import de.prob.web.ReflectionServlet;
import de.prob.web.WebUtils;
import de.prob.web.data.SessionResult;
import de.prob.webconsole.ServletContextListener;

@SuppressWarnings("serial")
@Singleton
public class BMotionStudioServlet extends HttpServlet {

	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB
	
	public static final String URL_PATTERN = "/sessions/";

	Logger logger = LoggerFactory.getLogger(ReflectionServlet.class);

	private final Map<String, ISession> sessions;
	private final ExecutorService taskExecutor = Executors
			.newFixedThreadPool(3);
	private final CompletionService<SessionResult> taskCompletionService = new ExecutorCompletionService<SessionResult>(
			taskExecutor);

	private AnimationSelector selector;
	
	@Inject
	public BMotionStudioServlet(AnimationSelector selector, @Sessions Map<String, ISession> sessions) {
		this.selector = selector;
		this.sessions = sessions;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		Trace currentTrace = this.selector.getCurrentTrace();

		if(currentTrace == null)
			return;
		
		String modelFolderPath = currentTrace.getModel().getModelFile()
				.getParent();

		String uri = req.getRequestURI();
		
		String requestPath = uri.replace("/bms/", "");

		logger.trace("Request URI " + uri);		
		List<String> parts = new PartList(uri.split("/"));
		String session = parts.get(2);

		BMotionStudioSession bmsSession = (BMotionStudioSession) sessions
				.get(session);
		
		if (bmsSession == null) {

			BMotionStudioSession obj = ServletContextListener.INJECTOR
					.getInstance(BMotionStudioSession.class);
			String id = obj.getSessionUUID().toString();
			sessions.put(id, obj);
			resp.sendRedirect("/bms/" + id + "/" + requestPath);
			return;

		} else if (isUUID(session)) {

			requestPath = uri.replace("/bms/" + session + "/", "");

			String mode = req.getParameter("mode");
			Map<String, String[]> parameterMap = req.getParameterMap();
			if ("update".equals(mode)) {
				int lastinfo = Integer.parseInt(req.getParameter("lastinfo"));
				String client = req.getParameter("client");
				bmsSession.sendPendingUpdates(client, lastinfo,
						req.startAsync());
			} else if ("command".equals(mode)) {
				Callable<SessionResult> command = bmsSession
						.command(parameterMap);
				send(resp, "submitted");
				submit(command);
			} else {

				InputStream stream = null;

				// No request, show base HTML page
				if (requestPath.isEmpty()) {

					String baseHtml = getBaseHtml(modelFolderPath);
					stream = new ByteArrayInputStream(baseHtml.getBytes());
					toOutput(resp, stream);
					return;

				} else {

					String fullRequestPath = modelFolderPath + "/"
							+ requestPath;

					stream = new FileInputStream(fullRequestPath);

					// Set correct mimeType
					String mimeType = getServletContext().getMimeType(
							fullRequestPath);
					resp.setContentType(mimeType);

					// Ugly ...
					if (fullRequestPath.endsWith(".html")) {

						bmsSession.setTemplate(fullRequestPath);

						String templateHtml = WebUtils.render(fullRequestPath);
						String baseHtml = getBaseHtml(modelFolderPath);

						Document templateDocument = Jsoup.parse(templateHtml);
						Elements headTag = templateDocument
								.getElementsByTag("head");

						String head = headTag.html();
						Elements bodyTag = templateDocument
								.getElementsByTag("body");
						String body = bodyTag.html();
						Document baseDocument = Jsoup.parse(baseHtml);

						Elements headTag2 = baseDocument
								.getElementsByTag("head");
						Element bodyTag2 = baseDocument
								.getElementById("vis_container");
						bodyTag2.append(body);

						headTag2.append(head);

						stream = new ByteArrayInputStream(baseDocument
								.toString().getBytes());

					}

				}

				toOutput(resp, stream);

			}

		}

	}
	
	private void toOutput(HttpServletResponse resp, InputStream stream) {

		// Prepare streams.
		BufferedInputStream input = null;
		BufferedOutputStream output = null;

		try {
			// Open streams.
			input = new BufferedInputStream(stream, DEFAULT_BUFFER_SIZE);
			output = new BufferedOutputStream(resp.getOutputStream(),
					DEFAULT_BUFFER_SIZE);
			// Write file contents to response.
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Gently close streams.
			close(output);
			close(input);
		}

	}
	
	private void send(HttpServletResponse resp, String html) throws IOException {
		PrintWriter writer = resp.getWriter();
		writer.write(html);
		writer.flush();
		writer.close();
	}
	
	private String getBaseHtml(String workspacePath) {
		Object scope = WebUtils.wrap("clientid", UUID.randomUUID().toString(),
				"workspace", workspacePath);
		return WebUtils.render("/ui/bmsview/index.html", scope);
	}

	private void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				// Do your thing with the exception. Print it, log it or mail
				// it.
				e.printStackTrace();
			}
		}
	}

	public void submit(Callable<SessionResult> command) {
		taskCompletionService.submit(command);
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
