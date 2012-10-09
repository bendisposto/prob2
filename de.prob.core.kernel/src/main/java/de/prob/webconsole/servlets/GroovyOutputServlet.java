package de.prob.webconsole.servlets;

import java.io.ByteArrayOutputStream;
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
public class GroovyOutputServlet extends HttpServlet {

	private final GroovyExecution executor;

	@Inject
	public GroovyOutputServlet(GroovyExecution executor) {
		this.executor = executor;
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PrintWriter out = res.getWriter();

		ByteArrayOutputStream sideeffects = executor.getSideeffects();
		String outputs = sideeffects.toString();
		executor.renewSideeffects();
		out.println(new Gson().toJson(outputs));
		out.close();
	}

}