package de.prob.bmotion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;

import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.EvaluationResult;
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
import de.prob.statespace.Trace;
import de.prob.web.WebUtils;

public class BMotionStudioUtil {

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
	
	public static String getJsonDataForLanguage(Trace trace, String template) {
		String jsonRendered = "{\"observer\":[],\"events\":[]}";
		if (template != null) {
			String[] split = template.split("/");
			String filename = split[split.length - 1];
			int i = filename.lastIndexOf('.');
			if (i > 0) {
				String extension = filename.substring(i + 1);
				String jsonfilename = filename.replace(extension, "json");
				String jsonfilepath = template.replace(filename, jsonfilename);
				File jsonfile = new File(jsonfilepath);
				if (jsonfile.exists()) {
					String fjsonRendered = WebUtils.render(jsonfilepath,
							BMotionStudioUtil.getJsonDataForRendering(trace));
					if (!fjsonRendered.isEmpty())
						jsonRendered = fjsonRendered;
				}
			}
		}
		return jsonRendered;
	}
	
	public static Map<String, Object> getJsonDataForRendering(Trace currentTrace) {

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
			fMap.put("var", bVariablesAsJson);

			// Add constants
			Map<String, Object> bConstantsAsJson = getBConstantsAsJson(
					currentTrace, eventbModel);
			fMap.put("con", bConstantsAsJson);

			// Add invariants
			Map<String, Object> bInvariantsAsJson = getBInvariantsAsJson(
					currentTrace, eventbModel);
			fMap.put("inv", bInvariantsAsJson);

		}

		// Add trace
		List<Map<String, Object>> trace = new ArrayList<Map<String, Object>>();
		List<OpInfo> opList = currentTrace.getHead().getOpList();
		List<String> opStrList = new ArrayList<String>();
		for (OpInfo op : opList) {
			Map<String, Object> opm = new HashMap<String, Object>();
			opm.put("name", op.getName());
			opm.put("parameter", op.getParams());
			trace.add(opm);

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
			opStrList.add(opNameWithParameter);
		}
		fMap.put("trace", trace);
		fMap.put("traceAsList", opStrList);

		if (currentTrace.getHead().getOp() != null)
			fMap.put("lastOperation", currentTrace.getHead().getOp().getName());

		modelMap.put("eval", new EvalExpression(currentTrace));

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

				Object evalElement = null;
				AbstractModel model = currentTrace.getModel();

				if (model instanceof EventBModel
						|| model instanceof ClassicalBModel) {
					evalElement = new ClassicalB(input);
				} else if (model instanceof CSPModel) {
					evalElement = new CSP(input,
							(CSPModel) currentTrace.getModel());
				}
				
				EvaluationResult evalResult = currentTrace
						.evalCurrent(evalElement);

				if (evalResult != null) {
					if (evalResult.hasError()) {
						output = "<span style='color:red;font-weight:bold;'>"
								+ evalResult.code + ": "
								+ evalResult.getErrors() + "</span>";
					} else {
						output = translateValue(evalResult.value);
					}
				}

			} catch (Exception e) {
				output = "<span style='color:red;font-weight:bold;'>"
						+ e.getMessage() + "</span>";
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
	
}
