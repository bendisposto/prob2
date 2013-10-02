package de.prob.web.views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.AsyncContext;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.inject.Inject;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.classicalb.ClassicalBVariable;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBVariable;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.Trace;
import de.prob.visualization.AnimationNotLoadedException;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class BMotionStudioSession extends AbstractSession implements
		IAnimationChangeListener {
	
	Logger logger = LoggerFactory.getLogger(BMotionStudioSession.class);
	
	private Trace currentTrace;
	
	private Map<String, IEvalElement> formulas = new ConcurrentHashMap<String, IEvalElement>();

	private List<FormulaElement> finalFormulas = new CopyOnWriteArrayList<BMotionStudioSession.FormulaElement>();
	
	private Map<String, Object> bvarmap = new HashMap<String, Object>();
	
	private List<ObserverElement> observerElements = new ArrayList<ObserverElement>();
	
	private String contentString, scriptingString;
	
	private final AbstractModel model;
	
	private AnimationSelector selector;
	
	@Inject
	public BMotionStudioSession(AnimationSelector selector) {
		this.selector = selector;
		currentTrace = selector.getCurrentTrace();
		if (currentTrace == null) {
			throw new AnimationNotLoadedException(
					"Please load model before opening Value over Time visualization");
		}
		model = currentTrace.getModel();
		selector.registerAnimationChangeListener(this);
	}
	
	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		Object scope = WebUtils.wrap("clientid", clientid, "id", UUID
				.randomUUID().toString());
		return WebUtils.render("/ui/bmsview/index.html", scope);
	}

	// ----------------------------------------------------------------
	// Rendering
	// ----------------------------------------------------------------
	
	private Map<String, Object> getDataMapForRendering() {
		
		Map<String, Object> dataWrap = new HashMap<String, Object>();
		for (FormulaElement f : finalFormulas) {
			EvaluationResult result = f.getResult();
			IEvalElement formula = f.getFormula();
			String value = result.getValue();
			if (formula.getKind().equals("#PREDICATE")) {
				boolean bvalue = false;
				if (value.equalsIgnoreCase("TRUE"))
					bvalue = true;
				dataWrap.put(f.getName(), bvalue);
			} else {
				dataWrap.put(f.getName(), value);
			}
		}
		dataWrap.put("b", bvarmap);
		
		// Collect observer
		List<Object> result = new ArrayList<Object>();
		for (ObserverElement o : observerElements) {
			Map<String, String> predicateObserver = WebUtils.wrap("template",
					o.getObserverPath(), "data", o.getData());
			result.add(predicateObserver);
		}
		
		String json = WebUtils.toJson(result);
		dataWrap.put("observer", json);
		
		return dataWrap;
		
	}
	
	private String getJsonDataForRendering() {
		return WebUtils.toJson(getDataMapForRendering());
	}
	
	public Object forcerendering(Map<String, String[]> params) {
		return WebUtils.wrap("cmd", "bms.renderVisualization", "data",
				getJsonDataForRendering());
	}
	
	public Object executeOperation(Map<String, String[]> params) {
		String op = params.get("op")[0];
		String predicate = "1=1";
		if(params.get("predicate") != null)
			predicate = params.get("predicate")[0];
		Trace currentTrace = selector.getCurrentTrace();
		try {
			if(predicate == null)
				predicate = "1=1";
			Trace newTrace = currentTrace.add(op,predicate);
			selector.replaceTrace(currentTrace, newTrace);
		} catch (BException e) {
			e.printStackTrace();
		}
//		Set<OpInfo> ops = currentTrace.getNextTransitions();
//		for (OpInfo opInfo : ops) {
//			String name = opInfo.name;
//			if (name.equals(op)) {
//				Trace newTrace = currentTrace.add(opInfo.id);
//				selector.replaceTrace(currentTrace, newTrace);
//			}
//		}
		return null;
	}
	
	// ----------------------------------------------------------------
	
	// ----------------------------------------------------------------
	// Managing Formulas
	// ----------------------------------------------------------------

	public Object removeFormula(final Map<String, String[]> params) {
		String id = params.get("id")[0];
		formulas.remove(id);
		for (FormulaElement element : finalFormulas) {
			if (element.id.equals(id)) {
				finalFormulas.remove(element);
			}
		}
		return WebUtils.wrap("cmd", "bms.formulaRemoved", "id", id);
	}
	
	public Object clearObserver(Map<String, String[]> params) {
		observerElements.clear();
		return null;
	}
	
	public Object addObserver(Map<String, String[]> params) {
		ObserverElement observerElement = new ObserverElement(
				params.get("observerPath")[0], params.get("json")[0]);
		observerElements.add(observerElement);
		return null;
	}
	
	public Object setTemplate(Map<String, String[]> params) {

		System.out.println("TESt");
		
		String templatePath = params.get("path")[0];
		try {
			File ff = new File(templatePath);
			String content = Files.toString(ff, Charsets.UTF_8);
			
			System.out.println(content);
			
			return WebUtils.wrap("cmd", "bms.setTemplate", "template", content);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	public Object addFormula(Map<String, String[]> params) {

		String id = params.get("id")[0];
		Boolean newFormula = Boolean.valueOf(params.get("newFormula")[0]);
		IEvalElement formula = formulas.get(id);
		if (formula == null) {
			return sendError(
					id,
					"Whoops!",
					"Could not add formula because it is invalid for this model",
					"alert-danger");
		}

		try {
			EvaluationResult res = currentTrace.evalCurrent(formula);
			if (res == null) {
				return sendError(
						id,
						"Warning!",
						"Could not add formula because it is not possible to assert the validity of the formula at this state in the animation",
						"");
			}
			if (res.hasError()) {
				return sendError(
						id,
						"Sorry!",
						"The specified formula cannot be evaluated for this model!",
						"alert-danger");
			}
			if (!correctType(id, res)) {
				return sendError(
						id,
						"Sorry!",
						"The specified formula must be of the correct type (Integer for time expression, Integer or boolean for other formula)",
						"alert-danger");
			}

			if (newFormula) {
				FormulaElement formulaElement = new FormulaElement(id,
						getUniqueName(), formula);
				evalFormula(formulaElement, currentTrace);
				finalFormulas.add(formulaElement);
				return WebUtils.wrap("cmd", "bms.formulaAdded", "id", id,
						"formula", formula.getCode(), "nextId", UUID
								.randomUUID().toString());
			}
			
			for (FormulaElement f : finalFormulas) {
				if (f.id.equals(id)) {
					f.formula = formula;					
					evalFormula(f, currentTrace);
				}
			}
			
			return WebUtils.wrap("cmd", "bms.formulaRestored", "id",
					id, "formula", formula.getCode());
		
		} catch (Exception e) {
			return sendError(
					id,
					"Whoops!",
					"Could not add formula because evaluation of the formula threw an exception of type "
							+ e.getClass().getSimpleName(), "alert-danger");
		}		
			
	}
	
	public Object parse(final Map<String, String[]> params) {
		String f = params.get("formula")[0];
		String id = params.get("id")[0];
		try {
			IEvalElement e = model.parseFormula(f);
			formulas.put(id, e);
			return WebUtils.wrap("cmd", "bms.parseOk", "id", id);
		} catch (Exception e) {
			formulas.remove(id);
			return WebUtils.wrap("cmd", "bms.parseError", "id", id);
		}
	}
	
	// ----------------------------------------------------------------
	
	private void collectBVariables(Trace trace) {
		AbstractElement mainComponent = model.getMainComponent();
		if (mainComponent instanceof EventBMachine) {
			EventBMachine eventbMachine = (EventBMachine) mainComponent;
			List<EventBVariable> variables = eventbMachine.getVariables();
			for (EventBVariable var : variables) {
				EvaluationResult eval = trace.evalCurrent(var.getExpression());
				String value = eval.getValue();
				boolean bvalue = false;
				if (value.equalsIgnoreCase("TRUE")) {
					bvalue = true;
					bvarmap.put(var.getName(), bvalue);
				} else if (value.equalsIgnoreCase("FALSE")) {
					bvalue = false;
					bvarmap.put(var.getName(), bvalue);
				} else {
					bvarmap.put(var.getName(), value);
				}
			}
		} else if (mainComponent instanceof ClassicalBMachine) {
			ClassicalBMachine classicalBMachine = (ClassicalBMachine) mainComponent;
			List<ClassicalBVariable> variables = classicalBMachine
					.getVariables();
			for (ClassicalBVariable var : variables) {
				EvaluationResult eval = trace.evalCurrent(var.getExpression());
				String value = eval.getValue();
				bvarmap.put(var.getName(), value);
			}
		}
	}
	
	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {

		super.reload(client, lastinfo, context);

		List<Object> result = new ArrayList<Object>();
		for (FormulaElement formula : finalFormulas) {
			result.add(WebUtils.wrap("id", formula.id, "formula",
					formula.formula.getCode()));
		}
		
		Map<String, String> wrap = WebUtils.wrap("cmd", "bms.restorePage",
				"formulas", WebUtils.toJson(result), "data",
				getJsonDataForRendering(), "template_content",
				this.contentString, "template_scripting", this.scriptingString);

		submit(wrap);
		
	}	
	
	public Object iframeLoaded(Map<String, String[]> params) {
		List<Object> resultObserver = new ArrayList<Object>();
		for(ObserverElement o : observerElements) {			
			resultObserver.add(WebUtils.wrap("observerPath",
					o.getObserverPath(), "data", o.getData()));
		}
		return WebUtils.wrap("cmd", "bms.restoreObserver",
				"data", WebUtils.toJson(resultObserver));
	}
	
	@Override
	public void traceChange(Trace trace) {

		if (trace != null
				&& trace.getStateSpace().equals(model.getStatespace())) {
			currentTrace = trace;
			// Evaluate formulas ...
			for (FormulaElement f : finalFormulas)
				evalFormula(f, trace);
			collectBVariables(trace);
		}
		// ... and force rendering
		submit(WebUtils.wrap("cmd", "bms.renderVisualization", "data",
				getJsonDataForRendering()));

	}

	private void evalFormula(FormulaElement f, Trace trace) {

		Assert.assertNotNull(f);
		Assert.assertNotNull(trace);
		EvaluationResult evalCurrent = trace.evalCurrent(f.formula);
		f.setResult(evalCurrent);

	}

	public Object saveTemplate(Map<String, String[]> params) {
		this.contentString = params.get("template_content")[0];
		this.scriptingString = params.get("template_scripting")[0];
		System.out.println("save template ...");
		return null;
	}

	private Map<String, String> sendError(final String id,
			final String emphasized, final String msg, final String level) {
		return WebUtils.wrap("cmd", "bms.error", "id", id, "msg", msg,
				"strong", emphasized, "alertLevel", level);
	}

	private boolean correctType(final String id, final EvaluationResult res) {
		String value = res.getValue();
		if ((value.equals("TRUE") || value.equals("FALSE"))
				&& !"time".equals(id)) {
			return true;
		}
		try {
			Integer.parseInt(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private String getUniqueName() {
		return getUniqueNameRecursive("o", finalFormulas.size());
	}

	private String getUniqueNameRecursive(String name, int counter) {
		String fname = name + counter;
		for (FormulaElement f : finalFormulas) {
			if (f.getName().equals(fname))
				return getUniqueNameRecursive(name, counter++);
		}
		return fname;
	}
	
	private class FormulaElement {

		public final String id;
		public IEvalElement formula;
		private EvaluationResult result;
		private String name;

		public FormulaElement(String id, String name, IEvalElement formula) {
			this.id = id;
			this.name = name;
			this.formula = formula;
		}

		public EvaluationResult getResult() {
			return result;
		}

		public void setResult(EvaluationResult result) {
			this.result = result;
		}

		public String getName() {
			return name;
		}

		public IEvalElement getFormula() {
			return formula;
		}

	}
	
	private class ObserverElement {
		
		private String name;
		private String id;		
		private String observerPath;
		private String data;
				
		public ObserverElement(String observerPath, String data) {
			this.observerPath = observerPath;
			this.data = data;
		}

		public String getObserverPath() {
			return observerPath;
		}

		public String getData() {
			return data;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		
	}

}
