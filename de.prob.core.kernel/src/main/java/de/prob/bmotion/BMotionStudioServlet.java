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
import java.net.URL;
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
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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

				bmsSession.setTemplatePath(templateFullPath);

				String templateHtml = WebUtils.render(fullRequestPath);
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
				
				for (Element e : baseDocument.getElementsByTag("svg")) {
					// Workaround, since jsoup renames svg image tags to img
					// tags ...
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
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String task = req.getParameter("task");
		
		if(task != null) {

			if (task.equals("init")) {

				String templatePath = req.getParameter("template");
				// Get svg string from template
				String templateHtml = WebUtils.render(templatePath);
				Document templateDocument = Jsoup.parse(templateHtml);
				templateDocument.outputSettings().prettyPrint(false);
				for (Element e : templateDocument.getElementsByTag("svg")) {
					// If svg element has no id, set unique ID
					if (e.attr("id").isEmpty())
						e.attr("id", UUID.randomUUID().toString());
				}
				Element svgElement = templateDocument.getElementsByTag("svg")
						.first();
				String svgString = svgElement.toString();
				String svgId = svgElement.attr("id");
				
				// Get json data from template
				Elements headTag = templateDocument.getElementsByTag("head");
				Element headElement = headTag.get(0);
				Elements elements = headElement.getElementsByAttributeValue("name",
						"bms.json");
				Element jsonDomElement = elements.first();
				String jsonFileName = jsonDomElement.attr("content");
				String templateFolder = new File(templatePath).getParent();
				String jsonFilePath = templateFolder + "/" + jsonFileName;
				String jsonRendered = readFile(jsonFilePath);
						
				// Send svg string and json data to client ...
				resp.setContentType("application/json");
				String json = WebUtils.toJson(WebUtils.wrap("svg", svgString,
						"svgid", svgId, "json", jsonRendered));
				toOutput(resp, json);

			} else if(task.equals("save")) {
				
				String templatePath = req.getParameter("template");
				String jsonString = req.getParameter("json");
				String svgString = req.getParameter("svg");
				String svgElementId = req.getParameter("svgid");

				// Prepare template
				String templateHtml = WebUtils.render(templatePath);
				Document templateDocument = Jsoup.parse(templateHtml);
				templateDocument.outputSettings().prettyPrint(false);
				Document tmpParsed = Jsoup.parse(svgString);
				Element newSvgElement = tmpParsed.getElementsByTag("svg")
						.first();
				newSvgElement.attr("id", svgElementId);
				Element orgSvgElement = templateDocument
						.getElementById(svgElementId);
				orgSvgElement.replaceWith(newSvgElement);
				File templateFile = new File(templatePath);				
				
				// Prepare json data
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.setPrettyPrinting();
				gsonBuilder.disableHtmlEscaping();
				Gson gson = gsonBuilder.create();
				JsonParser jp = new JsonParser();
				JsonElement je = jp.parse(jsonString);
				String prettyJsonString = gson.toJson(je);
				String jsonFilePath = null;
				Elements elements = templateDocument
						.getElementsByAttributeValue("name", "bms.json");
				Element jsonDomElement = elements.first();
				if (jsonDomElement != null) { // Json file is linked
					String jsonFileName = jsonDomElement.attr("content");
					String templateFolder = templateFile.getParent();
					jsonFilePath = templateFolder + "/" + jsonFileName;
				} else { // No json file is linked
					String templateFileName = templateFile.getName();
					String extension = templateFileName.substring(
							templateFileName.lastIndexOf('.'),
							templateFileName.length());
					String jsonFileName = templateFileName.replace("."
							+ extension, ".json");
					jsonFilePath = templatePath.replace(templateFileName,
							jsonFileName);
					Elements headTag = templateDocument
							.getElementsByTag("head");
					Element metaElement = templateDocument
							.createElement("meta");
					metaElement.attr("name", "bms.json");
					metaElement.attr("content", jsonFileName);
					headTag.append(metaElement.toString());
				}
				
				// Save data
				writeStringToFile(prettyJsonString, new File(jsonFilePath));
				writeStringToFile(templateDocument.html(), templateFile);
				toOutput(resp, "ok");
				
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
	
	private void delegateEditMode(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		String templatePath = req.getParameter("template");
		URL editorPath = getClass().getResource(
				"/ui/bmsview/bms-editor/index.html");
		resp.setCharacterEncoding("UTF-8");
		toOutput(resp,
				WebUtils.render(editorPath.getPath(),
						WebUtils.wrap("templatePath", templatePath)));
	}
	
	private void delegateRunMode(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {

		String uri = req.getRequestURI();

		// Get session id from URI
		List<String> parts = new PartList(uri.split("/"));
		String sessionID = parts.get(2);
		BMotionStudioSession bmsSession = (BMotionStudioSession) sessions
				.get(sessionID);

		// If no session exists yet ...
		if (bmsSession == null) {

			// Create a new BMotionStudioSession
			bmsSession = ServletContextListener.INJECTOR
					.getInstance(BMotionStudioSession.class);
			String id = bmsSession.getSessionUUID().toString();
			// Register the new session
			sessions.put(id, bmsSession);

			// Prepare redirect ...
			String redirect = "/bms/" + id;
			Map<String, String[]> parameterMap = req.getParameterMap();

			// Get path to template from corresponding parameter
			String template = req.getParameter("template");

			// If a template was specified ...
			if (template != null) {

				// Set template path in BMotionStudioSession
				bmsSession.setTemplatePath(template);

				// Build up parameter string
				StringBuilder parameterString = new StringBuilder();

				for (Map.Entry<String, String[]> e : parameterMap.entrySet()) {
					bmsSession.addParameter(e.getKey(), e.getValue()[0]);
					parameterString.append("&" + e.getKey() + "="
							+ e.getValue()[0]);
				}
				String fpstring = "?"
						+ parameterString
								.substring(1, parameterString.length());

				// Get only template file (no full path)
				List<String> templateParts = new PartList(template.split("/"));

				for (Map.Entry<String, String[]> e : req.getParameterMap()
						.entrySet()) {
					bmsSession.addParameter(e.getKey(), e.getValue()[0]);
				}

				// New template requested via parameter
				String templateFile = templateParts
						.get(templateParts.size() - 1);
				// Send redirect with new session id and template file
				redirect = "/bms/" + id + "/" + templateFile + fpstring;

			}

			resp.sendRedirect(redirect);

			return;

		} else { // If an session already exists ... delegate
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
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getParameter("edit") != null) {
			delegateEditMode(req, resp);
			return;
		} else {
			delegateRunMode(req, resp);
			return;
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
	
	private void writeStringToFile(String str, File file) {
		try {
			FileOutputStream fop = new FileOutputStream(file);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
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

}
