package de.prob.bmotion

import com.google.common.base.Charsets
import com.google.common.io.Resources
import com.google.gson.JsonElement
import de.prob.scripting.ScriptEngineProvider
import de.prob.ui.api.ITool
import de.prob.ui.api.IToolListener
import de.prob.ui.api.ImpossibleStepException
import de.prob.ui.api.ToolRegistry
import de.prob.web.WebUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptException
import javax.servlet.AsyncContext

public class BMotion extends AbstractBMotionStudioSession
        implements IToolListener {

    Logger logger = LoggerFactory.getLogger(BMotion.class)

    private final Map<String, BMotionComponent> components = [:]

    private final ScriptEngineProvider engineProvider

    private final static String DEFAULT_COMPONENT = "DEFAULT_COMPONENT"

    private boolean initialised = false

    public BMotion(
            final UUID id,
            final ITool tool,
            final Map<String, BMotionComponent> components,
            final ToolRegistry registry, final String templatePath,
            final ScriptEngineProvider engineProvider, final String host, final int port) {
        super(id, tool, templatePath, host, port);
        this.engineProvider = engineProvider
        this.components = components
        this.components.put(DEFAULT_COMPONENT, new BMotionDefaultComponent())
        registry.registerListener(this)
        incrementalUpdate = true
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

    @Override
    public void animationChange(final ITool tool) {
        components.each { it.value.apply(this) }
    }

    // ---------- BMS API
    public void registerObserver(final IBMotionObserver o) {
        components.get(DEFAULT_COMPONENT).registerObserver(o)
    }

    public void registerObserver(final List<IBMotionObserver> o) {
        components.get(DEFAULT_COMPONENT).registerObserver(o)
    }

    /**
     *
     * This method applies a list of JavaScript snippets represented as Strings
     * on the visualisation.
     *
     * @param js A list of JavaScript snippets represented as Strings.
     */
    public void apply(final String js) {
        submit(WebUtils.wrap("cmd", "bms.applyJavaScript", "values", js));
    }

    /**
     *
     * This method calls a JavaScript method with the given json data.
     *
     * @param cmd The JavaScript method name to be called.
     * @param json The json data.
     */
    public void apply(final String cmd, final Map<Object, Object> json) {
        json.put("cmd", cmd);
        submit(json);
    }

    public void apply(final IBMotionObserver o) {
        o.apply(this)
    }

    public void apply(final List<IBMotionObserver> o) {
        o.each { it.apply(this) }
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
        // TODO: Decreases performance!!!/
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
                .getFullTemplatePath(getTemplatePath())
        // Initialise ProBMotion components
        initProBMotionComponents()
        // Register observer from json
        if (getTool() instanceof IObserver) {
            JsonElement jsonObserver = BMotionUtil.getJsonObserver(
                    absoluteTemplatePath, getParameterMap().get("json"));
            registerObserver(((IObserver) getTool())
                    .getBMotionObserver(jsonObserver));
        }
        // Initialise groovy scripting engine
        initGroovyScriptEngine();
        initialised = true;
    }

    private void initProBMotionComponents() {
        components.each {
            it.value.observers.clear()
            it.value.init(this)
        }
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