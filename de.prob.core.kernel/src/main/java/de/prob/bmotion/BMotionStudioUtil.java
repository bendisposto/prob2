package de.prob.bmotion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON;

import com.google.common.base.Function;

import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.ClassicalBVariable;
import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBConstant;
import de.prob.model.eventb.EventBInvariant;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.EventBVariable;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.scripting.CSPModel;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.web.WebUtils;

public class BMotionStudioUtil {
	
	private static HashMap<String, Object> cspcache = new HashMap<String, Object>();

	private static Map<String, Object> getBInvariantsAsJson(Trace trace,
			EventBModel model) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, AbstractElement> components = model.getComponents();
		for (AbstractElement e : components.values()) {
			if (e instanceof EventBMachine) {
				EventBMachine eventbMachine = (EventBMachine) e;
				List<EventBInvariant> invariants = eventbMachine
						.getInvariants();
				for (EventBInvariant inv : invariants) {
					map.put(inv.getName(), "\"" + inv.getPredicate() + "\"");
				}
			}
		}
		return map;
	}
	
	private static Map<String, Object> getBConstantsAsJson(Trace trace,
			EventBModel model) {
		Map<String, Object> map = new HashMap<String, Object>();
		AbstractElement mainComponent = model.getMainComponent();
		if (mainComponent instanceof EventBMachine) {
			EventBMachine machine = (EventBMachine) mainComponent;
			List<Context> sees = machine.getSees();
			for (Context context : sees) {
				List<EventBConstant> constants = context.getConstants();
				for (EventBConstant c : constants) {
					EvaluationResult value = c.getValue(trace);
					if (value != null)
						map.put(c.getName(), value.getValue());
				}
			}
		}
		return map;
	}
	
	private static Map<String, Object> getBVariablesAsJson(Trace trace,
			EventBModel model) {
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
	
	public static Map<String, Object> getJsonDataForRendering(Trace currentTrace, String template) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String, Object> fMap = new HashMap<String, Object>();
		modelMap.put("model", fMap);
		AbstractModel model = currentTrace.getModel();

		if (model instanceof EventBModel) {

			EventBModel eventbModel = (EventBModel) model;
			fMap.put("name", ((EventBModel) model).getMainComponentName());

			// Add variables
			Map<String, Object> bVariablesAsJson = getBVariablesAsJson(
					currentTrace, eventbModel);
			fMap.put("variables", bVariablesAsJson);

			List<Map<String, String>> elements = new ArrayList<Map<String, String>>();
			for (Map.Entry<String, Object> entry : bVariablesAsJson.entrySet()) {
				elements.add(WebUtils.wrap("key",entry.getKey(),"value",entry.getValue()));
			}
			fMap.put("variablesAsList", elements);

			// Add constants
			Map<String, Object> bConstantsAsJson = getBConstantsAsJson(
					currentTrace, eventbModel);
			fMap.put("constants", bConstantsAsJson);

			elements = new ArrayList<Map<String, String>>();
			for (Map.Entry<String, Object> entry : bConstantsAsJson.entrySet()) {
				elements.add(WebUtils.wrap("key",entry.getKey(),"value",entry.getValue()));
			}
			fMap.put("constantsAsList", elements);
			
			// Add invariants
			Map<String, Object> bInvariantsAsJson = getBInvariantsAsJson(
					currentTrace, eventbModel);
			fMap.put("invariants", bInvariantsAsJson);
			
			elements = new ArrayList<Map<String, String>>();
			for (Map.Entry<String, Object> entry : bInvariantsAsJson.entrySet()) {
				elements.add(WebUtils.wrap("key",entry.getKey(),"value",entry.getValue()));
			}
			fMap.put("invariantsAsList", elements);
			
		}

		// Add trace
		List<Map<String, Object>> trace = new ArrayList<Map<String, Object>>();
		List<OpInfo> opList = currentTrace.getCurrent().getOpList();
		for (OpInfo op : opList) {
			Map<String, Object> opm = new HashMap<String, Object>();
			opm.put("name", op.getName());
			opm.put("parameter", op.getParams());
			opm.put("full", getOpString(op));
			trace.add(opm);
		}
		fMap.put("trace", trace);

		if (currentTrace.getHead().getOp() != null)
			fMap.put("lastOperation", getOpString(currentTrace.getHead().getOp()));

		modelMap.put("eval", new EvalExpression(currentTrace));
		
		// -------------------------------------------------------
		
		String jsonRendered = "{}";
		if (template != null) {
			String[] split = template.split("/");
			String filename = split[split.length - 1];
			String folderPath = template.replace(filename, "");
			File folder = new File(folderPath);
			for (File f : folder.listFiles()) {
				if (f.getName().endsWith(".json")) {
					String fjsonRendered = WebUtils.render(f.getPath(),
							modelMap);
					if (!fjsonRendered.isEmpty())
						jsonRendered = fjsonRendered;
				}
			}
		}
		
		modelMap.put("data", JSON.parse(jsonRendered));
		
		return modelMap;

	}
	
	private static class EvalExpression implements Function<String, Object> {

		private Trace currentTrace;

		public EvalExpression(Trace currentTrace) {
			this.currentTrace = currentTrace;
		}

		public Object apply(String input) {

			Object output = "???";
				
			try {

				EvaluationResult evaluationResult = null;
				IEvalElement evalElement = null;
				
				AbstractModel model = currentTrace.getModel();
				if (model instanceof EventBModel
						|| model instanceof ClassicalBModel) {
					evalElement = new ClassicalB(input);
					StateSpace stateSpace = currentTrace.getStateSpace();
					Map<IEvalElement, EvaluationResult> valuesAt = stateSpace
							.valuesAt(currentTrace.getCurrentState());
					evaluationResult = valuesAt.get(evalElement);
					if (evaluationResult == null)
						stateSpace.subscribe(this, evalElement);
					// TODO: unscribe!!!
				} else if (model instanceof CSPModel) {
					Object object = cspcache.get(input);
					if (object != null) {
						return object;
					} else {
						evalElement = new CSP(input,
								(CSPModel) currentTrace.getModel());
						evaluationResult = currentTrace
								.evalCurrent(evalElement);
					}
				}		
				
				if (evaluationResult != null) {
					if (evaluationResult.hasError()) {
						// output = "<span style='color:red;font-weight:bold;'>"
						// + evalResult.code + ": "
						// + evalResult.getErrors() + "</span>";
					} else {
						output = translateValue(evaluationResult.value);
						if (model instanceof CSPModel)
							cspcache.put(input, output);
					}
				}

			} catch (Exception e) {
				// output = "<span style='color:red;font-weight:bold;'>"
				// + e.getMessage() + "</span>";
			}
		
			return output;

		}

	}
	
	private static Object translateValue(String val) {
		Object fvalue = val;
		if (val.equalsIgnoreCase("TRUE")) {
			fvalue = true;
		} else if (val.equalsIgnoreCase("FALSE")) {
			fvalue = false;
		}
		return fvalue;
	}
	
	private static String getOpString(OpInfo op) {
		
		String opName = op.getName();
		String AsImplodedString = "";
		List<String> opParameter = op.getParams();
		if (opParameter.size() > 0) {
			String[] inputArray = opParameter
					.toArray(new String[opParameter.size()]);
			StringBuffer sb = new StringBuffer();
			sb.append(inputArray[0]);
			for (int i = 1; i < inputArray.length; i++) {
				sb.append(".");
				sb.append(inputArray[i]);
			}
			AsImplodedString = "." + sb.toString();
		}
		String opNameWithParameter = opName + AsImplodedString;
		return opNameWithParameter;
		
	}
	
}
