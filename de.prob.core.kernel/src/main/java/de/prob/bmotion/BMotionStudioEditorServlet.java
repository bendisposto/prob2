package de.prob.bmotion;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
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

import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBVariable;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.ModelElementList;
import de.prob.scripting.Api;
import de.prob.statespace.AnimationSelector;
import de.prob.web.WebUtils;

@SuppressWarnings("serial")
@Singleton
public class BMotionStudioEditorServlet extends AbstractBMotionStudioServlet {

	private final Gson gson;

	@Inject
	public BMotionStudioEditorServlet(Api api, AnimationSelector animations,
			Gson gson) {
		super(api, animations);
		this.gson = gson;
	}

	private void taskInit(HttpServletRequest req, HttpServletResponse resp) {

		List<String> parts = new PartList(req.getRequestURI().split("/"));
		String sessionID = parts.get(2);

		String templatePath = BMotionUtil.getFullTemplatePath(req
				.getParameter("template"));

		String svgString = "";
		String svgId = UUID.randomUUID().toString();
		String jsonRendered = "{}";
		String modelPath = "";
		String scriptPath = "";

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

			BMotionUtil.fixSvgImageTags(templateDocument);

			Element svgElement = templateDocument.getElementsByTag("svg")
					.first();

			if (svgElement != null)
				svgString = svgElement.toString();
			if (svgElement != null)
				svgId = svgElement.attr("id");

			String jsonFileName = getMetaAttributeValue(templateDocument,
					"bms.json");
			if (jsonFileName != null) {
				String templateFolder = templateFile.getParent();
				String jsonFilePath = templateFolder + "/" + jsonFileName;
				jsonRendered = BMotionUtil.readFile(jsonFilePath);
			}

			// Get model and script path from template
			modelPath = getMetaAttributeValue(templateDocument, "bms.model") != null ? getMetaAttributeValue(
					templateDocument, "bms.model") : modelPath;
			scriptPath = getMetaAttributeValue(templateDocument, "bms.script") != null ? getMetaAttributeValue(
					templateDocument, "bms.script") : scriptPath;

		}

