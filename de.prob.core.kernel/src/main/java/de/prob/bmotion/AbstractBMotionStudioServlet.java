package de.prob.bmotion;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.io.Files;
import com.google.inject.Singleton;

import de.prob.Main;
import de.prob.model.representation.AbstractModel;
import de.prob.scripting.Api;
import de.prob.statespace.AnimationSelector;
import de.prob.web.WebUtils;

@SuppressWarnings("serial")
@Singleton
public abstract class AbstractBMotionStudioServlet extends HttpServlet {

	private final int DEFAULT_BUFFER_SIZE = 10240; // 10KB

	private final Map<String, AbstractBMotionStudioSession> sessions = new HashMap<String, AbstractBMotionStudioSession>();

	protected final Api api;

	protected final AnimationSelector animations;

	public AbstractBMotionStudioServlet(final Api api,
			final AnimationSelector animations) {
		this.api = api;
		this.animations = animations;
	}

	protected void toOutput(HttpServletResponse resp, InputStream stream) {
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

	protected void toOutput(HttpServletResponse resp, Object obj) {
		PrintWriter writer = null;
		try {
			writer = resp.getWriter();
			writer.print(obj);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Gently close streams.
			close(writer);
		}
	}

	protected void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
				File file = new File(
						BMotionUtil.getFullTemplatePath(templatePath));
				if (!file.exists())
					errors.add("The template " + templatePath
							+ " does not exist.");
			}
		}

		return errors;

	}

	private Map<String, String> getParameterFromTemplate(String templatePath) {

		Map<String, String> params = new HashMap<String, String>();
		String templateHtml = BMotionUtil.readFile(templatePath);
		Document templateDocument = Jsoup.parse(templateHtml);
		Elements headTag = templateDocument.getElementsByTag("head");
		Element headElement = headTag.get(0);
		Elements elements = headElement.getElementsByAttributeValueStarting(
				"name", "bms.");
		// Add additional parameters from template to BMotionStudioSession
		for (Element e : elements) {
			String content = e.attr("content");
			String name = e.attr("name");
			params.put(name.replace("bms.", ""), content);
		}
		return params;

	}

	private AbstractBMotionStudioSession createNewSessionAndRedirect(
			HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		int port = req.getLocalPort();
		String host = req.getRemoteAddr();
		if (Main.local) {
			host = "localhost";
		}

		// Extract template path
		String templatePath = req.getParameter("template");
		String fullTemplatePath = BMotionUtil.getFullTemplatePath(templatePath);

		Map<String, String> params = getParameterFromTemplate(fullTemplatePath);
		StringBuilder parameterString = new StringBuilder();
		for (Map.Entry<String, String[]> e : req.getParameterMap().entrySet()) {
			params.put(e.getKey(), e.getValue()[0]);
			parameterString.append("&" + e.getKey());
			if (!e.getValue()[0].isEmpty())
				parameterString.append("=" + e.getValue()[0]);
		}
		String modelPath = params.get("model");

		// Get model reference
		AbstractModel model = BMotionUtil.loadModel(api, animations, modelPath,
				fullTemplatePath);
		AbstractBMotionStudioSession bmsSession = createSession(templatePath,
				model, host, port);
		bmsSession.setParameterMap(params);
		String id = bmsSession.getSessionUUID().toString();
		bmsSession.initSession();
		sessions.put(id, bmsSession);

		// Send redirect with new session id, template file and parameters
		String fpstring = "?"
				+ parameterString.substring(1, parameterString.length());
		String fileName = new File(templatePath).getName();
		String redirect = req.getServletPath() + "/" + id + "/" + fileName
				+ fpstring;
		resp.sendRedirect(redirect);

		return bmsSession;

	}

	protected AbstractBMotionStudioSession initSession(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		// Get session from URI
		List<String> parts = new PartList(uri.split("/"));
		String sessionId = parts.get(2);

		// Try to get session. If no session exists, create one
		AbstractBMotionStudioSession bmsSession = getSessions().get(sessionId);
		if (bmsSession == null) {
			List<String> errors = validateRequest(req, resp);
			if (!errors.isEmpty()) {
				ByteArrayInputStream errorSiteStream = new ByteArrayInputStream(
						getErrorHtml(errors).getBytes());
				toOutput(resp, errorSiteStream);
			} else {
				bmsSession = createNewSessionAndRedirect(req, resp);
			}
		}
		return bmsSession;
	}

	private String getErrorHtml(List<String> errors) {
		String standalone = Main.standalone ? "yes" : "";
		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("error", true);
		scope.put("standalone", standalone);
		scope.put("errors", errors);
		return WebUtils.render("ui/bmsview/index.html", scope);
	}

	protected Map<String, AbstractBMotionStudioSession> getSessions() {
		return sessions;
	}

	protected void delegateFileRequest(HttpServletRequest req,
			HttpServletResponse resp, AbstractBMotionStudioSession bmsSession) {

		String sessionId = bmsSession.getSessionUUID().toString();
		String templatePath = bmsSession.getTemplatePath();
		File templateFile = new File(templatePath);
		String templateFolderPath = templateFile.getParent();
		String fileRequest = req.getRequestURI().replace(
				"/" + req.getServletPath() + sessionId + "/", "");
		String fullRequestPath = templateFolderPath + "/" + fileRequest;

		InputStream stream = null;

		// Set correct mimeType
		String mimeType = getServletContext().getMimeType(fullRequestPath);
		resp.setContentType(mimeType);

		// Ugly ...
		if (fullRequestPath.endsWith(templateFile.getName())) {
			// Get default page ...
			String defaultPage = getDefaultPage(bmsSession);
			stream = new ByteArrayInputStream(defaultPage.getBytes());
		} else {
			// All other files related to template folder
			File file = new File(fullRequestPath);
			if (!file.exists() || file.isDirectory())
				return;
			try {
				stream = new FileInputStream(fullRequestPath);
			} catch (FileNotFoundException e1) {
				// TODO Handle file not found exception!!!
				// e1.printStackTrace();
			}
		}
		toOutput(resp, stream);
		return;

	}

	protected abstract String getDefaultPage(
			AbstractBMotionStudioSession bmsSession);

	protected abstract AbstractBMotionStudioSession createSession(
			String template, AbstractModel model, String host, int port);

	protected class PartList extends ArrayList<String> {

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
