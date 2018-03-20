package de.prob.scripting;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import com.google.common.io.CharStreams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroovySE implements ScriptEngine {

	private final ScriptEngine groovy;
	private final Logger logger = LoggerFactory.getLogger(GroovySE.class);

	private static final String IMPORTS =
		"import de.prob.animator.command.*;"
		+ "import de.prob.animator.domainobjects.*;"
		+ "import de.prob.bmotion.*;"
		+ "import de.prob.check.*;"
		+ "import de.prob.model.classicalb.*;"
		+ "import de.prob.model.eventb.*;"
		+ "import de.prob.model.representation.*;"
		+ "import de.prob.statespace.*;"
		+ "import de.prob.visualization.*;"
		+ '\n'
	;

	public GroovySE(final ScriptEngine engine) {
		groovy = engine;

		final String initscript;
		try (
			final InputStream is = this.getClass().getResourceAsStream("/initscript");
			final Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
		) {
			initscript = CharStreams.toString(reader);
		} catch (IOException e) {
			logger.error("Could not read initscript", e);
			return;
		}
		try {
			groovy.eval(IMPORTS + initscript);
		} catch (ScriptException e) {
			logger.error("initscript threw an exception", e);
		}
	}

	@Override
	public Object eval(final String script, final ScriptContext context)
			throws ScriptException {
		StringBuffer buff = new StringBuffer();
		if (groovy.get("__console") == null) {
			groovy.put("__console", buff);
		}
		Object result = groovy.eval(IMPORTS + script, context);
		if (result == null) {
			return "null";
		}
		if (buff.length() > 0) {
			logger.error("Automatically captured prints from groovy. "
					+ "Users of a groovy engine should provide a console. "
					+ "Output was: {}", buff.toString());
		}
		return result;
	}

	public Object eval(final String script, final ScriptContext context,
			final StringBuffer console) throws ScriptException {
		groovy.put("__console", console);
		Object result = eval(script, context);
		return new Object[] { result, console.toString() };
	}

	public Object eval(final String script, final StringBuffer console)
			throws ScriptException {
		groovy.put("__console", console);
		Object result = eval(script, getContext());
		return new Object[] { result, console.toString() };
	}

	@Override
	public Bindings createBindings() {
		return groovy.createBindings();
	}

	@Override
	public ScriptEngineFactory getFactory() {
		return groovy.getFactory();
	}

	@Override
	public Bindings getBindings(final int scope) {
		return groovy.getBindings(scope);
	}

	@Override
	public void setBindings(final Bindings bindings, final int scope) {
		groovy.setBindings(bindings, scope);
	}

	@Override
	public void setContext(final ScriptContext context) {
		groovy.setContext(context);
	}

	@Override
	public ScriptContext getContext() {
		return groovy.getContext();
	}

	@Override
	public Object get(final String key) {
		return groovy.get(key);
	}

	@Override
	public void put(final String key, final Object value) {
		groovy.put(key, value);
	}

	@Override
	public Object eval(final Reader reader, final ScriptContext context)
			throws ScriptException {
		try {
			return eval(CharStreams.toString(reader), context);
		} catch (IOException e) {
			throw new ScriptException(e);
		}
	}

	@Override
	public Object eval(final String script) throws ScriptException {
		return eval(script, getContext());
	}

	@Override
	public Object eval(final Reader reader) throws ScriptException {
		try {
			return eval(CharStreams.toString(reader));
		} catch (IOException e) {
			throw new ScriptException(e);
		}
	}

	@Override
	public Object eval(final String script, final Bindings n) throws ScriptException {
		return eval(script, getScriptContext(n));
	}

	@Override
	public Object eval(final Reader reader, final Bindings n) throws ScriptException {
		try {
			return eval(CharStreams.toString(reader), n);
		} catch (IOException e) {
			throw new ScriptException(e);
		}
	}

	protected ScriptContext getScriptContext(final Bindings nn) {

		SimpleScriptContext ctxt = new SimpleScriptContext();
		Bindings gs = getBindings(ScriptContext.GLOBAL_SCOPE);

		if (gs != null) {
			ctxt.setBindings(gs, ScriptContext.GLOBAL_SCOPE);
		}

		Objects.requireNonNull(nn, "Engine scope Bindings may not be null.");
		ctxt.setBindings(nn, ScriptContext.ENGINE_SCOPE);

		return ctxt;

	}

}