		// Send svg string and json data to client ...
		resp.setContentType("application/json");
		toOutput(resp,
				WebUtils.toJson(WebUtils.wrap("svg", svgString, "svgid", svgId,
						"sessionid", sessionID, "json", jsonRendered,
						"templatefile", templateFile.getName(), "templatepath",
						templatePath, "modelpath", modelPath, "scriptpath",
						scriptPath)));

	}

	private void taskConfigSave(HttpServletRequest req, HttpServletResponse resp) {
		String newModelPath = req.getParameter("newModelPath");
		String newScriptPath = req.getParameter("newScriptPath");
		String templatePath = req.getParameter("templatePath");
		File templateFile = new File(templatePath);
		Document templateDocument = Jsoup.parse(WebUtils.render(templatePath));
		setMetaAttributeValue(templateDocument, "bms.model", newModelPath);
		setMetaAttributeValue(templateDocument, "bms.script", newScriptPath);
		BMotionUtil.writeStringToFile(templateDocument.toString(),
				templateFile);
		toOutput(resp, "ok");
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
			String jsonFilePath = getMetaAttributeValue(templateDocument,
					"bms.json");
			if (jsonFilePath != null) {
				jsonFile = new File(templateFile.getParent() + "/"
						+ jsonFilePath);
				jsonSaveString = BMotionUtil.readFile(jsonFile
						.getAbsolutePath());
			} else {
				createJson = true;
			}
		}

		if (createJson) {
			String jsonFileName = Files.getNameWithoutExtension(templateFile
					.getName()) + ".json";
			setMetaAttributeValue(templateDocument, "bms.json", jsonFileName);
			// TODO: create correct json string!
			jsonSaveString = "{\"observers\":[{\"type\":\"EvalObserver\"}]}";
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

		BMotionUtil.fixSvgImageTags(templateDocument);
		String templateSaveString = templateDocument.toString();

		// Save data
		BMotionUtil.writeStringToFile(jsonSaveString, jsonFile);
		BMotionUtil.writeStringToFile(templateSaveString, templateFile);
		toOutput(resp, "ok");

	}

	private void deletageTaskRequest(String taskParameter,
			HttpServletRequest req, HttpServletResponse resp) {
		if ("init".equals(taskParameter)) {
			taskInit(req, resp);
		} else if ("save".equals(taskParameter)) {
			taskSave(req, resp);
		} else if ("configSave".equals(taskParameter)) {
			taskConfigSave(req, resp);
		}
	}
	
	@Override
	protected void delegateFileRequest(HttpServletRequest req,
			HttpServletResponse resp, AbstractBMotionStudioSession bmsSession) {

		String sessionId = bmsSession.getSessionUUID().toString();
		String templatePath = bmsSession.getTemplatePath();
		File templateFile = new File(templatePath);
		String templateFolderPath = templateFile.getParent();
		String fileRequest = req.getRequestURI().replace(
				"/bmseditor/" + sessionId + "/", "");
		String fullRequestPath = templateFolderPath + "/" + fileRequest;
		if (fullRequestPath.endsWith("tpl.html")) {
			// Template request
			String content = WebUtils.render("ui/bmsview/bms-editor/templates/"
					+ fileRequest);
			toOutput(resp, new ByteArrayInputStream(content.getBytes()));
		} else {
			super.delegateFileRequest(req, resp, bmsSession);
		}

	}

	private void delegateDataRequest(String dataParameter,
			HttpServletRequest req, HttpServletResponse resp,
			AbstractBMotionStudioSession bmsSession) {
		String json = "{}";
		if ("operations".equals(dataParameter)) {
			List<XEditableListObj> l = new ArrayList<XEditableListObj>();
			AbstractModel model = bmsSession.getModel();
			AbstractElement mainComponent = model.getMainComponent();
			if (mainComponent instanceof EventBMachine) {
				ModelElementList<Event> events = ((EventBMachine) mainComponent)
						.getEvents();
				for (Event event : events) {
					XEditableListObj xEditableListObj = new XEditableListObj(
							event.getName(), event.getName());
					l.add(xEditableListObj);
				}
			}
			json = gson.toJson(l);
		} else if ("variables".equals(dataParameter)) {
			List<XEditableListObj> l = new ArrayList<XEditableListObj>();
			AbstractModel model = bmsSession.getModel();
			AbstractElement mainComponent = model.getMainComponent();
			if (mainComponent instanceof EventBMachine) {
				ModelElementList<EventBVariable> variables = ((EventBMachine) mainComponent)
						.getVariables();
				for (EventBVariable var : variables) {
					XEditableListObj xEditableListObj = new XEditableListObj(
							var.getName(), var.getName());
					l.add(xEditableListObj);
				}
			}
			json = gson.toJson(l);
		}
		resp.setContentType("application/json");
		toOutput(resp, json);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		AbstractBMotionStudioSession bmsSession = initSession(req, resp);
		String dataParameter = req.getParameter("data");
		// Data request
		if (dataParameter != null) {
			delegateDataRequest(dataParameter, req, resp, bmsSession);
		} else {
			// Else (should be) file request
			delegateFileRequest(req, resp, bmsSession);
		}
		return;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String taskParameter = req.getParameter("task");
		if (taskParameter != null)
			deletageTaskRequest(taskParameter, req, resp);
		return;
	}

	private void setMetaAttributeValue(Document doc, String name, String value) {
		Elements metaElements = doc.getElementsByAttributeValue("name", name);
		Element metaElement = metaElements.first();
		if (metaElement == null) {
			Elements headTag = doc.getElementsByTag("head");
			Element headElement = headTag.get(0);
			metaElement = doc.createElement("meta");
			metaElement.attr("name", "bms.model");
			headElement.appendChild(metaElement);
		}
		metaElement.attr("content", value);
	}

	private String getMetaAttributeValue(Document doc, String name) {
		Elements metaElements = doc.getElementsByAttributeValue("name", name);
		Element metaElement = metaElements.first();
		if (metaElement != null)
			return metaElement.attr("content");
		return null;
	}

	@Override
	protected String getDefaultPage(AbstractBMotionStudioSession bmsSession) {
		return WebUtils.render("ui/bmsview/bms-editor/index.html",
				WebUtils.wrap("templatePath", bmsSession.getTemplatePath()));
	}

	@Override
	protected AbstractBMotionStudioSession createSession(String template,
			AbstractModel model, String host, int port) {
		return new BMotionStudioEditorSession(template, model, host, port);
	}
	
	private class XEditableListObj {

		@SuppressWarnings("unused")
		private String text;
		@SuppressWarnings("unused")
		private String value;

		public XEditableListObj(String text, String value) {
			this.text = text;
			this.value = value;
		}

	}

}
