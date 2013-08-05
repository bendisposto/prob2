package de.prob.worksheet;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.prob.scripting.Api;
import de.prob.statespace.AnimationSelector;

@Singleton
public class ScriptEngineProvider implements Provider<ScriptEngine> {

	private final Api api;
	private final AnimationSelector animations;
	private final ScriptEngineManager manager;

	@Inject
	public ScriptEngineProvider(Api api, AnimationSelector animations) {
		this.api = api;
		this.animations = animations;
		this.manager = new ScriptEngineManager(this.getClass().getClassLoader());
	}

	@Override
	public ScriptEngine get() {
		ScriptEngine engine = manager.getEngineByName("Groovy");
		Bindings bindings = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
		bindings.put("api", api);
		bindings.put("animations", animations);
		return new GroovySE(engine);
	}

}
