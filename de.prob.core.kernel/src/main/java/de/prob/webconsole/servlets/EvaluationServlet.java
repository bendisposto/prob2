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

@SuppressWarnings("serial")
@Singleton
public class EvaluationServlet extends HttpServlet {

	private static final String NL = System.getProperty("line.separator");

	private final ArrayList<String> inputs = new ArrayList<String>();
	private final ArrayList<String> imports = new ArrayList<String>();

	private final Interpreter interpreter;
	private final Interpreter try_interpreter;

	private final Parser parser;

	private ByteArrayOutputStream sideeffects;

	@Inject
	public EvaluationServlet(Api api) {
		Binding binding = new Binding();
		binding.setVariable("api", api);
		this.interpreter = new Interpreter(this.getClass().getClassLoader(),
				binding);
		this.try_interpreter = new Interpreter(
				this.getClass().getClassLoader(), binding);
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

		eval(out, input);
	}

	private void collectImports(String input) {
		String[] split = input.split(";");
		for (String string : split) {
			if (string.startsWith("import ")) {
				try {
					try_interpreter.evaluate(Collections.singletonList(string));
					imports.add(string + ";");
				} catch (Exception e) {
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void eval(PrintWriter out, String input) throws IOException {
		if (input == null) {
			return;
		} else {
			ResultObject result = new ResultObject();
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
					oldBindings.addAll(interpreter.getContext().getVariables().keySet());
					evaluate = interpreter.evaluate(eval);
					for (String v : (Set<String>) interpreter.getContext().getVariables()
							.keySet()) {
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

			sendResult(out, result);
		}
	}

	private void sendResult(PrintWriter out, ResultObject result) {
		Gson g = new Gson();
		String json = g.toJson(result);
		out.println(json);
		out.close();
	}
}