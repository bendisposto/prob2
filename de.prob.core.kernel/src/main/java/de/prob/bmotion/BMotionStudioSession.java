package de.prob.bmotion;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.scripting.ScriptEngineProvider;
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

	private final AnimationSelector selector;

	private String template;

	private ScriptEngine groovy;

	@Inject
	public BMotionStudioSession(final AnimationSelector selector,
			ScriptEngineProvider sep) {
		this.selector = selector;
		this.groovy = sep.get();

		StringBuffer sb = new StringBuffer();

		Bindings bindings = groovy.getBindings(ScriptContext.GLOBAL_SCOPE);

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

	public Object evalGroovy(final Map<String, String[]> params) {
		String code = params.get("code")[0];
		String callback = params.get("callback")[0];
		String errorCallback = params.get("error_callback")[0];
		try {
			Object eval = groovy.eval(code);
			return WebUtils.wrap("cmd", "bms.callback", "fkt", callback,
					"value", eval.toString());
		} catch (ScriptException e) {
			return WebUtils.wrap("cmd", "bms.callback", "fkt", errorCallback,
					"value", e.toString());
		}
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
		String jsonDataFromFile = WebUtils.toJson(BMotionStudioUtil
				.getJsoFromFileForRendering(currentTrace, template));
		String jsonDataForRendering = WebUtils.toJson(BMotionStudioUtil
				.getJsonDataForRendering(currentTrace));
		submit(WebUtils.wrap("cmd", "bms.reloadTemplate", "observer",
				jsonDataFromFile, "data", jsonDataForRendering));
	}

	@Override
	public void traceChange(final Trace trace) {
		this.currentTrace = trace;
		String jsonDataFromFile = WebUtils.toJson(BMotionStudioUtil
				.getJsoFromFileForRendering(currentTrace, template));
		String jsonDataForRendering = WebUtils.toJson(BMotionStudioUtil
				.getJsonDataForRendering(currentTrace));
		submit(WebUtils.wrap("cmd", "bms.renderVisualization", "observer",
				jsonDataFromFile, "data", jsonDataForRendering));
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getTemplate() {
		return this.template;
	}

}
