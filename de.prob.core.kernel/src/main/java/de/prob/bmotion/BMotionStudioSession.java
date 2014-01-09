package de.prob.bmotion;

import java.io.File;
import java.io.FileNotFoundException;
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

import org.eclipse.jetty.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
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

	private String template;
	
	private final ScriptEngine groovy;

	private Map<String, Object> parameterMap = new HashMap<String, Object>();
	
	private Map<String, Object> formulas = new HashMap<String, Object>();
	
	private Map<String, IEvalElement> formulasForEvaluating = new HashMap<String, IEvalElement>();

	private Map<String, String> cachedCSPString = new HashMap<String, String>();
	
	private List<Object> jsonData = new ArrayList<Object>();
	
	private Observer observer;
	
	private List<IBMotionScript> scriptListeners = new ArrayList<IBMotionScript>();

	@Inject
	public BMotionStudioSession(final AnimationSelector selector,
			final ScriptEngineProvider sep) {
		this.selector = selector;
		incrementalUpdate = false;
		currentTrace = selector.getCurrentTrace();
		groovy = sep.get();
		observer = new Observer(this);
		selector.registerAnimationChangeListener(this);
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return null;
	}

	public Object executeOperation(final Map<String, String[]> params) {
		String op = params.get("op")[0];
		String predicate = params.get("predicate")[0];
		if (predicate.isEmpty())
			predicate = "1=1";
		Trace currentTrace = selector.getCurrentTrace();
		try {
			Trace newTrace = currentTrace.add(op, predicate);
			selector.replaceTrace(currentTrace, newTrace);
		} catch (BException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object setTemplate(final Map<String, String[]> params) {
		String fullTemplatePath = params.get("path")[0];
		submit(WebUtils.wrap("cmd", "bms.setTemplate", "request",
				fullTemplatePath));
		return null;
	}
	
	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {

		// Remove all script listeners and add new observer scriptlistener
		scriptListeners.clear();
		scriptListeners.add(observer);
		// After reload do not resent old messages
		int old = responses.size() + 1;
		// Add dummy message
		submit(WebUtils.wrap("cmd", "extern.skip"));
		// Initialize json data (if not already done)
		initJsonData();
		// Init Groovy scripts
		initGroovy();
		// Trigger an init trace change
		if(currentTrace != null)
			traceChange(currentTrace);
		// Resent messages from groovy script and init trace change
		if (!responses.isEmpty())
			resend(client, old, context);

		super.reload(client, lastinfo, context);

	}

	private void registerFormulas(final AbstractModel model) {

		System.out.println("REGISTER FORMULAS");
		
		StateSpace s = model.getStatespace();

		System.out.println(formulasForEvaluating);
		
		for (Map.Entry<String, IEvalElement> entry : formulasForEvaluating
				.entrySet()) {

			String formula = entry.getKey();
			IEvalElement evalElement = entry.getValue();

			if (evalElement == null) {

				try {

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
											((EvalResult) evaluationResult)
													.getValue());
								}
							}
						}

					} else if (model instanceof EventBModel
							|| model instanceof ClassicalBModel) {

						if (model instanceof ClassicalBModel)
							evalElement = new ClassicalB(formula);
						else if (model instanceof EventBModel)
							evalElement = new EventB(formula);
						
						formulasForEvaluating.put(formula, evalElement);
						s.subscribe(this, evalElement);

					}

				} catch (Exception e) {
					// TODO: do something ...
					// e.printStackTrace();
				}

			}

		}

	}

	private void deregisterFormulas(final AbstractModel model) {
		StateSpace s = model.getStatespace();
		for (Map.Entry<String, IEvalElement> entry : formulasForEvaluating
				.entrySet()) {
			IEvalElement evalElement = entry.getValue();
			s.unsubscribe(this, evalElement);
		}
	}

	@Override
	public void traceChange(final Trace trace) {

		// Deregister formulas if no trace exists ...
		if (trace == null) {
			currentTrace = null;
			deregisterFormulas(currentModel);
			currentModel = null;
			return;
		}

		this.currentTrace = trace;

		// If the model has been changed: (1) deregister formulas in the "old"
		// model and (2) register formulas for new model. Else check if new
		// formulas has been added and re register formulas
		AbstractModel newModel = trace.getModel();
		if (!newModel.equals(currentModel)) {
			if (currentModel != null) {
				deregisterFormulas(currentModel);
			}
			this.currentModel = newModel;
			registerFormulas(currentModel);
		} else if (formulasForEvaluating.values().contains(null)) {
			registerFormulas(currentModel);
		}

		// Collect subscribed values ...
		Map<IEvalElement, IEvalResult> valuesAt = trace.getStateSpace()
				.valuesAt(trace.getCurrentState());
		for (Map.Entry<IEvalElement, IEvalResult> entry : valuesAt.entrySet()) {
			IEvalElement ee = entry.getKey();
			IEvalResult er = entry.getValue();
			if (er instanceof EvalResult) {
				formulas.put(ee.getCode(),
						translateValue(((EvalResult) er).getValue()));
			}
		}
		// Add all cached CSP formulas
		formulas.putAll(cachedCSPString);

		// Trigger all registered script listeners
		for (IBMotionScript s : scriptListeners) {
			s.traceChange(trace, formulas);
		}

	}

	private void initJsonData() {

		if (template == null)
			return;

		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("eval", new EvalExpression());

		String templateFolder = getTemplateFolder();
		Object jsonPaths = parameterMap.get("json");
		if (jsonPaths != null) {
			String[] sp = jsonPaths.toString().split(",");
			for (String s : sp) {
				File f = new File(templateFolder + "/" + s);
				WebUtils.render(f.getPath(), scope);
				String jsonRendered = readFile(f.getPath());
				jsonData.add(JSON.parse(jsonRendered));
			}
		}

	}

	private String readFile(String filename) {
		String content = null;
		File file = new File(filename); // for ex foo.txt
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

	public void registerFormula(String formula) {
		formulasForEvaluating.put(formula, null);
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

	public void toVisualization(Object values) {
		submit(WebUtils.wrap("cmd", "bms.update_visualization", "values",
				values));
	}

	public void registerScript(IBMotionScript script) {
		scriptListeners.add(script);
		if (currentTrace != null)
			script.traceChange(currentTrace, formulas);
	}

	public List<Object> getJsonData() {
		return jsonData;
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}

	public String getTemplate() {
		return this.template;
	}
	
	private String getTemplateFolder() {
		if(this.template != null)
			return new File(this.template).getParent();
		return null;
	}
	
	public void addParameter(String key, Object value) {
		this.parameterMap.put(key, value);
	}

	@Override
	public void modelChanged(StateSpace s) {
		// TODO: Reload  ........
	}
	
	private void initGroovy() {

		if (template == null)
			return;
		
		try {

			String templateFolder = getTemplateFolder();
			Bindings bindings = groovy.getBindings(ScriptContext.GLOBAL_SCOPE);
			bindings.putAll(parameterMap);
			bindings.put("bms", this);

			Object scriptPaths = parameterMap.get("script");
			if (scriptPaths != null) {
				String[] sp = scriptPaths.toString().split(",");
				for (String s : sp) {
					FileReader fr = new FileReader(templateFolder + "/" + s);
					groovy.eval(fr, bindings);
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
	
}