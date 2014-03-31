package de.prob.bmotion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
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
import de.prob.scripting.Api;
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
	
	private final Api api;

	private String jsonPath;
	
	private String templatePath;
	
	private String language;
	
	private final ScriptEngine groovyScriptEngine;

	private final Map<String, Object> parameterMap = new HashMap<String, Object>();

	private final Map<String, Object> formulas = new HashMap<String, Object>();

	private final Map<String, IEvalElement> formulasForEvaluating = new HashMap<String, IEvalElement>();

	private final Map<String, Object> cachedCspResults = new HashMap<String, Object>();

	private final Observer defaultObserver;
	
	private JsonElement json;
	
	private boolean modelStarted = false;
	
	private String modelFile;

	private final List<IBMotionScript> scriptListeners = new ArrayList<IBMotionScript>();

	@Inject
	public BMotionStudioSession(final AnimationSelector selector,
			final ScriptEngineProvider sep, final Api api) {
		this.api = api;
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
		// Init formal model
		initFormalModel();
		// Initialize json data (if not already done)
		initJsonData();
		// Init Groovy scripts
		initGroovy();
	}

	private void initFormalModel() {

		Object formalism = getParameterMap().get("lang");
		Object machinePath = getParameterMap().get("machine");

		if (machinePath != null && formalism != null) {

			File machineFile = new File(machinePath.toString());

			if (!machineFile.isAbsolute())
				machinePath = getTemplateFolder() + "/" + machinePath;

			if (!modelStarted
					|| (currentModel != null && !currentModel.getModelFile()
							.getAbsolutePath().equals(machinePath))) {
				try {
					Method method = api.getClass().getMethod(
							formalism + "_load", String.class);
					AbstractModel model = (AbstractModel) method.invoke(api,
							machinePath);
					StateSpace s = model.getStatespace();
					selector.addNewAnimation(new Trace(s));
					modelStarted = true;
					setModelFile(s.getModel().getModelFile().getAbsolutePath());
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
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

			if (modelFile != null
					&& modelFile.equals(trace.getModel().getModelFile()
							.getAbsolutePath())) {

				currentTrace = trace;
				currentModel = trace.getModel();

				checkSubscribedFormulas(currentTrace, currentModel);

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
				formulas.putAll(cachedCspResults);
				// Trigger all registered script listeners with collected
				// formulas
				for (IBMotionScript s : scriptListeners) {
					s.traceChanged(currentTrace, formulas);
				}

			}

		}

	}

	private void checkSubscribedFormulas(Trace trace, AbstractModel model) {

		// Subscribe formulas if not done yet ...
		for (Map.Entry<String, IEvalElement> entry : formulasForEvaluating
				.entrySet()) {
			IEvalElement evalElement = entry.getValue();
			if (evalElement == null
					|| !trace.getStateSpace().isSubscribed(evalElement)) {
				subscribeFormula(entry.getKey(), currentModel);
			}
		}

	}

	private void initJsonData() {

		if (getTemplatePath() == null) {
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

	private Object subscribeFormula(final String formula,
			final AbstractModel model) {

		Object result = null;
		
		try {
			
			StateSpace s = model.getStatespace();
			IEvalElement evalElement = null;

			if (model instanceof CSPModel) {
				
				if (cachedCspResults.get(formula) == null) {
					evalElement = new CSP(formula, (CSPModel) model);
					IEvalResult evaluationResult = currentTrace
							.evalCurrent(evalElement);
					if (evaluationResult != null) {
						if (evaluationResult instanceof ComputationNotCompletedResult) {
							// TODO: do something .....
						} else if (evaluationResult instanceof EvalResult) {
							result = ((EvalResult) evaluationResult).getValue();
							cachedCspResults.put(formula, result);
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
					Map<IEvalElement, IEvalResult> valuesAt = s
							.valuesAt(currentTrace.getCurrentState());
					return translateValue(((EvalResult) valuesAt
							.get(evalElement)).getValue());
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
			// TODO: do something ...
			e.printStackTrace();
		}

		return result;
		
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

	public void registerScript(final IBMotionScript script) {
		scriptListeners.add(script);
		if (currentTrace != null) {
			script.traceChanged(currentTrace, formulas);
		}
	}

	private String getTemplateFolder() {
		String template = getTemplatePath();
		if (template != null) {
			return new File(template).getParent();
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

		if (getTemplatePath() == null) {
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

	public Map<String, Object> getParameterMap() {
		return parameterMap;
	}
	
	public JsonElement getJson() {
		return json;
	}
	
	public void setTemplatePath(final String templatePath) {
		this.templatePath = templatePath;
	}

	public String getTemplatePath() {
		return templatePath;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getModelFile() {
		return modelFile;
	}

	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}
	
	// ---------- BMS API
	// public void toGui(final Object values) {
	// submit(WebUtils.wrap("cmd", "bms.update_visualization", "values",
	// values));
	// }
	
	public void toGui(final Object json) {
		submit(json);
	}

	public Object registerFormula(final String formula) {
		Object result = null;
		// Register a fresh new formula
		formulasForEvaluating.put(formula, null);
		// If a model exists, try to subscribe the formula
		if (currentModel != null)
			result = subscribeFormula(formula, currentModel);
		return result;
	}
	
	public Object eval(final String formula) throws Exception {
		if (currentModel == null)
			throw new Exception("No model loaded. Evaluating " + formula
					+ " not possible.");
		Object result = formulas.get(formula);
		if (result == null)
			result = subscribeFormula(formula, currentModel);
		return result;
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
	// ------------------
	
}