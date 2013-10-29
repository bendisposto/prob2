package de.prob.web.views;

import java.io.File;
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

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.classicalb.ClassicalBVariable;
import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBConstant;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.EventBVariable;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.Trace;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class BMotionStudioSession extends AbstractSession implements
		IAnimationChangeListener {

	Logger logger = LoggerFactory.getLogger(BMotionStudioSession.class);

	private Trace currentTrace;

	private final Map<String, IEvalElement> formulas = new ConcurrentHashMap<String, IEvalElement>();

	private final List<FormulaElement> finalFormulas = new CopyOnWriteArrayList<BMotionStudioSession.FormulaElement>();

	// private Map<String, Object> bmachinemap = new HashMap<String, Object>();

	private final List<ObserverElement> observerElements = new ArrayList<ObserverElement>();

	private AbstractModel model;

	private final AnimationSelector selector;

	private String templateFileName;

	@Inject
	public BMotionStudioSession(final AnimationSelector selector) {
		this.selector = selector;
		currentTrace = selector.getCurrentTrace();
		if (currentTrace == null) {
			// throw new AnimationNotLoadedException(
			// "Please load model before opening Value over Time visualization");
		} else {
			model = currentTrace.getModel();
			// if(model instanceof EventBModel) {
			// EventBModel eventbModel = (EventBModel) model;
			// bmachinemap.put("name", eventbModel.getMainComponentName());
			// bmachinemap.put("constants", getBConstantsAsJson(eventbModel));
			// }
			selector.registerAnimationChangeListener(this);
		}
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		Object scope = WebUtils
				.wrap("clientid", clientid, "id", UUID.randomUUID().toString(),
						"workspace", "/home/lukas/.prob/bms/");
		return WebUtils.render("/ui/bmsview/index.html", scope);
	}

	// ----------------------------------------------------------------
	// Rendering
	// ----------------------------------------------------------------

	private Map<String, Object> getJsonDataForRendering() {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String, Object> fMap = new HashMap<String, Object>();
		modelMap.put("model", fMap);

		if (model instanceof EventBModel) {

			EventBModel eventbModel = (EventBModel) model;
			fMap.put("name", ((EventBModel) model).getMainComponentName());
			Map<String, Object> bVariablesAsJson = getBVariablesAsJson(
					currentTrace, eventbModel);
			fMap.put("variables", bVariablesAsJson);
			Map<String, Object> bConstantsAsJson = getBConstantsAsJson(
					currentTrace, eventbModel);

			// List<Object> constantList = new ArrayList<Object>();
			// bConstantsAsJson.put("list", constantList);
			fMap.put("constants", bConstantsAsJson);

			// Collect observer
			// List<Object> result = new ArrayList<Object>();
			// for (ObserverElement o : observerElements) {
			// Map<String, String> predicateObserver = WebUtils.wrap("template",
			// o.getObserverPath(), "data", o.getData());
			// result.add(predicateObserver);
			// }

			// String json = WebUtils.toJson(result);
			// dataWrap.put("observer", json);
			// bmachinemap.put("var", dataWrap);
			// dataWrap.put("model", bmachinemap);

		}

		return modelMap;

	}

	// private String getJsonDataForRendering() {
	// return WebUtils.toJson(getDataMapForRendering());
	// }

	public Object forcerendering(final Map<String, String[]> params) {
		return WebUtils.wrap("cmd", "bms.renderVisualization", "templatefile",
				templateFileName, "data",
				WebUtils.toJson(getJsonDataForRendering()));
	}

	public Object executeOperation(final Map<String, String[]> params) {
		String op = params.get("op")[0];
		String predicate = "1=1";
		if (params.get("predicate") != null) {
			predicate = params.get("predicate")[0];
		}
		Trace currentTrace = selector.getCurrentTrace();
		try {
			if (predicate == null) {
				predicate = "1=1";
			}
			Trace newTrace = currentTrace.add(op, predicate);
			selector.replaceTrace(currentTrace, newTrace);
		} catch (BException e) {
			e.printStackTrace();
		}
		// Set<OpInfo> ops = currentTrace.getNextTransitions();
		// for (OpInfo opInfo : ops) {
		// String name = opInfo.name;
		// if (name.equals(op)) {
		// Trace newTrace = currentTrace.add(opInfo.id);
		// selector.replaceTrace(currentTrace, newTrace);
		// }
		// }
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

	public Object clearObserver(final Map<String, String[]> params) {
		observerElements.clear();
		return null;
	}

	public Object addObserver(final Map<String, String[]> params) {
		ObserverElement observerElement = new ObserverElement(
				params.get("observerPath")[0], params.get("json")[0]);
		observerElements.add(observerElement);
		return null;
	}

	public Object setTemplate(final Map<String, String[]> params) {
		String templatePath = params.get("path")[0];
		File ff = new File(templatePath);
		templateFileName = ff.getName();
		return WebUtils.wrap("cmd", "bms.setTemplate", "templatefile",
				templateFileName, "data",
				WebUtils.toJson(getJsonDataForRendering()));
	}

	public Object addFormula(final Map<String, String[]> params) {

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

			return WebUtils.wrap("cmd", "bms.formulaRestored", "id", id,
					"formula", formula.getCode());

		} catch (Exception e) {
			e.printStackTrace();
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

	private Map<String, Object> getBConstantsAsJson(final Trace trace,
			final EventBModel model) {
		Map<String, Object> map = new HashMap<String, Object>();
		AbstractElement mainComponent = model.getMainComponent();
		if (mainComponent instanceof EventBMachine) {
			EventBMachine machine = (EventBMachine) mainComponent;
			List<Context> sees = machine.getSees();
			for (Context context : sees) {
				List<EventBConstant> constants = context.getConstants();
				for (EventBConstant c : constants) {
					EvaluationResult value = c.getValue(currentTrace);
					if (value != null) {
						map.put(c.getName(), value.getValue());
					}
				}
			}
		}
		return map;
	}

	private Map<String, Object> getBVariablesAsJson(final Trace trace,
			final EventBModel model) {
		Map<String, Object> map = new HashMap<String, Object>();
		AbstractElement mainComponent = model.getMainComponent();
		if (mainComponent instanceof EventBMachine) {
			EventBMachine eventbMachine = (EventBMachine) mainComponent;
			List<EventBVariable> variables = eventbMachine.getVariables();
			for (EventBVariable var : variables) {
				EvaluationResult eval = trace.evalCurrent(var.getExpression());
				if (eval != null) {
					String value = eval.getValue();
					boolean bvalue = false;
					if (value.equalsIgnoreCase("TRUE")) {
						bvalue = true;
						map.put(var.getName(), bvalue);
					} else if (value.equalsIgnoreCase("FALSE")) {
						bvalue = false;
						map.put(var.getName(), bvalue);
					} else {
						map.put(var.getName(), value);
					}
				}
			}
		} else if (mainComponent instanceof ClassicalBMachine) {
			ClassicalBMachine classicalBMachine = (ClassicalBMachine) mainComponent;
			List<ClassicalBVariable> variables = classicalBMachine
					.getVariables();
			for (ClassicalBVariable var : variables) {
				EvaluationResult eval = trace.evalCurrent(var.getExpression());
				String value = eval.getValue();
				map.put(var.getName(), value);
			}
		}
		return map;
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {

		super.reload(client, lastinfo, context);

		submit(WebUtils.wrap("cmd", "bms.setTemplate", "templatefile",
				templateFileName, "data",
				WebUtils.toJson(getJsonDataForRendering())));

		// List<Object> result = new ArrayList<Object>();
		// for (FormulaElement formula : finalFormulas) {
		// result.add(WebUtils.wrap("id", formula.id, "formula",
		// formula.formula.getCode()));
		// }
		//
		// Map<String, String> wrap = WebUtils.wrap("cmd", "bms.restorePage",
		// "formulas", WebUtils.toJson(result), "data",
		// getJsonDataForRendering(), "template_content",
		// this.contentString, "template_scripting", this.scriptingString);
		//
		// submit(wrap);

	}

	public Object iframeLoaded(final Map<String, String[]> params) {
		List<Object> resultObserver = new ArrayList<Object>();
		for (ObserverElement o : observerElements) {
			resultObserver.add(WebUtils.wrap("observerPath",
					o.getObserverPath(), "data", o.getData()));
		}
		return WebUtils.wrap("cmd", "bms.restoreObserver", "data",
				WebUtils.toJson(resultObserver));
	}

	@Override
	public void traceChange(final Trace trace) {

		if (trace != null
				&& trace.getStateSpace().equals(model.getStatespace())) {
			currentTrace = trace;
			// Evaluate formulas ...
			for (FormulaElement f : finalFormulas) {
				evalFormula(f, trace);
			}
		}
		// ... and force rendering
		submit(WebUtils.wrap("cmd", "bms.renderVisualization", "data",
				WebUtils.toJson(getJsonDataForRendering())));

	}

	private void evalFormula(final FormulaElement f, final Trace trace) {

		Assert.assertNotNull(f);
		Assert.assertNotNull(trace);
		EvaluationResult evalCurrent = trace.evalCurrent(f.formula);
		f.setResult(evalCurrent);

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

	private String getUniqueNameRecursive(final String name, int counter) {
		String fname = name + counter;
		for (FormulaElement f : finalFormulas) {
			if (f.getName().equals(fname)) {
				return getUniqueNameRecursive(name, counter++);
			}
		}
		return fname;
	}

	private class FormulaElement {

		public final String id;
		public IEvalElement formula;
		private EvaluationResult result;
		private final String name;

		public FormulaElement(final String id, final String name,
				final IEvalElement formula) {
			this.id = id;
			this.name = name;
			this.formula = formula;
		}

		public EvaluationResult getResult() {
			return result;
		}

		public void setResult(final EvaluationResult result) {
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

		private final String observerPath;
		private final String data;

		public ObserverElement(final String observerPath, final String data) {
			this.observerPath = observerPath;
			this.data = data;
		}

		public String getObserverPath() {
			return observerPath;
		}

		public String getData() {
			return data;
		}

	}

}
