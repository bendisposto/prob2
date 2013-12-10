package de.prob.scripting;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.CharStreams;

public class GroovySE implements ScriptEngine {

	private final ScriptEngine groovy;
	private final Logger logger = LoggerFactory.getLogger(GroovySE.class);

	private static volatile int varcount = 0;
	private static final int BUFFER_SIZE = 1024;

	public static int nextVar() {
		return varcount++;
	}

	private static final String[] IMPORTS = new String[] {
			"import de.prob.statespace.*;",
			"import de.prob.model.representation.*;",
			"import de.prob.model.classicalb.*;",
			"import de.prob.model.eventb.*;",
			"import de.prob.animator.domainobjects.*;",
			"import de.prob.animator.commands.*;",
			"import de.prob.visualization.*;", "\n " };

	private final String imports = Joiner.on("\n").join(IMPORTS);

	public GroovySE(final ScriptEngine engine) {
		groovy = engine;
		initialize();
	}

	private void initialize() {
		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("initscript");

		String initscript = "";
		try {
			initscript = CharStreams.toString(new InputStreamReader(
					inputStream, Charsets.UTF_8));
		} catch (IOException e) {
			logger.error("Error reading from initscript.");
		}
		try {
			groovy.eval(imports + "\n" + initscript);
		} catch (ScriptException e) {
			logger.error("Error initializing groovy", e);
			e.printStackTrace();
		} // run init script
	}

	@Override
	public Object eval(final String script, final ScriptContext context)
			throws ScriptException {
		StringBuffer buff = new StringBuffer();
		if (groovy.get("__console") == null) {
			groovy.put("__console", buff);
		}
		Object result = groovy.eval(imports + "\n" + script, context);
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

	/**
	 * Delegate to eval(String, ScriptContext)
	 */
	@Override
	public Object eval(final Reader reader, final ScriptContext context)
			throws ScriptException {
		return eval(readFully(reader), context);
	}

	private String readFully(final Reader reader) throws ScriptException {
		char[] arr = new char[8 * BUFFER_SIZE]; // 8K at a time
		StringBuilder buf = new StringBuilder();
		int numChars;
		try {
			while ((numChars = reader.read(arr, 0, arr.length)) > 0) {
				buf.append(arr, 0, numChars);
			}
		} catch (IOException exp) {
			throw new ScriptException(exp);
		}
		return buf.toString();
	}

	@Override
	public Object eval(final String script) throws ScriptException {
		return eval(script, getContext());
	}

	@Override
	public Object eval(final Reader reader) throws ScriptException {
		return eval(readFully(reader));
	}

	@Override
	public Object eval(final String script, final Bindings n)
			throws ScriptException {
		ScriptContext ctxt = getScriptContext(n);
		return eval(script, ctxt);
	}

	@Override
	public Object eval(final Reader reader, final Bindings n)
			throws ScriptException {
		return eval(readFully(reader), n);
	}

	protected ScriptContext getScriptContext(final Bindings nn) {

		SimpleScriptContext ctxt = new SimpleScriptContext();
		Bindings gs = getBindings(ScriptContext.GLOBAL_SCOPE);

		if (gs != null) {
			ctxt.setBindings(gs, ScriptContext.GLOBAL_SCOPE);
		}

		if (nn != null) {
			ctxt.setBindings(nn, ScriptContext.ENGINE_SCOPE);
		} else {
			throw new NullPointerException(
					"Engine scope Bindings may not be null.");
		}

		return ctxt;

	}

}
