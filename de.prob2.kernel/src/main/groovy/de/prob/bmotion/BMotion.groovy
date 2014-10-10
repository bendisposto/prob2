package de.prob.bmotion;


import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptException
import javax.servlet.AsyncContext

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.Elements
import org.jsoup.select.NodeVisitor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.google.common.base.Charsets
import com.google.common.io.Resources
import com.google.gson.Gson
import com.google.gson.JsonElement

import de.prob.scripting.ScriptEngineProvider
import de.prob.ui.api.ITool
import de.prob.ui.api.IToolListener
import de.prob.ui.api.ImpossibleStepException
import de.prob.ui.api.ToolRegistry
import de.prob.web.WebUtils

public class BMotion extends AbstractBMotionStudioSession
implements IToolListener {

	Logger logger = LoggerFactory.getLogger(BMotion.class);

	private final List<BMotionObserver> observers = []

	private final ScriptEngineProvider engineProvider;

	private Document template
	private Elements body

	private boolean initialised = false;

	private final Gson g = new Gson();
	
	private def selectorCache = [:]

	public BMotion(final UUID id, final ITool tool,
	final ToolRegistry registry, final String templatePath,
	final ScriptEngineProvider engineProvider, final String host,
	final int port) {
		super(id, tool, templatePath, host, port);
		this.engineProvider = engineProvider;
		registry.registerListener(this);
		incrementalUpdate = true;
	}

	@Override
	public void reload(final String client, final int lastinfo,
			final AsyncContext context) {
		if (lastinfo == -1) {
			responses.reset();
			sendInitMessage(context);
			initSession();
		} else if (lastinfo >= 0) {
			resend(client, lastinfo, context);
		}
	}

	private List<String> getCachedBmsId(String selector) {
		def t = selector.isEmpty() ? []: selectorCache.get(selector)
		if(t == null) {
			t = body.select(selector).collect { it.attr("data-bmsid") }
			selectorCache.put(selector,t)
		}
		t
	}

	private void applyTransformers(List<Transform> transformers) {
		def applymap = [:]
		transformers.each { t ->
			getCachedBmsId(t.selector).each {
				def reactTransformer = applymap.get(it) ?: new ReactTransformer(it)
				applymap.put(it, reactTransformer)
				def aa = t.attributes.collectEntries { kv -> 
					(kv.value instanceof Closure)? [kv.key, kv.value()] : [kv.key, kv.value]}
				def ss = t.styles.collectEntries { kv ->
					(kv.value instanceof Closure)? [kv.key, kv.value()] : [kv.key, kv.value]}			
				reactTransformer.attributes.putAll(aa)
				reactTransformer.styles.putAll(ss)
				reactTransformer.content = (t.content instanceof Closure)? t.content() : t.content
			}
		}
		String json = g.toJson(applymap);
		submit(WebUtils.wrap("cmd", "bms.setObservers", "observers", json));
	}

	private List<Transform> getObserverTransformers(List<BMotionObserver> observers) {
		observers.collectMany { it.update(this) }
	}
	
	@Override
	public void animationChange(final ITool tool) {
		applyTransformers(getObserverTransformers(observers))
	}

	// ---------- BMS API
	public void registerObserver(BMotionObserver o) {
		observers.add(o);
		applyTransformers(getObserverTransformers(observers))
	}

	public void registerObserver(List<BMotionObserver> o) {
		observers.addAll(o);
		applyTransformers(getObserverTransformers(observers))
	}

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
	
	public void apply(final BMotionObserver o) {
		def t = getObserverTransformers(observers);
		t += o.update(this)
		applyTransformers(t);
	}

	public void apply(final List<BMotionObserver> o) {
		def t = getObserverTransformers(observers)
		t += getObserverTransformers(o)
		applyTransformers(t)
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
		Object evaluate = getTool().evaluate(getTool().getCurrentState(),
				formula);
		return evaluate;
		// } catch (IllegalFormulaException e) {
		// TODO: handle exception
		// }
		// }
		// return null;
	}

	public Object executeOperation(final Map<String, String[]> params) {
		String id = (params.get("id") != null && params.get("id").length > 0) ? params
				.get("id")[0] : "";
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
		String html = BMotionUtil.getFileContents(absoluteTemplatePath);
		template = Jsoup.parse(html);
		BMotionUtil.fixSvgImageTags(template);
		body = template.getElementsByTag("body");
		body.traverse(new NodeVisitor() {
					int counter = 1;

					public void head(Node node, int depth) {
						node.attr("data-bmsid", "bms"+String.valueOf(counter));
						counter++;
					}

					public void tail(Node node, int depth) {
					}
				});
		submit(WebUtils.wrap("cmd", "bms.setHtml", "html", body.html()));
		observers.clear();
		if (getTool() instanceof IObserver) {
			JsonElement jsonObserver = BMotionUtil.getJsonObserver(
					absoluteTemplatePath, getParameterMap().get("json"));
			registerObserver(((IObserver) getTool())
					.getBMotionObserver(jsonObserver));
		}
		initGroovyScriptEngine();
		initialised = true;
	}

	public void initGroovyScriptEngine() {

		try {
			String absoluteTemplatePath = BMotionUtil
					.getFullTemplatePath(getTemplatePath());
			ScriptEngine groovyEngine = engineProvider.get();
			Map<String, String> parameters = getParameterMap();
			String scriptPaths = parameters.get("script");
			if (absoluteTemplatePath != null && scriptPaths != null) {
				URL url = Resources.getResource("bmsscript");
				String bmsscript = Resources.toString(url, Charsets.UTF_8);
				groovyEngine.eval("import de.prob.bmotion.*;\n" + bmsscript);
				// Run custom groovy scripts
				String templateFolder = BMotionUtil
						.getTemplateFolder(absoluteTemplatePath);
				Bindings bindings = groovyEngine
						.getBindings(ScriptContext.GLOBAL_SCOPE);
				bindings.putAll(parameters);
				bindings.put("bms", this);
				bindings.put("templateFolder", templateFolder);
				String[] paths = scriptPaths.split(",");
				for (String path : paths) {
					groovyEngine.eval(
							"import de.prob.bmotion.*;\n"
							+ BMotionUtil
							.getFileContents(templateFolder
							+ File.separator + path),
							bindings);
				}
			}
		} catch (GroovyRuntimeException e) {
			logger.error("BMotion Studio (Groovy runtime exception): "
					+ e.getMessage());
		} catch (ScriptException e) {
			logger.error("BMotion Studio (Groovy script exception): "
					+ e.getMessage() + " (line " + e.getLineNumber() + ")");
		} catch (IOException e) {
			logger.error("BMotion Studio (Error reading script): "
					+ e.getMessage());
		}

	}

	public boolean isInitialised() {
		return initialised;
	}

}