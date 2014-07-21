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
import de.prob.model.representation.AbstractModel;
import de.prob.scripting.Api;
import de.prob.scripting.ScriptEngineProvider;
import de.prob.statespace.AnimationSelector;
import de.prob.ui.api.ITool;
import de.prob.ui.api.ToolRegistry;
import de.prob.web.WebUtils;
import de.prob.web.data.SessionResult;

@SuppressWarnings("serial")
@Singleton
public class BMotionStudioServlet extends AbstractBMotionStudioServlet {

	private final ExecutorService taskExecutor = Executors
			.newFixedThreadPool(3);
	private final CompletionService<SessionResult> taskCompletionService = new ExecutorCompletionService<SessionResult>(
			taskExecutor);
	private final ScriptEngineProvider engineProvider;
	private final ToolRegistry toolRegistry;
	
	@Inject
	public BMotionStudioServlet(final Api api,
			final AnimationSelector animations,
			final ToolRegistry toolRegistry,
			final ScriptEngineProvider engineProvider) {
		super(api, animations);
		this.toolRegistry = toolRegistry;
		this.engineProvider = engineProvider;
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

	public void submit(final Callable<SessionResult> command) {
		taskCompletionService.submit(command);
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

	@Override
	protected String getDefaultPage(AbstractBMotionStudioSession bmsSession) {

		String templateHtml = WebUtils.render(bmsSession.getTemplatePath());
		String baseHtml = getBaseHtml(bmsSession);

		Document templateDocument = Jsoup.parse(templateHtml);
		templateDocument.outputSettings().prettyPrint(false);

		for (Element e : templateDocument.getElementsByTag("svg")) {
			// If svg element has no id, set unique ID
			if (e.attr("id").isEmpty()) {
				e.attr("id", UUID.randomUUID().toString());
			}
		}

		Elements headTag = templateDocument.getElementsByTag("head");
		String head = headTag.html();
		Elements bodyTag = templateDocument.getElementsByTag("body");
		String body = bodyTag.html();
		Document baseDocument = Jsoup.parse(baseHtml);
		baseDocument.outputSettings().prettyPrint(false);

		Elements headTag2 = baseDocument.getElementsByTag("head");
		Element bodyTag2 = baseDocument.getElementById("vis_container");

		bodyTag2.append(body);
		headTag2.append(head);

		BMotionUtil.fixSvgImageTags(baseDocument);

		return baseDocument.html();

	}

	@Override
	protected AbstractBMotionStudioSession createSession(String template,
			AbstractModel model, String host, int port) {
		// Create new ITool in respect to model
		ITool tool = BMotionUtil.loadTool(model, animations, toolRegistry);
		return new BMotionStudioSession(tool, toolRegistry, template, model,
				engineProvider, host, port);
	}

}
