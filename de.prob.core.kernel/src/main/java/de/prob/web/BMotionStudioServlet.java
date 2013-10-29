package de.prob.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.OpInfo;
import de.prob.statespace.Trace;

@SuppressWarnings("serial")
@Singleton
public class BMotionStudioServlet extends HttpServlet {

	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB
	
	private AnimationSelector selector;
	
	@Inject
	public BMotionStudioServlet(AnimationSelector selector) {
		this.selector = selector;
	}
	
	private Map<String, Object> getBInvariantsAsJson(Trace trace,
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
	
	private Map<String, Object> getBConstantsAsJson(Trace trace,
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
	
	private Map<String, Object> getBVariablesAsJson(Trace trace,
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
	
	private Map<String, Object> getJsonDataForRendering(Trace currentTrace) {

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
			Map<String,Object> opm = new HashMap<String, Object>();
			opm.put("name", op.getName());
			opm.put("parameter", op.getParams());
			trace.add(opm);
			
			String opName = op.getName();
			String AsImplodedString = "";
			List<String> opParameter = op.getParams();
			if (opParameter.size() > 0) {
				String[] inputArray = opParameter.toArray(new String[opParameter
						.size()]);
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
		
		if(currentTrace.getHead().getOp() != null)
			fMap.put("lastOperation", currentTrace.getHead().getOp().getName());
		
		modelMap.put("eval", new EvalExpression(this.selector));
		
		return modelMap;

	}

//	private String readFile(String file) throws IOException {
//		BufferedReader reader = new BufferedReader(new FileReader(file));
//		String line = null;
//		StringBuilder stringBuilder = new StringBuilder();
//		String ls = System.getProperty("line.separator");
//		while ((line = reader.readLine()) != null) {
//			stringBuilder.append(line);
//			stringBuilder.append(ls);
//		}
//		reader.close();
//		return stringBuilder.toString();
//	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");

		String uri = req.getRequestURI();
		String furl = uri.replace("/bms/", "");
		String[] split = furl.split("/");
		String filename = split[split.length - 1];
		String filepath = furl;

		// Get json from file
		// Object jsonFromFile = null;
		// int i = filename.lastIndexOf('.');
		// if (i > 0) {
		// String extension = filename.substring(i + 1);
		// String jsonfilename = filename.replace(extension, "json");
		// File jsonfile = new File(home + jsonfilename);
		// if (jsonfile.exists())
		// jsonFromFile = JSON.parse(readFile(jsonfile.getPath()));
		// }
		
		// Set correct mimeType
		String mimeType = getServletContext().getMimeType(filepath);
		resp.setContentType(mimeType);
		
		// Prepare streams.
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		InputStream stream = new FileInputStream(filepath);
		
		// TODO: This is ugly ... we need a better method to check the file
		// type
		Trace currentTrace = selector.getCurrentTrace();
		if (filename.endsWith(".html") && currentTrace != null) {
			Map<String, Object> jsonDataForRendering = getJsonDataForRendering(currentTrace);
			String render = WebUtils.render(filepath, jsonDataForRendering);
			stream = new ByteArrayInputStream(render.getBytes());
		}
		
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
		} finally {
			// Gently close streams.
			// close(output);
			// close(input);
		}

	}

	// private void close(Closeable resource) {
	// if (resource != null) {
	// try {
	// resource.close();
	// } catch (IOException e) {
	// // Do your thing with the exception. Print it, log it or mail
	// // it.
	// e.printStackTrace();
	// }
	// }
	// }
	
	private class EvalExpression implements Function<String, Object> {

		private AnimationSelector selector;

		public EvalExpression(AnimationSelector selector) {
			this.selector = selector;
		}

		public Object apply(String input) {

			Trace currentTrace = this.selector.getCurrentTrace();

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
	
	private Object translateValue(String val) {
		Object fvalue = val;
		if (val.equalsIgnoreCase("TRUE")) {
			fvalue = true;
		} else if (val.equalsIgnoreCase("FALSE")) {
			fvalue = false;
		}
		return fvalue;
	}
	
}
