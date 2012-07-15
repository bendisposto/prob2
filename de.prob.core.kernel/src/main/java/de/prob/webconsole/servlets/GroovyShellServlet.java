package de.prob.webconsole.servlets;

import groovy.lang.Binding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.groovy.tools.shell.Interpreter;
import org.codehaus.groovy.tools.shell.ParseCode;
import org.codehaus.groovy.tools.shell.Parser;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.scripting.Api;
import de.prob.webconsole.ResultObject;

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
@SuppressWarnings("serial")
@Singleton
public class GroovyShellServlet extends HttpServlet {

	private static final String NL = System.getProperty("line.separator");

	private final ArrayList<String> inputs = new ArrayList<String>();
	private final ArrayList<String> imports = new ArrayList<String>();

	private final Interpreter interpreter;
	private Interpreter try_interpreter;

	private final Parser parser;

	private ByteArrayOutputStream sideeffects;

	@Inject
	public GroovyShellServlet(Api api) {
		Binding binding = new Binding();
		binding.setVariable("api", api);
		this.interpreter = new Interpreter(this.getClass().getClassLoader(),
				binding);
		this.try_interpreter = new Interpreter(
				this.getClass().getClassLoader(), new Binding());
		this.parser = new Parser();
		sideeffects = new ByteArrayOutputStream();
		System.setOut(new PrintStream(sideeffects));
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		String input = req.getParameter("input");
		collectImports(input);
		ResultObject result = eval(input);
		out.println(toJson(result));
		out.close();
	}

	/**
	 * Split the line into different commands and find out, if there was a valid
	 * import statement.
	 * 
	 * @param input
	 */
	private void collectImports(String input) {
		String[] split = input.split(";");
		for (String string : split) {
			if (string.startsWith("import ")) {
				try {
					try_interpreter.evaluate(Collections.singletonList(string));
					imports.add(string + ";"); // if try_interpreter does not
												// throw an exception, it was a
												// valid import statement
				} catch (Exception e) {
					this.try_interpreter = new Interpreter(this.getClass()
							.getClassLoader(), new Binding());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private ResultObject eval(String input) throws IOException {
		ResultObject result = new ResultObject();
		if (input != null) {
			Object evaluate = null;
			ParseCode parseCode;
			inputs.add(input);

			ArrayList<String> eval = new ArrayList<String>();
			eval.addAll(imports);
			eval.addAll(inputs);
			parseCode = parser.parse(eval).getCode();

			if (parseCode.equals(ParseCode.getINCOMPLETE())) {
				result.setContinued(true);
				result.setOutput("");
			} else {
				try {
					HashSet<String> oldBindings = new HashSet<String>();
					oldBindings.addAll(interpreter.getContext().getVariables()
							.keySet());
					evaluate = interpreter.evaluate(eval);
					for (String v : (Set<String>) interpreter.getContext()
							.getVariables().keySet()) {
						if (!oldBindings.contains(v) && !v.startsWith("this")
								&& !v.startsWith("__"))
							result.addBindings(v);
					}

				} catch (Exception e) {
					imports.remove(input);
					sideeffects.write(e.getMessage().getBytes());
				} finally {
					inputs.clear();
				}
				String resultString = "==> "
						+ (evaluate == null ? "null" : evaluate.toString());
				String se = sideeffects.toString();
				if (!se.endsWith(NL) && !se.isEmpty())
					se += NL;
				sideeffects = new ByteArrayOutputStream();
				System.setOut(new PrintStream(sideeffects));
				result.setOutput(se + resultString);
				result.setContinued(false);
				result.setImports(imports);
			}
		}
		return result;
	}

	/**
	 * Converts the ResultObject into a JSON representation
	 * 
	 * @param result
	 * @return
	 */
	private String toJson(ResultObject result) {
		Gson g = new Gson();
		String json = g.toJson(result);
		return json;
	}
}