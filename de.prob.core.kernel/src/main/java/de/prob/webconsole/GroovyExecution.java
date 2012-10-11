package de.prob.webconsole;

import groovy.lang.Binding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.tools.shell.Interpreter;
import org.codehaus.groovy.tools.shell.ParseCode;
import org.codehaus.groovy.tools.shell.Parser;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.scripting.Api;
import de.prob.statespace.AnimationSelector;

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
public class GroovyExecution {

	private final ArrayList<String> inputs = new ArrayList<String>();
	private final ArrayList<String> imports = new ArrayList<String>();

	private final Interpreter interpreter;
	private Interpreter try_interpreter;

	private final Parser parser;

	private ByteArrayOutputStream sideeffects;

	private boolean continued;

	private int genCounter = 0;

	public int nextCounter() {
		return genCounter++;
	}

	private String outputs;

	private static final String[] IMPORTS = new String[] { "import de.prob.statespace.*;" };
	private final ShellCommands shellCommands;

	@Inject
	public GroovyExecution(final Api api, final ShellCommands shellCommands,
			final AnimationSelector selector) {
		this.shellCommands = shellCommands;
		final Binding binding = new Binding();
		binding.setVariable("api", api);
		this.interpreter = new Interpreter(this.getClass().getClassLoader(),
				binding);

		imports.addAll(Arrays.asList(IMPORTS));

		this.try_interpreter = new Interpreter(
				this.getClass().getClassLoader(), new Binding());
		this.parser = new Parser();
		sideeffects = new ByteArrayOutputStream();
		System.setOut(new PrintStream(sideeffects));
	}

	public String evaluate(final String input) throws IOException {
		assert input != null;
		final List<String> m = shellCommands.getMagic(input);
		if (m.isEmpty()) {
			collectImports(input);
			return eval(input);
		} else {
			return shellCommands.perform(m, this);
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

	public ByteArrayOutputStream getSideeffects() {
		return sideeffects;
	}

	/**
	 * Split the line into different commands and find out, if there was a valid
	 * import statement.
	 * 
	 * @param input
	 */
	private void collectImports(final String input) {
		final String[] split = input.split(";|\n");
		for (final String string : split) {
			if (string.trim().startsWith("import ")) {
				try {
					try_interpreter.evaluate(Collections.singletonList(string));
					imports.add(string + ";"); // if try_interpreter does not
												// throw an exception, it was a
												// valid import statement
				} catch (final Exception e) {
					this.try_interpreter = new Interpreter(this.getClass()
							.getClassLoader(), new Binding());
				}
			}
		}
	}

	private String eval(final String input) throws IOException {
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
				String message = e.getMessage();
				if (message == null && e.getCause() != null)
					message = e.getCause().getMessage();
				if (message != null)
					sideeffects.write(message.getBytes());
				else
					e.printStackTrace(System.out);
			} finally {
				inputs.clear();
			}
			while (sideeffects.size() > 0) {
			}
			return evaluate == null ? "null" : evaluate.toString();
		}
	}

	public void renewSideeffects() {
		sideeffects = new ByteArrayOutputStream();
		System.setOut(new PrintStream(sideeffects));
	}

}