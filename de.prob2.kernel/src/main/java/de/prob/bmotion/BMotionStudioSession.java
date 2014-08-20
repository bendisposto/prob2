package de.prob.bmotion;

import groovy.lang.GroovyRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.script.ScriptException;
import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;

import de.prob.scripting.ScriptEngineProvider;
import de.prob.ui.api.ITool;
import de.prob.ui.api.IToolListener;
import de.prob.ui.api.ImpossibleStepException;
import de.prob.ui.api.ToolRegistry;
import de.prob.visualization.Transformer;
import de.prob.web.WebUtils;

public class BMotionStudioSession extends AbstractBMotionStudioSession
		implements IToolListener {

	Logger logger = LoggerFactory.getLogger(BMotionStudioSession.class);

	private final List<IBMotionGroovyObserver> groovyObserverListener = new ArrayList<IBMotionGroovyObserver>();

	private final ScriptEngineProvider engineProvider;
	
	private boolean initialised = false;
	
	public BMotionStudioSession(final UUID id, final ITool tool,
			final ToolRegistry registry, final String templatePath,
			final ScriptEngineProvider engineProvider, final String host,
			final int port) {
		super(id, tool, templatePath, host, port);
		this.engineProvider = engineProvider;
		registry.registerListener(this);
		this.incrementalUpdate = true;
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		if (lastinfo == -1) {
			responses.reset();
			sendInitMessage(context);
			initSession();			
		}
	}

	public void registerGroovyObserver(final IBMotionGroovyObserver script) {
		groovyObserverListener.add(script);
		script.update(getTool());
	}

	@Override
	public void animationChange(final ITool tool) {
		try {
			for (IBMotionGroovyObserver s : groovyObserverListener) {
				s.update(tool);
			}
		} catch (GroovyRuntimeException e) {
			logger.error("BMotion Studio (Groovy runtime exception): "
					+ e.getMessage());
		}
	}

	// ---------- BMS API
	public void apply(final String cmd, final Map<Object, Object> json) {
		json.put("cmd", cmd);
		submit(json);
	}

	/**
	 * 
	 * This method applies a list of JavaScript snippets represented as Strings
	 * on the visualisation.
	 * 
	 * @param values
	 *            A list of JavaScript snippets represented as Strings
	 */
	public void apply(final String js) {
		submit(WebUtils.wrap("cmd", "bms.applyJavaScript", "values", js));
	}

	public void apply(final Object transformer) {
		if (transformer instanceof Transformer) {
			ArrayList<Transformer> t = new ArrayList<Transformer>();
			t.add((Transformer) transformer);
			submit(WebUtils.wrap("cmd", "bms.applyTransformers",
					"transformers", t));
		} else {
			submit(WebUtils.wrap("cmd", "bms.applyTransformers",
					"transformers", transformer));
		}
	}
	
	@Deprecated
	public void toGui(final Object json) {
		submit(json);
	}
	
	@Deprecated
	public void callJs(final Object values) {
		submit(WebUtils.wrap("cmd", "bms.update_visualization", "values",
				values));
	}

	/**
	 * 
	 * This method evaluates a given formula and returns the corresponding
	 * result.
	 * 
	 * @param formula
	 *            The formula to evaluate
	 * @return the result of the formula or null if no result was found or no
	 *         reference model and no trace exists
	 * @throws Exception
	 */
	public Object eval(final String formula) throws Exception {
		// TODO: Decreases performance!!!
		// if (getTool().getErrors(getTool().getCurrentState(),
		// formula).isEmpty()) {
		// try {
		Object evaluate = getTool().evaluate(getTool().getCurrentState(), formula);
		return evaluate;
		// } catch (IllegalFormulaException e) {
		// TODO: handle exception
		// }
		// }
		// return null;
	}

	public Object executeOperation(final Map<String, String[]> params) {
		String id = params.get("id").length > 0 ? params.get("id")[0] : "";
		String op = params.get("op")[0];
		String[] parameters = params.get("predicate");
		try {
			getTool().doStep(id, op, parameters);
		} catch (ImpossibleStepException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// ------------------

	@Override
	public void initSession() {
		String absoluteTemplatePath = BMotionUtil
				.getFullTemplatePath(getTemplatePath());
		groovyObserverListener.clear();
		if (getTool() instanceof IObserver) {
			JsonElement jsonObserver = BMotionUtil.getJsonObserver(
					absoluteTemplatePath, getParameterMap().get("json"));
			registerGroovyObserver(((IObserver) getTool())
					.getBMotionGroovyObserver(this, jsonObserver));
		}
		try {
			BMotionUtil.evaluateGroovy(engineProvider.get(),
					absoluteTemplatePath, getParameterMap(), this);
		} catch (GroovyRuntimeException e) {
			logger.error("BMotion Studio (Groovy runtime exception): "
					+ e.getMessage());
		} catch (ScriptException e) {
			logger.error("BMotion Studio (Groovy script exception): "
					+ e.getMessage() + "(line " + e.getLineNumber() + ")");
		}
		initialised = true;
	}

	public boolean isInitialised() {
		return initialised;
	}
	
}