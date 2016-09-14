package de.prob.scripting;

import java.io.IOException;
import java.net.URL;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.prob.statespace.AnimationSelector;

@Singleton
public class ScriptEngineProvider implements Provider<ScriptEngine> {

	private final Api api;
	private final AnimationSelector animations;
	private final ScriptEngineManager manager;
	private final Logger logger = LoggerFactory.getLogger(ScriptEngineProvider.class);

	private static final String[] IMPORTS = new String[] {
			"import de.prob.statespace.*;",
			"import de.prob.model.representation.*;",
			"import de.prob.model.classicalb.*;",
			"import de.prob.model.eventb.*;",
			"import de.prob.animator.domainobjects.*;",
			"import de.prob.animator.command.*;",
			"import de.prob.visualization.*", "import de.prob.bmotion.*" };

	@Inject
	public ScriptEngineProvider(final Api api,
			final AnimationSelector animations) {
		this.api = api;
		this.animations = animations;
		manager = new ScriptEngineManager(this.getClass().getClassLoader());
	}

	@Override
	public ScriptEngine get() {
		ScriptEngine engine = manager.getEngineByName("Groovy");
		Bindings bindings = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
		bindings.put("api", api);
		bindings.put("animations", animations);
		bindings.put("engine", engine);
		URL url = Resources.getResource("initscript");
		String script;
		try {
			script = Resources.toString(url, Charsets.UTF_8);
			engine.eval(Joiner.on("\n").join(IMPORTS) + script);
		} catch (IOException e) {
			logger.error("Error evaluating initial script", e);

		} catch (ScriptException e) {
			logger.error("Error evaluating initial script", e);
		}
		return new GroovySE(engine);
	}
}
