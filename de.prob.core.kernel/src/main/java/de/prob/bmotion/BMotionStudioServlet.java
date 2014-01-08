package de.prob.bmotion;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.annotations.Sessions;
import de.prob.web.ISession;
import de.prob.web.WebUtils;
import de.prob.web.data.SessionResult;
import de.prob.webconsole.ServletContextListener;

@SuppressWarnings("serial")
@Singleton
public class BMotionStudioServlet extends HttpServlet {

	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB

	private final Map<String, ISession> sessions;
	private final ExecutorService taskExecutor = Executors
			.newFixedThreadPool(3);
	private final CompletionService<SessionResult> taskCompletionService = new ExecutorCompletionService<SessionResult>(
			taskExecutor);

	@Inject
	public BMotionStudioServlet(@Sessions Map<String, ISession> sessions) {
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
								res.session.submit(res.result);
							}
						}
					} catch (Throwable e) {
					}
				}

			}
		}).start();
	}

	private void update(HttpServletRequest req, BMotionStudioSession bmsSession) {
		int lastinfo = Integer.parseInt(req.getParameter("lastinfo"));
		String client = req.getParameter("client");
		bmsSession.sendPendingUpdates(client, lastinfo, req.startAsync());
	}

	private void executeCommand(HttpServletRequest req,
			HttpServletResponse resp, BMotionStudioSession bmsSession)
			throws IOException {
		Map<String, String[]> parameterMap = req.getParameterMap();
		Callable<SessionResult> command = bmsSession.command(parameterMap);
		PrintWriter writer = resp.getWriter();
		writer.write("submitted");
		writer.flush();
		writer.close();
		submit(command);
	}

	private void delegateFileRequest(HttpServletRequest req,
			HttpServletResponse resp, BMotionStudioSession bmsSession) {

		String sessionId = bmsSession.getSessionUUID().toString();
		String templateFullPath = bmsSession.getTemplate();

		// If no template exists show BMotionStudio base HTML page
		if (templateFullPath == null) {

			String baseHtml = getBaseHtml(bmsSession);
			toOutput(resp, new ByteArrayInputStream(baseHtml.getBytes()));
			return;

		} else { // Else handle template/file requests ...

			String fileRequest = req.getRequestURI().replace(
					"/bms/" + sessionId + "/", "");
			List<String> parts = new PartList(templateFullPath.split("/"));
			String templateFile = parts.get(parts.size() - 1);
			String workspacePath = templateFullPath.replace(templateFile, "");
			if (fileRequest.isEmpty())
				fileRequest = templateFile;
			String fullRequestPath = workspacePath + fileRequest;

			InputStream stream = null;
			try {
				stream = new FileInputStream(fullRequestPath);
			} catch (FileNotFoundException e1) {
				// TODO Handle file not found exception!!!
				e1.printStackTrace();
				return;
			}

			// Set correct mimeType
			String mimeType = getServletContext().getMimeType(fullRequestPath);
			resp.setContentType(mimeType);

			// Ugly ...
			if (fullRequestPath.endsWith(".html")) {

				bmsSession.setTemplate(templateFullPath);

				String templateHtml = WebUtils.render(fullRequestPath);
				String baseHtml = getBaseHtml(bmsSession);

				Document templateDocument = Jsoup.parse(templateHtml);
				templateDocument.outputSettings().prettyPrint(false);

				Elements headTag = templateDocument.getElementsByTag("head");
				Element headElement = headTag.get(0);

				Elements elements = headElement
						.getElementsByAttributeValueStarting("name", "bms.");

				for (Element e : elements) {
					String content = e.attr("content");
					String name = e.attr("name");
					bmsSession.addParameter(name.replace("bms.", ""), content);
				}

				String head = headTag.html();
				Elements bodyTag = templateDocument.getElementsByTag("body");
				String body = bodyTag.html();
				Document baseDocument = Jsoup.parse(baseHtml);
				baseDocument.outputSettings().prettyPrint(false);

				Elements headTag2 = baseDocument.getElementsByTag("head");
				Element bodyTag2 = baseDocument.getElementById("vis_container");
				bodyTag2.append(body);

				headTag2.append(head);

				// Workaround, since jsoup renames svg image tags to img
				// tags ...
				Elements svgElements = baseDocument.getElementsByTag("svg");
				for (Element e : svgElements) {
					Elements imgTags = e.getElementsByTag("img");
					imgTags.tagName("image");
				}
				stream = new ByteArrayInputStream(baseDocument.html()
						.getBytes());

			}

			toOutput(resp, stream);

		}

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// Check if an existing session is request
		String uri = req.getRequestURI();
		List<String> parts = new PartList(uri.split("/"));
		String sessionID = parts.get(2);

		// Try to get BMotion Studio session
		BMotionStudioSession bmsSession = (BMotionStudioSession) sessions
				.get(sessionID);

		// If no session exists yet ...
		if (bmsSession == null) {

			// Get a new BMotionStudioSession
			bmsSession = ServletContextListener.INJECTOR
					.getInstance(BMotionStudioSession.class);
			String id = bmsSession.getSessionUUID().toString();
			// Register sessions
			sessions.put(id, bmsSession);

			String redirect;

			Map<String, String[]> parameterMap = req.getParameterMap();
			String template = req.getParameter("template");
			// New template requested via parameter
			if (template != null) {

				bmsSession.setTemplate(template);

				for (Map.Entry<String, String[]> e : parameterMap.entrySet()) {
					bmsSession.addParameter(e.getKey(), e.getValue()[0]);
				}

				String templateFullPath = bmsSession.getTemplate();
				List<String> templateParts = new PartList(
						templateFullPath.split("/"));
				String templateFile = templateParts
						.get(templateParts.size() - 1);
				// Send redirect with new session id and template file
				redirect = "/bms/" + id + "/" + templateFile;

			} else {
				// Send redirect only with new session id (we have still no
				// template)
				redirect = "/bms/" + id;
			}

			resp.sendRedirect(redirect);
			return;

		} else {
			String mode = req.getParameter("mode");
			if ("update".equals(mode)) {
				update(req, bmsSession);
			} else if ("command".equals(mode)) {
				executeCommand(req, resp, bmsSession);
			} else {
				delegateFileRequest(req, resp, bmsSession);
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

	private String getBaseHtml(BMotionStudioSession bmsSession) {
		Object scope = WebUtils.wrap("clientid", bmsSession.getSessionUUID()
				.toString());
		return WebUtils.render("ui/bmsview/index.html", scope);
	}

	private void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void submit(Callable<SessionResult> command) {
		taskCompletionService.submit(command);
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
