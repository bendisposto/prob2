package de.prob.bmotion;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.Main;
import de.prob.web.WebUtils;
import de.prob.web.data.SessionResult;

@SuppressWarnings("serial")
@Singleton
public class BMotionStudioServlet extends AbstractBMotionStudioServlet {

	private final ExecutorService taskExecutor = Executors
			.newFixedThreadPool(3);
	private final CompletionService<SessionResult> taskCompletionService = new ExecutorCompletionService<SessionResult>(
			taskExecutor);

	@Inject
	public BMotionStudioServlet() {
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

	private void update(HttpServletRequest req,
			AbstractBMotionStudioSession bmsSession) {
		int lastinfo = Integer.parseInt(req.getParameter("lastinfo"));
		String client = req.getParameter("client");
		bmsSession.sendPendingUpdates(client, lastinfo, req.startAsync());
	}

	private void executeCommand(HttpServletRequest req,
			HttpServletResponse resp, AbstractBMotionStudioSession bmsSession)
			throws IOException {
		Map<String, String[]> parameterMap = req.getParameterMap();
		Callable<SessionResult> command = bmsSession.command(parameterMap);
		PrintWriter writer = resp.getWriter();
		writer.write("submitted");
		writer.flush();
		writer.close();
		submit(command);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		AbstractBMotionStudioSession bmsSession = initSession(req, resp);
		String modeParameter = req.getParameter("mode");
		if ("update".equals(modeParameter)) {
			update(req, bmsSession);
		} else if ("command".equals(modeParameter)) {
			executeCommand(req, resp, bmsSession);
		} else {
			// Else (should be) file request
			delegateFileRequest(req, resp, bmsSession);
		}
		return;
	}

	private String getBaseHtml(AbstractBMotionStudioSession bmsSession) {
		String templatePath = bmsSession.getTemplatePath();
		String fileName = new File(templatePath).getName();
		String standalone = Main.standalone ? "yes" : "";
		Object scope = WebUtils.wrap("clientid", bmsSession.getSessionUUID()
				.toString(), "port", bmsSession.getPort(), "host", bmsSession
				.getHost(), "template", templatePath, "templatefile", fileName,
				"standalone", standalone);
		return WebUtils.render("ui/bmsview/index.html", scope);
	}

	public void submit(Callable<SessionResult> command) {
		taskCompletionService.submit(command);
	}

	@Override
	protected String getUrlPrefix() {
		return "bms";
	}

	@Override
	protected Class<?> getSessionClass() {
		return BMotionStudioSession.class;
	}

	@Override
	protected String getDefaultPage(AbstractBMotionStudioSession bmsSession) {

		String templateHtml = WebUtils.render(bmsSession.getTemplatePath());
		String baseHtml = getBaseHtml(bmsSession);

		Document templateDocument = Jsoup.parse(templateHtml);
		templateDocument.outputSettings().prettyPrint(false);

		for (Element e : templateDocument.getElementsByTag("svg")) {
			// If svg element has no id, set unique ID
			if (e.attr("id").isEmpty())
				e.attr("id", UUID.randomUUID().toString());
		}

		Elements headTag = templateDocument.getElementsByTag("head");
		Element headElement = headTag.get(0);

		Elements elements = headElement.getElementsByAttributeValueStarting(
				"name", "bms.");

		// Add additional parameters from template to BMotionStudioSession
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

		BMotionStudioUtil.fixSvgImageTags(baseDocument);

		return baseDocument.html();

	}

}
