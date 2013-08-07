package de.prob.worksheet;

import java.io.IOException;
import java.net.URL;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.prob.scripting.Api;
import de.prob.scripting.Downloader;
import de.prob.statespace.AnimationSelector;

@Singleton
public class ScriptEngineProvider implements Provider<ScriptEngine> {

	private final Api api;
	private final AnimationSelector animations;
	private final ScriptEngineManager manager;
	private final Downloader downloader;

	private static final String[] IMPORTS = new String[] {
			"import de.prob.statespace.*;",
			"import de.prob.model.representation.*;",
			"import de.prob.model.classicalb.*;",
			"import de.prob.model.eventb.*;",
			"import de.prob.animator.domainobjects.*;",
			"import de.prob.animator.command.*;",
			"import de.prob.visualization.*" };

	@Inject
	public ScriptEngineProvider(Api api, AnimationSelector animations,
			Downloader downloader) {
		this.api = api;
		this.animations = animations;
		this.downloader = downloader;
		this.manager = new ScriptEngineManager(this.getClass().getClassLoader());
	}

	@Override
	public ScriptEngine get() {
		ScriptEngine engine = manager.getEngineByName("Groovy");
		Bindings bindings = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
		bindings.put("api", api);
		bindings.put("animations", animations);
		bindings.put("downloader", downloader);
		URL url = Resources.getResource("initscript");
		String script;
		try {
			script = Resources.toString(url, Charsets.UTF_8);
			engine.eval(Joiner.on("\n").join(IMPORTS) + script);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return new GroovySE(engine);
	}
}
