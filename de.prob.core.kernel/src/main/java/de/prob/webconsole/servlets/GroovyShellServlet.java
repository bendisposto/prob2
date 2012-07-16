package de.prob.webconsole.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.webconsole.GroovyExecution;
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

	private final GroovyExecution executor;

	@Inject
	public GroovyShellServlet(GroovyExecution executor) {
		this.executor = executor;
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		String input = req.getParameter("input");
		String result = executor.evaluate(input);

		ResultObject r = new ResultObject(executor.getOutputs() + result,
				executor.isContinued(), executor.getImports());

		out.println(toJson(r));
		out.close();
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