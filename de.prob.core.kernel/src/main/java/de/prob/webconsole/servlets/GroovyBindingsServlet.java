package de.prob.webconsole.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.webconsole.GroovyExecution;

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
public class GroovyBindingsServlet extends HttpServlet {

	private static class JSonResult {
		public final String[][] aaData;

		public JSonResult(String[][] aaData) {
			this.aaData = aaData;
		}
	}

	private final GroovyExecution executor;

	@Inject
	public GroovyBindingsServlet(GroovyExecution executor) {
		this.executor = executor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PrintWriter out = res.getWriter();

		Map<String, Object> variables = executor.getBindings().getVariables();
		Set<Entry<String, Object>> vars = variables.entrySet();

		Map<String, Object> filtered = new HashMap<String, Object>();
		for (Entry<String, Object> v : vars) {
			if (!v.getKey().startsWith("this") && !v.getKey().startsWith("__"))
				filtered.put(v.getKey(), v.getValue());
		}

		String[][] result = new String[filtered.size()][3];
		int c = 0;
		for (Entry<String, Object> v : filtered.entrySet()) {
			result[c][0] = v.getKey();
			Object value = v.getValue();
			result[c][1] = value == null ? "---" : value.getClass()
					.getCanonicalName();
			result[c][2] = value == null ? "null" : value.toString();
			c++;
		}

		Gson g = new Gson();
		out.println(g.toJson(new JSonResult(result)));
		out.close();
	}
}