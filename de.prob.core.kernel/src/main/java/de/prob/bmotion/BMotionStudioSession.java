package de.prob.bmotion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.servlet.AsyncContext;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.ComputationNotCompletedResult;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.CSPModel;
import de.prob.scripting.ScriptEngineProvider;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class BMotionStudioSession extends AbstractSession implements
		IAnimationChangeListener, IModelChangedListener {

	Logger logger = LoggerFactory.getLogger(BMotionStudioSession.class);

	private Trace currentTrace;

	private AbstractModel currentModel;

	private final AnimationSelector selector;

	private String templatePath;
	
	private String jsonPath;
	
	private Document templateDocument;

	private final ScriptEngine groovyScriptEngine;

	private final Map<String, Object> parameterMap = new HashMap<String, Object>();

	private final Map<String, Object> formulas = new HashMap<String, Object>();

	private final Map<String, IEvalElement> formulasForEvaluating = new HashMap<String, IEvalElement>();

	private final Map<String, String> cachedCSPString = new HashMap<String, String>();

	private final Observer defaultObserver;
	
	private JsonElement json;

	private final List<IBMotionScript> scriptListeners = new ArrayList<IBMotionScript>();

	@Inject
	public BMotionStudioSession(final AnimationSelector selector,
			final ScriptEngineProvider sep) {
		this.selector = selector;
		incrementalUpdate = false;
		currentTrace = selector.getCurrentTrace();
		groovyScriptEngine = sep.get();
		defaultObserver = new Observer(this);
		selector.registerAnimationChangeListener(this);
		selector.registerModelChangedListener(this);
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return null;
	}

	public Object executeOperation(final Map<String, String[]> params) {
		String op = params.get("op")[0];
		String predicate = params.get("predicate")[0];
		if (predicate.isEmpty()) {
			predicate = "1=1";
		}
		Trace currentTrace = selector.getCurrentTrace();
		try {
			Trace newTrace = currentTrace.add(op, predicate);
			selector.replaceTrace(currentTrace, newTrace);
		} catch (BException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object openSvgEditor(final Map<String, String[]> params) {
		String svgId = params.get("id")[0];
		Element orgSvgElement = templateDocument.getElementById(svgId);
		if(json.isJsonObject()) {
			JsonObject asJsonObject = json.getAsJsonObject();
			JsonElement jsonElement = asJsonObject.get("observers");
			if (jsonElement.isJsonArray()) {
				submit(WebUtils.wrap("cmd", "bms.openSvgEditor", "svg",
						orgSvgElement.toString(), "json", jsonElement));
			}			
		} else {
			submit(WebUtils.wrap("cmd", "bms.openSvgEditor", "svg",
					orgSvgElement.toString()));
		}
		return null;
	}
	
	public Object removeObserver(final Map<String, String[]> params) {
		String type = params.get("type")[0];
		String group = params.get("group")[0];
		Integer index = Integer.valueOf(params.get("index")[0]);
		JsonObject observerJsonObject = getObserverGroupJsonObject(type, group);
		JsonArray observerItems = observerJsonObject.get("items")
				.getAsJsonArray();
		JsonArray tmpArray = new JsonArray();
		for (int i = 0; i < observerItems.size(); i++) {
			if (i != index)
				tmpArray.add(observerItems.get(i));
		}
		observerJsonObject.add("items", tmpArray);
		return null;
	}
	
	public Object addObserver(final Map<String, String[]> params) {
		String type = params.get("type")[0];
		String group = params.get("group")[0];
		JsonObject observerJsonObject = getObserverGroupJsonObject(type, group);
		JsonArray observerItems = observerJsonObject.get("items")
				.getAsJsonArray();
		JsonObject jsonObject = new JsonObject();
		observerItems.add(jsonObject);
		return null;
	}
	
	private JsonObject getObserverGroupJsonObject(String type, String group) {
		JsonArray asJsonArray = json.getAsJsonObject().get("observers")
				.getAsJsonArray();
		for (JsonElement el : asJsonArray) {
			JsonObject asJsonObject = el.getAsJsonObject();
			String observerType = asJsonObject.get("type").getAsString();
			if (observerType.equals(type)) {
				JsonArray observerObjs = asJsonObject.get("objs").getAsJsonArray();
				for (JsonElement obj : observerObjs) {
					JsonObject asJsonObject2 = obj.getAsJsonObject();
					String observerGroup = asJsonObject2.get("group")
							.getAsString();
					if (observerGroup.equals(group))
						return asJsonObject2;
				}
			}
		}
		return null;
	}
	
	public Object changeObserverData(final Map<String, String[]> params) {
		String type = params.get("type")[0];
		String group = params.get("group")[0];
		Integer index = Integer.valueOf(params.get("index")[0]);
		String key = params.get("key")[0];
		String value = params.get("value")[0];
		JsonObject observerJsonObject = getObserverGroupJsonObject(type, group);
		JsonArray observerItems = observerJsonObject.get("items")
				.getAsJsonArray();
		JsonObject item = observerItems.get(index).getAsJsonObject();
		item.addProperty(key, value);
		return null;
	}
	
	public Object saveSvg(final Map<String, String[]> params) {
		
		// Replace old SVG element with new one		
		String svgString = params.get("svg")[0];
		String svgId = params.get("id")[0];
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting();
		gsonBuilder.disableHtmlEscaping();
		Gson gson = gsonBuilder.create();
		
		String jsonToFile = gson.toJson(json);
		
		Element orgSvgElement = templateDocument.getElementById(svgId);
		Document parse = Jsoup.parse(svgString);
		Element newSvgElement = parse.getElementsByTag("svg").first();
		newSvgElement.attr("id",svgId);
		orgSvgElement.replaceWith(newSvgElement);
		
		// Save template and json file
		File templateFile = new File(templatePath);

		if (jsonPath == null) {
			String templateFileName = templateFile.getName();
			String extension = templateFileName.substring(
					templateFileName.lastIndexOf('.'),
					templateFileName.length());
			String jsonFileName = templateFileName.replace("." + extension,
					".json");
			jsonPath = templatePath.replace(templateFileName, jsonFileName);
			Elements headTag = templateDocument.getElementsByTag("head");
			Element metaElement = templateDocument.createElement("meta");
			metaElement.attr("name", "bms.json");
			metaElement.attr("content", jsonFileName);
			headTag.append(metaElement.toString());
		}

		writeStringToFile(templateDocument.html(), templateFile);
		
		try {
			File jsonFile = new File(jsonPath);
			if (!jsonFile.exists())
				jsonFile.createNewFile();
			writeStringToFile(jsonToFile, jsonFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		initJsonData();

		submit(WebUtils.wrap("cmd", "bms.saveSvg", "svgid", svgId, "svgstring",
				newSvgElement.toString()));

		return null;
		
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
	
	public Object setTemplate(final Map<String, String[]> params) {
		String fullTemplatePath = params.get("path")[0];
		submit(WebUtils.wrap("cmd", "bms.setTemplate", "request",
				fullTemplatePath));
		return null;
	}

	public Object triggerListener(final Map<String, String[]> params) {
		// Trigger all registered script listeners with collected formulas
		for (IBMotionScript s : scriptListeners) {
			s.traceChanged(currentTrace, formulas);
		}
		return null;
	}
	
	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {

		// After reload do not resent old messages
		int old = responses.size() + 1;
		// Add dummy message
		submit(WebUtils.wrap("cmd", "extern.skip"));
		
		// Initialize Session
		initSession();

		// If a trace already exists, trigger a trace change and modelchanged
		if (currentTrace != null) {
			traceChange(currentTrace, true);
			modelChanged(currentTrace.getStateSpace());
		}

		// Resent messages, send while initializing the session and trace change
		if (!responses.isEmpty()) {
			resend(client, old, context);
		}

		super.reload(client, lastinfo, context);

	}

	private void initSession() {
		// Remove all script listeners and add new observer scriptlistener
		scriptListeners.clear();
		scriptListeners.add(defaultObserver);
		// Initialize json data (if not already done)
		initJsonData();
		// Init Groovy scripts
		initGroovy();
	}

	private void registerFormulas(final AbstractModel model) {
		for (Map.Entry<String, IEvalElement> entry : formulasForEvaluating
				.entrySet()) {
			String formula = entry.getKey();
			IEvalElement evalElement = entry.getValue();
			if (evalElement == null) {
				subscribeFormula(formula, model);
			}
		}
	}

	private void deregisterFormulas(final AbstractModel model) {
		StateSpace s = model.getStatespace();
		for (Map.Entry<String, IEvalElement> entry : formulasForEvaluating
				.entrySet()) {
			IEvalElement evalElement = entry.getValue();
			try {
				s.unsubscribe(this, evalElement);
			} catch (Exception e) {
			}

		}
	}

	@Override
	public void traceChange(final Trace trace,
			final boolean currentAnimationChanged) {
		
		if (currentAnimationChanged) {
			// Deregister formulas if no trace exists and exit
			if (trace == null) {
				currentTrace = null;
				deregisterFormulas(currentModel);
				currentModel = null;
				return;
			}
			currentTrace = trace;
			currentModel = trace.getModel();
			// If a new formula was added dynamically (for instance via a groovy
			// script), call register formulas method
			if (formulasForEvaluating.containsValue(null)) {
				registerFormulas(currentModel);
			}
			// Collect results of subscibred formulas
			formulas.clear();
			Map<IEvalElement, IEvalResult> valuesAt = trace.getStateSpace()
					.valuesAt(trace.getCurrentState());
			for (Map.Entry<IEvalElement, IEvalResult> entry : valuesAt
					.entrySet()) {
				IEvalElement ee = entry.getKey();
				IEvalResult er = entry.getValue();
				if (er instanceof EvalResult) {
					formulas.put(ee.getCode(),
							translateValue(((EvalResult) er).getValue()));
				}
			}
			// Add all cached CSP formulas
			formulas.putAll(cachedCSPString);
			// Trigger all registered script listeners with collected formulas
			for (IBMotionScript s : scriptListeners) {
				s.traceChanged(currentTrace, formulas);
			}

		}

	}

	private void initJsonData() {

		if (templatePath == null) {
			return;
		}

		json = null;
		jsonPath = null;
		
		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("eval", new EvalExpression());

		String templateFolder = getTemplateFolder();
		Object jsonPaths = parameterMap.get("json");
		if (jsonPaths != null) {

			String[] sp = jsonPaths.toString().split(",");
			for (String s : sp) {
				jsonPath = templateFolder + "/" + s;
				File f = new File(jsonPath);
				if (f.exists()) {
					WebUtils.render(f.getPath(), scope);
					String jsonRendered = readFile(f.getPath());
					JsonParser jsonParser = new JsonParser();
					JsonElement jsonElement = jsonParser.parse(jsonRendered);
					if (!(jsonElement instanceof JsonNull))
						json = jsonElement;
				}

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

	private class EvalExpression implements Function<String, Object> {

		@Override
		public Object apply(final String input) {
			registerFormula(input.replace("\\\\", "\\"));
			return null;
		}
	}

	public void registerFormula(final String formula) {
		// Register a fresh new formula
		formulasForEvaluating.put(formula, null);
		// If a model exists, try to subscribe the formula
		if (currentModel != null) {
			subscribeFormula(formula, currentModel);
		}
	}

	private void subscribeFormula(final String formula,
			final AbstractModel model) {

		try {

			StateSpace s = model.getStatespace();
			IEvalElement evalElement = null;

			if (model instanceof CSPModel) {

				if (cachedCSPString.get(formula) == null) {
					evalElement = new CSP(formula, (CSPModel) model);
					IEvalResult evaluationResult = currentTrace
							.evalCurrent(evalElement);
					if (evaluationResult != null) {
						if (evaluationResult instanceof ComputationNotCompletedResult) {
							// TODO: do something .....
						} else if (evaluationResult instanceof EvalResult) {
							cachedCSPString.put(formula,
									((EvalResult) evaluationResult).getValue());
						}
					}
				}

			} else if (model instanceof EventBModel
					|| model instanceof ClassicalBModel) {
				if (model instanceof ClassicalBModel) {
					evalElement = new ClassicalB(formula);
				} else if (model instanceof EventBModel) {
					evalElement = new EventB(formula);
				}
				formulasForEvaluating.put(formula, evalElement);
				try {
					s.subscribe(this, evalElement);
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
			// TODO: do something ...
			// e.printStackTrace();
		}

	}

	private Object translateValue(final String val) {
		Object fvalue = val;
		if (val.equalsIgnoreCase("TRUE")) {
			fvalue = true;
		} else if (val.equalsIgnoreCase("FALSE")) {
			fvalue = false;
		}
		return fvalue;
	}

	public void toGui(final Object values) {
		submit(WebUtils.wrap("cmd", "bms.update_visualization", "values",
				values));
	}

	public void registerScript(final IBMotionScript script) {
		scriptListeners.add(script);
		if (currentTrace != null) {
			script.traceChanged(currentTrace, formulas);
		}
	}

	public void setTemplate(final String template) {
		this.templatePath = template;
	}

	public String getTemplate() {
		return templatePath;
	}

	private String getTemplateFolder() {
		if (templatePath != null) {
			return new File(templatePath).getParent();
		}
		return null;
	}

	public void addParameter(final String key, final Object value) {
		parameterMap.put(key, value);
	}

	@Override
	public void modelChanged(final StateSpace statespace) {
		for (IBMotionScript s : scriptListeners) {
			s.modelChanged(statespace);
		}
	}

	private void initGroovy() {

		if (templatePath == null) {
			return;
		}

		try {

			String templateFolder = getTemplateFolder();
			Bindings bindings = groovyScriptEngine.getBindings(ScriptContext.GLOBAL_SCOPE);
			bindings.putAll(parameterMap);
			bindings.put("bms", this);

			Object scriptPaths = parameterMap.get("script");
			if (scriptPaths != null) {
				String[] sp = scriptPaths.toString().split(",");
				for (String s : sp) {
					FileReader fr = new FileReader(templateFolder + "/" + s);
					groovyScriptEngine.eval(fr, bindings);
				}
			}

		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void animatorStatus(final boolean busy) {
		// TODO Auto-generated method stub

	}

	public void setTemplateDocument(Document templateDocument) {
		this.templateDocument = templateDocument;		
	}

	public Map<String, Object> getParameterMap() {
		return parameterMap;
	}
	
	public JsonElement getJson() {
		return json;
	}
	
}