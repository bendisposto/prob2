package de.prob.webconsole;

import groovy.lang.Binding;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.tools.shell.Interpreter;
import org.codehaus.groovy.tools.shell.ParseCode;
import org.codehaus.groovy.tools.shell.Parser;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.scripting.Api;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IStatesCalculatedListener;
import de.prob.statespace.OpInfo;
import de.prob.testing.TestRegistry;

/**
 * This servlet takes a line from the web interface and evaluates it using
 * Groovy. The Groovy interpreter does not remember import statements, i.e., the
 * input 'import foo.Bar; x = new Bar' will work, but spliting it into two
 * separate lines won't. We thus collect any import statement and prefix every
 * command with all the imports.
 * 
 * @author bendisposto
 * 
 */
@Singleton
public class GroovyExecution implements IStatesCalculatedListener {

	private final ArrayList<String> inputs = new ArrayList<String>();
	private final ArrayList<String> imports = new ArrayList<String>();

	private final Interpreter interpreter;

	private final Parser parser;

	private final OutputBuffer sideeffects;

	private boolean continued;

	// private int genCounter = 0;
	private final Map<String, Integer> gencounter = new HashMap<String, Integer>();

	private final List<IGroovyExecutionListener> listeners = new ArrayList<IGroovyExecutionListener>();

	public synchronized int nextCounter(final String s) {
		int c = gencounter.containsKey(s) ? gencounter.get(s) : 0;
		gencounter.put(s, c + 1);
		return c;
	}

	private String outputs;

	private static final String[] IMPORTS = new String[] {
			"import de.prob.statespace.*;",
			"import de.prob.model.representation.*;",
			"import de.prob.model.classicalb.*;",
			"import de.prob.model.eventb.*;",
			"import de.prob.animator.domainobjects.*;",
			"import de.prob.animator.command.*;",
			"import de.prob.visualization.*" };
	private final ShellCommands shellCommands;

	@Inject
	public GroovyExecution(final Api api, final ShellCommands shellCommands,
			final AnimationSelector selector, final OutputBuffer sideeffects,
			final TestRegistry tests) {
		this.shellCommands = shellCommands;
		this.sideeffects = sideeffects;
		final Binding binding = new Binding();
		binding.setVariable("api", api);
		binding.setVariable("animations", selector);
		binding.setVariable("tests", tests);
		binding.setVariable("__console", sideeffects);
		interpreter = new Interpreter(this.getClass().getClassLoader(), binding);

		imports.addAll(Arrays.asList(GroovyExecution.IMPORTS));
		parser = new Parser();
		runInitScript(Resources.getResource("initscript"));
	}

	public void runInitScript(final URL url) {
		String script = "";
		try {
			script = Resources.toString(url, Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		runSilentScript(script);
	}

	public void registerListener(final IGroovyExecutionListener listener) {
		listeners.add(listener);
	}

	public void notifyListerners() {
		for (IGroovyExecutionListener l : listeners) {
			l.notifyListner(this);
		}
	}

	public String evaluate(final String input) throws IOException {
		try {
			assert input != null;
			final List<String> m = shellCommands.getMagic(input);
			if (m.isEmpty()) {
				return eval(input);
			} else {
				return shellCommands.perform(m, this);
			}
		} finally {
			notifyListerners();
		}
	}

	public synchronized String freshVar(final String prefix) {
		String v;
		Binding bindings = getBindings();
		do {
			v = prefix + nextCounter(prefix);
		} while (bindings.hasVariable(v));
		bindings.setVariable(v, null);
		return v;
	}

	public String runScript(final String content) {
		String s = "script_";
		return runScript(content, s);
	}

	public String runSilentScript(final String content) {
		return runScript(content, null, true);
	}

	public String runScript(final String content, final String prefix) {
		return runScript(content, prefix, false);
	}

	public String runScript(final String content, final String prefix,
			final boolean silent) {
		Object result = runScript2(content);
		if (!silent && result != null) {
			getBindings().setVariable(freshVar(prefix), result);
		}
		return result == null ? "null" : result.toString();
	}

	public Object runScript2(final String content) {
		try {
			final ArrayList<String> eval = new ArrayList<String>();
			eval.addAll(imports);
			eval.add(content);
			Object evaluate = null;
			try {
				evaluate = interpreter.evaluate(eval);
			} catch (final Throwable e) {
				printStackTrace(sideeffects, e);
			} finally {
				inputs.clear();
			}
			return evaluate;
		} finally {
			notifyListerners();
		}
	}

	public Object tryevaluate(final String input) throws IOException {
		final Interpreter tinterpreter = new Interpreter(this.getClass()
				.getClassLoader(), interpreter.getContext());

		assert input != null;
		final ArrayList<String> eval = new ArrayList<String>();
		eval.addAll(imports);
		eval.addAll(Collections.singletonList(input));
		return tinterpreter.evaluate(eval);
	}

	public String[] getImports() {
		final String[] result = new String[imports.size()];
		int c = 0;
		for (final String string : imports) {
			result[c++] = " " + string.substring(7, string.length() - 1).trim();
		}
		return result;
	}

	public Binding getBindings() {
		return interpreter.getContext();
	}

	public String getOutputs() {
		return outputs;
	}

	public boolean isContinued() {
		return continued;
	}

	private void printStackTrace(final OutputBuffer buffer, final Throwable t) {
		String msg = t.toString();

		ArrayList<String> trace = new ArrayList<String>();
		// add each element of the stack trace
		for (StackTraceElement element : t.getStackTrace()) {
			trace.add(element.toString());
		}

		buffer.error(msg, trace);

	}

	private String eval(final String input) {
		Object evaluate = null;
		ParseCode parseCode;
		inputs.add(input);

		final ArrayList<String> eval = new ArrayList<String>();
		eval.addAll(imports);
		eval.addAll(inputs);
		parseCode = parser.parse(eval).getCode();

		if (parseCode.equals(ParseCode.getINCOMPLETE())) {
			continued = true;
			outputs = "";
			return "";
		} else {
			continued = false;
			try {
				evaluate = interpreter.evaluate(eval);
			} catch (final Throwable e) {
				imports.remove(input);
				printStackTrace(sideeffects, e);
			} finally {
				inputs.clear();
			}
			return evaluate == null ? "null" : evaluate.toString();
		}
	}

	public void addImport(final String imp) {
		imports.add(imp);
	}

	@Override
	public void newTransitions(final List<? extends OpInfo> ops) {
		notifyListerners();
	}

	public void reset() {
		continued = false;
		inputs.clear();
	}

}
