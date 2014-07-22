package de.prob.bmotion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;

import de.prob.model.representation.AbstractModel;
import de.prob.scripting.ScriptEngineProvider;
import de.prob.ui.api.ITool;
import de.prob.ui.api.IToolListener;
import de.prob.ui.api.ImpossibleStepException;
import de.prob.ui.api.ToolRegistry;
import de.prob.web.WebUtils;

public class BMotionStudioSession extends AbstractBMotionStudioSession
		implements IToolListener {

	Logger logger = LoggerFactory.getLogger(BMotionStudioSession.class);

	private final List<IBMotionGroovyObserver> groovyObserverListener = new ArrayList<IBMotionGroovyObserver>();

	private final ITool tool;
	
	private final ScriptEngineProvider engineProvider;
	
	public BMotionStudioSession(final ITool tool, final ToolRegistry registry,
			final String templatePath, final AbstractModel model,
			final ScriptEngineProvider engineProvider, final String host,
			final int port) {
		super(templatePath, model, host, port);
		this.tool = tool;
		this.engineProvider = engineProvider;
		incrementalUpdate = false;
		registry.registerListener(this);
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		sendInitMessage(context);
		animationChange(tool);
	}

	public void registerGroovyObserver(final IBMotionGroovyObserver script) {
		groovyObserverListener.add(script);
		animationChange(tool);
	}

	@Override
	public void animationChange(final ITool tool) {
		for (IBMotionGroovyObserver s : groovyObserverListener) {
			s.update(tool);
		}
	}

	// ---------- BMS API
	public void toGui(final Object json) {
		submit(json);
	}

	public void toGui(final String cmd, final Map<Object, Object> json) {
		json.put("cmd", cmd);
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
	 * result.
	 * 
	 * @param formula
	 *            The formula to evaluate
	 * @return the result of the formula or null if no result was found or no
	 *         reference model and no trace exists
	 * @throws Exception
	 */
	public Object eval(final String formula) throws Exception {
		return tool.evaluate(tool.getCurrentState(), formula);
	}

	public Object executeOperation(final Map<String, String[]> params) {
		String id = params.get("id").length > 0 ? params.get("id")[0] : "";
		String op = params.get("op")[0];
		String[] parameters = params.get("predicate");
		try {
			tool.doStep(id, op, parameters);
		} catch (ImpossibleStepException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void initSession() {
		String absoluteTemplatePath = BMotionUtil
				.getFullTemplatePath(getTemplatePath());
		if (tool instanceof IObserver) {
			JsonElement jsonObserver = BMotionUtil.getJsonObserver(
					absoluteTemplatePath, getParameterMap().get("json"));
			registerGroovyObserver(((IObserver) tool).getBMotionGroovyObserver(
					this, jsonObserver));
		}
		BMotionUtil.evaluateGroovy(engineProvider.get(), absoluteTemplatePath,
				getParameterMap(), this);
	}

	// ------------------

}