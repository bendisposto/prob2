package de.prob.bmotion;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.Main;
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
	
	private void initParameterFromTemplate(BMotionStudioSession bmsSession) {

		String templateHtml = WebUtils.render(bmsSession.getTemplatePath());
		Document templateDocument = Jsoup.parse(templateHtml);
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

	}
	
	private String buildBMotionStudioRunPage(BMotionStudioSession bmsSession) {

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

		fixSvgImageTags(baseDocument);

		return baseDocument.html();

	}
	
	private void fixSvgImageTags(Document template) {
		for (Element e : template.getElementsByTag("svg")) {
			// Workaround, since jsoup renames svg image tags to img
			// tags ...
			Elements imgTags = e.getElementsByTag("img");
			imgTags.tagName("image");
		}
	}
	
	private void delegateFileRequest(HttpServletRequest req,
			HttpServletResponse resp, BMotionStudioSession bmsSession) {
		
		String sessionId = bmsSession.getSessionUUID().toString();
		String templatePath = bmsSession.getTemplatePath();
		File templateFile = new File(templatePath);
		String templateFolderPath = templateFile.getParent();
		String fileRequest = req.getRequestURI().replace(
				"/bms/" + sessionId + "/", "");
		String fullRequestPath = templateFolderPath + "/" + fileRequest;

		if (new File(fullRequestPath).isDirectory())
			return;

		InputStream stream = null;
		try {
			stream = new FileInputStream(fullRequestPath);
		} catch (FileNotFoundException e1) {
			// TODO Handle file not found exception!!!
			// e1.printStackTrace();
		}

		// Set correct mimeType
		String mimeType = getServletContext().getMimeType(fullRequestPath);
		resp.setContentType(mimeType);

		// Ugly ...
		if (fullRequestPath.endsWith(templateFile.getName())) {
			if (req.getParameter("editor") != null) {
				resp.setCharacterEncoding("UTF-8");
				String render = WebUtils.render(
						"ui/bmsview/bms-editor/index.html",
						WebUtils.wrap("templatePath", templatePath));
				stream = new ByteArrayInputStream(render.getBytes());
			} else {
				String html = buildBMotionStudioRunPage((BMotionStudioSession) bmsSession);
				stream = new ByteArrayInputStream(html.getBytes());
			}
		} else if (fullRequestPath.endsWith("tpl.html")) {
			String content = WebUtils.render("ui/bmsview/bms-editor/templates/"
					+ fileRequest);
			stream = new ByteArrayInputStream(content.getBytes());
		}
		toOutput(resp, stream);

	}
	
	private void taskInit(HttpServletRequest req, HttpServletResponse resp,
			BMotionStudioSession bmsSession) {

		List<String> parts = new PartList(req.getRequestURI().split("/"));
		String sessionID = parts.get(2);

		String templatePath = bmsSession.getTemplatePath();
//		String formalism = bmsSession.getFormalism();
		
		String svgString = "";
		String svgId = UUID.randomUUID().toString();

		String jsonRendered = "{}";
//		if (formalism.equals("b") || formalism.equals("eventb")) {
//			jsonRendered = "{\"observers\":[{\"type\":\"ExecuteOperation\"}, {\"type\":\"EvalObserver\"}]}";
//		} else if (formalism.equals("csp")) {
//			jsonRendered = "{\"observers\":[{\"type\":\"CspEventObserver\"}]}";
//		}

		File templateFile = new File(templatePath);
		if (templateFile.exists()) {

			// Get svg string from template
			String templateHtml = WebUtils.render(templatePath);
			Document templateDocument = Jsoup.parse(templateHtml);
			templateDocument.outputSettings().prettyPrint(false);
			for (Element e : templateDocument.getElementsByTag("svg")) {
				// If svg element has no id, set unique ID
				if (e.attr("id").isEmpty())
					e.attr("id", UUID.randomUUID().toString());
			}

			fixSvgImageTags(templateDocument);

			Element svgElement = templateDocument.getElementsByTag("svg")
					.first();

			if (svgElement != null)
				svgString = svgElement.toString();
			if (svgElement != null)
				svgId = svgElement.attr("id");

			// Get json data from template
			Elements headTag = templateDocument.getElementsByTag("head");
			Element headElement = headTag.get(0);
			Elements elements = headElement.getElementsByAttributeValue("name",
					"bms.json");
			Element jsonDomElement = elements.first();
			if (jsonDomElement != null) {
				String jsonFileName = jsonDomElement.attr("content");
				String templateFolder = templateFile.getParent();
				String jsonFilePath = templateFolder + "/" + jsonFileName;
				jsonRendered = readFile(jsonFilePath);
			}

		}

		// Send svg string and json data to client ...
		resp.setContentType("application/json");
		toOutput(resp, WebUtils.toJson(WebUtils.wrap("svg", svgString, "svgid",
				svgId, "sessionid", sessionID, "json", jsonRendered,
				"templatefile", templateFile.getName(), "templatepath",
				templatePath)));

	}
	
	private void addJsonMetaData(Document templateDocument, String jsonFileName) {

		Elements headTag = templateDocument.getElementsByTag("head");
		Element metaElement = templateDocument.createElement("meta");
		metaElement.attr("name", "bms.json");
		metaElement.attr("content", jsonFileName);
		headTag.append(metaElement.toString());

	}
	
	private void taskSave(HttpServletRequest req, HttpServletResponse resp) {

		// Get parameter
		String templatePath = req.getParameter("newtemplate");
		String newjson = req.getParameter("json");
		String newsvg = req.getParameter("svg");
		String svgElementId = req.getParameter("svgid") == null ? UUID
				.randomUUID().toString() : req.getParameter("svgid");

		File templateFile = new File(templatePath);
		File jsonFile = null;

		String jsonSaveString = null;
		Document templateDocument = null;
		boolean createJson = false;

		// If template file doesn't exist, create a new one
		if (!templateFile.exists()) {

			templateDocument = Jsoup.parse(WebUtils.render(
					"ui/bmsview/default_template.html",
					WebUtils.wrap("svgid", svgElementId)));
			createJson = true;

		} else {

			templateDocument = Jsoup.parse(WebUtils.render(templatePath));
			Elements elements = templateDocument.getElementsByAttributeValue(
					"name", "bms.json");
			Element jsonDomElement = elements.first();
			if (jsonDomElement != null) { // Json file is linked
				jsonFile = new File(templateFile.getParent() + "/"
						+ jsonDomElement.attr("content"));
				jsonSaveString = readFile(jsonFile.getAbsolutePath());
			} else { // No json file is linked
				createJson = true;
			}

		}
		
		if (createJson) {
			String jsonFileName = Files.getNameWithoutExtension(templateFile
					.getName()) + ".json";
			addJsonMetaData(templateDocument, jsonFileName);
			// TODO: create correct json string!
			jsonSaveString = "{\"observers\":[{\"type\":\"CspEventObserver\"}]}";
			jsonFile = new File(templateFile.getParent() + "/" + jsonFileName);
		}
		
		// If a new svg dom was passed, save in template
		if (newsvg != null) {
			templateDocument.outputSettings().prettyPrint(false);
			Document tmpParsed = Jsoup.parse(newsvg);
			Element newSvgElement = tmpParsed.getElementsByTag("svg").first();
			newSvgElement.attr("id", svgElementId);
			// Try first to get the svg element by id. If no exists, try to get
			// the first existing svg element in document
			Element orgSvgElement = templateDocument
					.getElementById(svgElementId);
			if (orgSvgElement == null)
				orgSvgElement = templateDocument.getElementsByTag("svg")
						.first();
			if (orgSvgElement != null)
				orgSvgElement.replaceWith(newSvgElement);
		}
		
		// If a new json was passed, save in json file
		if (newjson != null) {

			// Prepare json data
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setPrettyPrinting();
			gsonBuilder.disableHtmlEscaping();
			Gson gson = gsonBuilder.create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(newjson);
			jsonSaveString = gson.toJson(je);

		}

		fixSvgImageTags(templateDocument);
		String templateSaveString = templateDocument.toString();

		// Save data
		writeStringToFile(jsonSaveString, jsonFile);
		writeStringToFile(templateSaveString, templateFile);
		toOutput(resp, "ok");

	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String uri = req.getRequestURI();
		// Get session from URI
		List<String> parts = new PartList(uri.split("/"));
		String sessionID = parts.get(2);
		BMotionStudioSession bmsSession = (BMotionStudioSession) sessions
				.get(sessionID);
		String task = req.getParameter("task");

		if (task != null) {

			if (task.equals("init")) {
				taskInit(req, resp, bmsSession);
			} else if (task.equals("save")) {
				taskSave(req, resp);
			}

		}

	}

	private String readFile(final String filename) {
		String content = null;
		File file = new File(filename);
		try {
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	private void createNewSessionAndRedirect(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {

		String templatePath = req.getParameter("template");
		int port = req.getLocalPort();
		String host = req.getRemoteAddr();
		if (Main.local)
			host = "localhost";

		// Create a new BMotionStudioSession
		BMotionStudioSession bmsSession = ServletContextListener.INJECTOR
				.getInstance(BMotionStudioSession.class);
		String id = bmsSession.getSessionUUID().toString();
		// Register the new session
		sessions.put(id, bmsSession);

		// Prepare redirect ...
		Map<String, String[]> parameterMap = req.getParameterMap();

		// Set template path, port and host
		bmsSession.setTemplatePath(getFullTemplatePath(templatePath));
		bmsSession.setPort(port);
		bmsSession.setHost(host);
		// Build up parameter string and add parameters to
		// BMotionStudioSession
		StringBuilder parameterString = new StringBuilder();
		for (Map.Entry<String, String[]> e : parameterMap.entrySet()) {
			bmsSession.addParameter(e.getKey(), e.getValue()[0]);
			parameterString.append("&" + e.getKey());
			if (!e.getValue()[0].isEmpty())
				parameterString.append("=" + e.getValue()[0]);
		}
		initParameterFromTemplate(bmsSession);
		bmsSession.initSession();

		// Send redirect with new session id, template file and parameters
		String fpstring = "?"
				+ parameterString.substring(1, parameterString.length());
		String fileName = new File(templatePath).getName();
		String redirect = "/bms/" + id + "/" + fileName + fpstring;
		resp.sendRedirect(redirect);

	}
		
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String uri = req.getRequestURI();
		// Get session from URI
		List<String> parts = new PartList(uri.split("/"));
		String sessionID = parts.get(2);
		BMotionStudioSession bmsSession = (BMotionStudioSession) sessions
				.get(sessionID);

		if (bmsSession == null) {
			List<String> errors = validateRequest(req, resp);
			if (!errors.isEmpty()) {
				ByteArrayInputStream errorSiteStream = new ByteArrayInputStream(
						getErrorHtml(errors).getBytes());
				toOutput(resp, errorSiteStream);
				return;
			} else {
				createNewSessionAndRedirect(req, resp);
				return;
			}
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

		return;

	}
	
	private List<String> validateRequest(HttpServletRequest req,
			HttpServletResponse resp) {

		String templatePath = req.getParameter("template");
		String editor = req.getParameter("editor");

		List<String> errors = new ArrayList<String>();

		if (templatePath == null)
			errors.add("Please enter a template.");

		if (templatePath != null) {
			String fileExtension = Files.getFileExtension(templatePath);
			if (!(fileExtension.equals("html") || fileExtension.equals("htm"))) {
				errors.add("Plese enter a valid template (.html).");
			} else if (editor == null) {
				File file = new File(getFullTemplatePath(templatePath));
				if (!file.exists())
					errors.add("The template " + templatePath
							+ " does not exist.");
			}
		}

		return errors;

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

	private void toOutput(HttpServletResponse resp, String html) {
		PrintWriter writer = null;
		try {
			writer = resp.getWriter();
			writer.write(html);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Gently close streams.
			close(writer);
		}
	}

	private String getBaseHtml(BMotionStudioSession bmsSession) {
		String templatePath = bmsSession.getTemplatePath();
		String fileName = new File(templatePath).getName();
		String standalone = Main.standalone ? "yes" : "";
		Object scope = WebUtils.wrap("clientid", bmsSession.getSessionUUID()
				.toString(), "port", bmsSession.getPort(), "host", bmsSession
				.getHost(), "template", templatePath, "templatefile", fileName,
				"standalone", standalone);
		return WebUtils.render("ui/bmsview/index.html", scope);
	}
	
	private String getErrorHtml(List<String> errors) {
		String standalone = Main.standalone ? "yes" : "";
		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("error", true);
		scope.put("standalone", standalone);
		scope.put("errors", errors);
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
	
	private void writeStringToFile(String str, File file) {
		try {
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileOutputStream fop = new FileOutputStream(file);
			// get the content in bytes
			byte[] contentInBytes = str.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	private String getFullTemplatePath(String templatePath) {
		if (!new File(templatePath).isAbsolute()) {
			String homedir = System.getProperty("bms.home");
			if (homedir != null)
				return templatePath = homedir + templatePath;
			return templatePath = System.getProperty("user.home")
					+ templatePath;
		}
		return templatePath;
	}

}
