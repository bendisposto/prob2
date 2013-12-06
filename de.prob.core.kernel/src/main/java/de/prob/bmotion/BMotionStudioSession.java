package de.prob.bmotion;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.CSPModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.visualization.AnimationNotLoadedException;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class BMotionStudioSession extends AbstractSession implements
		IAnimationChangeListener {

	Logger logger = LoggerFactory.getLogger(BMotionStudioSession.class);

	private Trace currentTrace;

	private final AnimationSelector selector;

	private String template;
	
	private Map<String, String> evalMap = new HashMap<String, String>();
	
	private Map<String, String> cachedCSPString = new HashMap<String, String>();
	
	@Inject
	public BMotionStudioSession(final AnimationSelector selector) {
		this.selector = selector;
		incrementalUpdate = false;
		currentTrace = selector.getCurrentTrace();
		if (currentTrace == null) {
			throw new AnimationNotLoadedException(
					"Please load model before opening a BMotion Studio visualization and than reload page.");
		} else {
			selector.registerAnimationChangeListener(this);
		}
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return null;
	}

	public Object eval(final Map<String, String[]> params) {

		String formula = params.get("formula")[0];
		String callback = params.get("callback")[0];

		String data = null;
		String[] dataPara = params.get("data");
		if (dataPara != null)
			data = dataPara[0];

		Object parse = JSON.parse(formula);

		Map<String, String> wrap = WebUtils.wrap("cmd", callback);

		if (parse instanceof Object[]) {
			Map<String, Object> tmp = new HashMap<String, Object>();
			Object[] oa = (Object[]) parse;
			for (Object o : oa) {
				String f = o.toString();
				Object value = translateValue(getEvalValue(currentTrace, f));
				tmp.put(f, value);
			}
			wrap.put("result", WebUtils.toJson(tmp));
		} else {
			Object value = translateValue(getEvalValue(currentTrace,
					parse.toString()));
			wrap.put("result", value.toString());

		}

		if (data != null)
			wrap.put("data", data);
		submit(wrap);
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
		super.reload(client, lastinfo, context);
		
		String jsonDataFromFile = WebUtils.toJson(getJsoFromFileForRendering(
				currentTrace, template));

		Map<String, Object> jsonDataForRenderingMap = BMotionStudioUtil
				.getJsonDataForRendering(currentTrace);
		jsonDataForRenderingMap.put("eval", evalMap);

		String jsonDataForRendering = WebUtils.toJson(jsonDataForRenderingMap);

		submit(WebUtils.wrap("cmd", "bms.reloadTemplate", "observer",
				jsonDataFromFile, "data", jsonDataForRendering));
		
	}

	@Override
	public void traceChange(final Trace trace) {

		this.currentTrace = trace;
		
		String jsonDataFromFile = WebUtils.toJson(getJsoFromFileForRendering(
				currentTrace, template));

		Map<String, Object> jsonDataForRenderingMap = BMotionStudioUtil
				.getJsonDataForRendering(currentTrace);
		jsonDataForRenderingMap.put("eval", evalMap);

		String jsonDataForRendering = WebUtils.toJson(jsonDataForRenderingMap);

		submit(WebUtils.wrap("cmd", "bms.renderVisualization", "observer",
				jsonDataFromFile, "data", jsonDataForRendering));

	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	public String getTemplate() {
		return this.template;
	}

	public Map<String, Object> getJsoFromFileForRendering(
			final Trace currentTrace, final String template) {

		HashMap<String, Object> m = new HashMap<String, Object>();

		Map<String, Object> jsonDataForRendering = BMotionStudioUtil
				.getJsonDataForRendering(currentTrace);

		Map<String, Object> scope = new HashMap<String, Object>();
		scope.put("eval", new EvalExpression(currentTrace));
		scope.putAll(jsonDataForRendering);

		String jsonRendered = "{}";
		if (template != null) {

			String[] split = template.split("/");
			String filename = split[split.length - 1];
			String folderPath = template.replace(filename, "");
			File folder = new File(folderPath);
			if (folder.exists()) {
				for (File f : folder.listFiles()) {
					if (f.getName().endsWith(".json")) {
						WebUtils.render(f.getPath(), scope);
						jsonRendered = readFile(f.getPath());
					}
				}
			}
		}

		m.put("wrapper", JSON.parse(jsonRendered));

		return m;

	}
	
	public String readFile(String filename)
	{
	   String content = null;
	   File file = new File(filename); //for ex foo.txt
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

		private final Trace trace;

		public EvalExpression(final Trace trace) {
			this.trace = trace;
		}

		@Override
		public Object apply(final String input) {
			String finput = input.replace("\\\\", "\\");
			Object output = translateValue(getEvalValue(trace, finput));
			evalMap.put(finput, output.toString());
			return output;
		}

	}
	
	private String getEvalValue(Trace trace, String formula) {

		String output = "???";

		try {

			IEvalResult evaluationResult = null;
			IEvalElement evalElement = null;

			AbstractModel model = trace.getModel();

			if (model instanceof EventBModel
					|| model instanceof ClassicalBModel) {

				evalElement = new ClassicalB(formula);
				
				StateSpace stateSpace = trace.getStateSpace();
				Map<IEvalElement, IEvalResult> valuesAt = stateSpace
						.valuesAt(trace.getCurrentState());
				evaluationResult = valuesAt.get(evalElement);
				if (evaluationResult == null) {
					evaluationResult = trace.evalCurrent(evalElement);
					stateSpace.subscribe(this, evalElement);
					// TODO: unscribe!!!
				}

			} else if (model instanceof CSPModel) {
				output = cachedCSPString.get(formula);
				if (output == null) {
					evalElement = new CSP(formula, (CSPModel) trace.getModel());
					evaluationResult = trace.evalCurrent(evalElement);
				}
			}

			if (evaluationResult != null) {
				if(evaluationResult instanceof ComputationNotCompletedResult) {
					// TODO: do something .....
				} else if(evaluationResult instanceof EvalResult){
					output = ((EvalResult) evaluationResult).getValue();
					if(model instanceof CSPModel)
						cachedCSPString.put(formula, output);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return output;

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

}
