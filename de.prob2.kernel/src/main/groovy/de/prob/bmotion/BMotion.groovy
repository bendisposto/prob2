package de.prob.bmotion

import com.google.common.base.Charsets
import com.google.common.io.Resources
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

    public Map<String, BMotionComponent> components = [:]

    private final Map<String, Trigger> observers = [:]

    private final Map<String, Closure> methods = [:]

    private TransformersObserver transformerObserver

    private final ScriptEngineProvider engineProvider

    public final static String TRIGGER_ANIMATION_CHANGED = "AnimationChanged"

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
    public void animationChange(final String trigger, final ITool tool) {
        observers.get(trigger)?.observers?.each { it.apply(this) }
    }

    // ---------- BMS API
    public void registerObserver(final BMotionObserver o, String trigger = TRIGGER_ANIMATION_CHANGED) {
        registerObserver([o], trigger)
    }

    public void registerObserver(final List<BMotionObserver> o, String trigger = TRIGGER_ANIMATION_CHANGED) {
        o.each {
            (it instanceof BMotionTransformer) ? transformerObserver.add(it) : observers.get(trigger)?.observers?.add(it)
        }
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

    public void apply(final BMotionObserver o) {
        o.apply(this)
    }

    public void apply(final List<BMotionObserver> o) {
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

    public void registerMethod(String name, Closure cls) {
        methods.put(name, cls)
    }

    public Object callGroovyMethod(final Map<String, String[]> params) {
        Closure cls = methods.get(params.get("gcmd")[0])
        if (cls != null) cls(params)
        null
    }

    // ------------------

    @Override
    public void initSession() {
//        String absoluteTemplatePath = BMotionUtil
//                .getFullTemplatePath(getTemplatePath())
        this.observers.clear()
        this.methods.clear()
        this.transformerObserver = new TransformersObserver()
        def Trigger trigger = new Trigger()
        trigger.observers.add(this.transformerObserver)
        this.observers.put(TRIGGER_ANIMATION_CHANGED, trigger)
        // Initialise ProBMotion components
        initProBMotionComponents()
//        // Register observer from json
//        if (getTool() instanceof IObserver) {
//            JsonElement jsonObserver = BMotionUtil.getJsonObserver(
//                    absoluteTemplatePath, getParameterMap().get("json"));
//            registerObserver(((IObserver) getTool())
//                    .getBMotionObserver(jsonObserver));
//        }
        // Initialise groovy scripting engine
        initGroovyScriptEngine();
        initialised = true;
    }

    private void initProBMotionComponents() {
        components.each {
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

    def class Trigger {

        public final List<BMotionObserver> observers = []

    }

}