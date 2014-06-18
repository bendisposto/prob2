package de.prob.bmotion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ui.api.ITool;
import de.prob.ui.api.IToolListener;
import de.prob.ui.api.ImpossibleStepException;
import de.prob.ui.api.ToolRegistry;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

public class BMotionStudioSession extends AbstractSession implements
		IToolListener {

	Logger logger = LoggerFactory.getLogger(BMotionStudioSession.class);

	private final List<IBMotionScript> scriptListeners = new ArrayList<IBMotionScript>();

	private final ITool tool;

	private final String templatePath;

	private final String host;

	private final int port;

	public BMotionStudioSession(final ITool tool, final ToolRegistry registry,
			final String templatePath, final String host, final int port) {
		this.tool = tool;
		this.templatePath = templatePath;
		this.host = host;
		this.port = port;
		incrementalUpdate = false;
		registry.registerListener(this);
	}

	public String getTemplatePath() {
		return templatePath;
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return null;
	}

	public Object triggerListener(final Map<String, String[]> params) {
		// Trigger all registered script listeners with collected formulas
		for (IBMotionScript s : scriptListeners) {
			s.update(tool);
		}
		return null;
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		sendInitMessage(context);
		animationChange(tool);

	}

	public void registerScript(final IBMotionScript script) {
		scriptListeners.add(script);
		animationChange(tool);
	}

	@Override
	public void animationChange(final ITool tool) {
		for (IBMotionScript s : scriptListeners) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	// ------------------

}