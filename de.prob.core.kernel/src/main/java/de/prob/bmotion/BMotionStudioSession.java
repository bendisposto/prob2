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
import de.prob.animator.domainobjects.EvaluationException;
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
	
	private final AnimationSelector selector;
	
	private final Api api;

	private String templatePath;
	
	private final ScriptEngine groovyScriptEngine;

	private final Map<String, Object> parameterMap = new HashMap<String, Object>();

	private final Map<String, Object> cachedCspResults = new HashMap<String, Object>();

	private final Map<String, IEvalElement> formulasForEvaluating = new HashMap<String, IEvalElement>();
	
	private final List<String> invalidFormulas = new ArrayList<String>();
	
	private final Map<String, Object> formulas = new HashMap<String, Object>();
	
	private final Observer defaultObserver;
	
	private JsonElement json;
	
	private AbstractModel model;
	
	private int port;
	
	private String host;

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
			s.traceChanged(currentTrace);
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

	public void registerFormula(String formula) {
		if (!formulasForEvaluating.containsKey(formula))
			formulasForEvaluating.put(formula, null);
	}

	private void clearSession() {
		// Clear formula data
		formulas.clear();
		formulasForEvaluating.clear();
	}
	
	/**
	 * This method initializes the session.
	 */
	private void initSession() {
		clearSession();
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

	/**
	 * This method is responsible to reference a model. If a reference model
	 * already exists, the method is existed. If no model is referenced: (1) If
	 * the visualization template points to a specific model, the method tries
	 * to start this model and takes it as the reference model. (2) Else the
	 * method checks if a model is currently animated by ProB2 and takes this
	 * model as the reference model.
	 */
	private void initFormalModel() {

		// Exit method if a model already exists
		if (this.model != null)
			return;

		Object machinePath = getParameterMap().get("machine");

		// If the template references a specific model, try to load this model
		if (machinePath != null) {

			File machineFile = new File(machinePath.toString());

			if (!machineFile.isAbsolute())
				machinePath = getTemplateFolder() + "/" + machinePath;

			String formalism = getFormalism(machinePath.toString());

			if (formalism != null) {

				try {
					Method method = api.getClass().getMethod(
							formalism + "_load", String.class);
					this.model = (AbstractModel) method
							.invoke(api, machinePath);
					StateSpace s = model.getStateSpace();
					selector.addNewAnimation(new Trace(s));
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

		} else {
			// Else check if a current animation exists, and take the
			// corresponding model
			Trace traceFromAnimatedModel = selector.getCurrentTrace();
			if (traceFromAnimatedModel != null)
				this.model = traceFromAnimatedModel.getModel();

		}

	}

	private void deregisterFormulas(final AbstractModel model) {
		StateSpace s = model.getStateSpace();
		for (Map.Entry<String, IEvalElement> entry : formulasForEvaluating
				.entrySet()) {
			IEvalElement evalElement = entry.getValue();
			try {
				s.unsubscribe(this, evalElement);
			} catch (Exception e) {
			}

		}
	}

	private void registerFormulas(final AbstractModel model, final Trace trace) {
		for (Map.Entry<String, IEvalElement> entry : formulasForEvaluating
				.entrySet()) {
			if (entry.getValue() == null)
				subscribeFormula(entry.getKey(), model, trace);
		}
	}
	
	@Override
	public void traceChange(final Trace trace,
			final boolean currentAnimationChanged) {
		
		if (currentAnimationChanged) {

			// Deregister formulas if no trace exists and exit
			if (trace == null) {
				currentTrace = null;
				deregisterFormulas(model);
				return;
			}
		
			// Trigger only if a reference model exists and the reference model
			// is the same as the model of the changed trace (they have the same
			// model files)
			if (model != null
					&& model.getModelFile().equals(
							trace.getModel().getModelFile())) {

				currentTrace = trace;
				StateSpace stateSpace = currentTrace.getStateSpace();

				if (formulasForEvaluating.containsValue(null)) {
					registerFormulas(model, trace);
					formulasForEvaluating.keySet().removeAll(invalidFormulas);
				}

				Map<IEvalElement, IEvalResult> valuesAt = stateSpace
						.valuesAt(trace.getCurrentState());
				for (Map.Entry<IEvalElement, IEvalResult> entry : valuesAt
						.entrySet()) {
					IEvalElement evalElement = entry.getKey();
					IEvalResult evalResult = entry.getValue();
					if (evalResult instanceof EvalResult) {
						formulas.put(evalElement.getCode(),
								translateValue(((EvalResult) evalResult)
										.getValue()));
					}
				}

				// Trigger all registered script listeners with collected
				// formulas
				for (IBMotionScript s : scriptListeners) {
					s.traceChanged(currentTrace);
				}

			}

		}

	}

	private void initJsonData() {

		if (getTemplatePath() != null) {

			Map<String, Object> scope = new HashMap<String, Object>();
			scope.put("eval", new EvalExpression());

			String templateFolder = getTemplateFolder();
			Object jsonPaths = parameterMap.get("json");
			if (jsonPaths != null) {

				String[] sp = jsonPaths.toString().split(",");
				for (String s : sp) {
					String jsonPath = templateFolder + "/" + s;
					File f = new File(jsonPath);
					if (f.exists()) {
						WebUtils.render(f.getPath(), scope);
						String jsonRendered = readFile(f.getPath());
						JsonParser jsonParser = new JsonParser();
						JsonElement jsonElement = jsonParser
								.parse(jsonRendered);
						if (!(jsonElement instanceof JsonNull))
							this.json = jsonElement;
					}

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
			try {
				return eval(input.replace("\\\\", "\\"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	private Object subscribeFormula(final String formula,
			final AbstractModel model, Trace trace) {

		Object result = null;

		try {

			StateSpace s = model.getStateSpace();
			IEvalElement evalElement = null;

			if (model instanceof CSPModel) {

				try {
					result = cachedCspResults.get(formula);
					if (result == null) {
						evalElement = new CSP(formula, (CSPModel) model);
						IEvalResult evaluationResult = trace
								.evalCurrent(evalElement);
						if (evaluationResult != null) {
							if (evaluationResult instanceof ComputationNotCompletedResult) {
								// TODO: do something .....
							} else if (evaluationResult instanceof EvalResult) {
								result = ((EvalResult) evaluationResult)
										.getValue();
								cachedCspResults.put(formula, result);
							}
						}
					}
				} catch (EvaluationException e) {
					System.err.println("Invalid CSP formula: " + formula);
				}

			} else if (model instanceof EventBModel
					|| model instanceof ClassicalBModel) {
				if (model instanceof ClassicalBModel) {
					evalElement = new ClassicalB(formula);
				} else if (model instanceof EventBModel) {
					evalElement = new EventB(formula);
				}

				result = getResultFromSubscription(evalElement, s, trace);
				if (result == null) {
					try {
						s.subscribe(this, evalElement);
						formulasForEvaluating.put(formula, evalElement);
						result = getResultFromSubscription(evalElement, s,
								trace);
						formulas.put(formula, result);
					} catch (Exception e) {
						System.err.println("Invalid B formula: " + formula);
						invalidFormulas.add(formula);
					}
				}

			}

		} catch (Exception e) {
			// TODO: do something ...
			e.printStackTrace();
		}

		return result;

	}
	
	private Object getResultFromSubscription(IEvalElement evalElement,
			StateSpace s, Trace t) {
		Map<IEvalElement, IEvalResult> valuesAt = s.valuesAt(currentTrace
				.getCurrentState());
		EvalResult evalResult = (EvalResult) valuesAt.get(evalElement);
		if (evalResult != null)
			return translateValue(evalResult.getValue());
		return null;
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
		if (currentTrace != null)
			script.traceChanged(currentTrace);
		if (model != null)
			script.modelChanged(model.getStateSpace());
	}

	public String getTemplateFolder() {
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
		// The session is (re)initialized if a model change was detected and (1)
		// no model exist yet or (2) the current model is the same as the new
		// model (they have the same model files).
		if (model == null
				|| (model != null && model.getModelFile().equals(
						statespace.getModel().getModelFile()))) {
			clearSession();
			initFormalModel();
			for (IBMotionScript s : scriptListeners) {
				s.modelChanged(statespace);
			}
		}
	}

	/**
	 * This method initializes the Groovy script which is referenced by the
	 * visualization template.
	 */
	private void initGroovy() {

		if (templatePath == null)
			return;

		try {

			String templateFolder = getTemplateFolder();
			Bindings bindings = groovyScriptEngine
					.getBindings(ScriptContext.GLOBAL_SCOPE);
			bindings.putAll(parameterMap);
			bindings.put("bms", this);
			bindings.put("templatefolder", templateFolder);

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

	public AbstractModel getModel() {
		return model;
	}

	public void setModel(AbstractModel model) {
		this.model = model;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public String getFormalism(String machinePath) {

		String lang = null;
		if (machinePath.endsWith(".csp")) {
			return "csp";
		} else if (machinePath.endsWith(".buc") || machinePath.endsWith(".bcc")
				|| machinePath.endsWith(".bum") || machinePath.endsWith(".bcm")) {
			return "eventb";
		} else if (machinePath.endsWith(".mch")) {
			return "b";
		} else if(machinePath.endsWith(".tla")) {
			return "tla";
		}
		return lang;

	}
	
	// ---------- BMS API
	public void toGui(final Object json) {
		submit(json);
	}
	
	/**
	 * 
	 * This method calls a list of JavaScript calls represented as Strings on
	 * the GUI.
	 * 
	 * @param values
	 *            A list of JavaScript call represented as Strings
	 */
	public void callJs(final Object values) {
		submit(WebUtils.wrap("cmd", "bms.update_visualization", "values",
				values));
	}

	/**
	 * 
	 * This method evaluates a given formula and returns the corresponding
	 * result. The method tries first to get the result for the given formula
	 * from the cache (The cache is constructed by the subscription mechanism of
	 * ProB2). If no result is cached, the method subscribes the formula and
	 * returns the result.
	 * 
	 * @param formula
	 *            The formula to evaluate
	 * @return the result of the formula or null if no result was found or no
	 *         reference model and no trace exists
	 * @throws Exception
	 */
	public Object eval(final String formula) throws Exception {
		if (model != null && currentTrace != null)
			return formulas.get(formula) != null ? formulas.get(formula)
					: subscribeFormula(formula, model, currentTrace);
		return null;
	}
	
	public Object executeOperation(final Map<String, String[]> params) {
		String[] id = params.get("id");
		String[] op = params.get("op");
		String[] predicate = params.get("predicate");
		if (id != null) {
			Trace newTrace = currentTrace.add(id[0]);
			selector.replaceTrace(currentTrace, newTrace);
			return null;
		} else if (op != null && predicate != null) {
			String fpredicate = predicate[0];
			if (fpredicate.isEmpty()) {
				fpredicate = "1=1";
			}
			Trace currentTrace = selector.getCurrentTrace();
			try {
				Trace newTrace = currentTrace.add(op[0], fpredicate);
				selector.replaceTrace(currentTrace, newTrace);
			} catch (BException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	// ------------------
	
}